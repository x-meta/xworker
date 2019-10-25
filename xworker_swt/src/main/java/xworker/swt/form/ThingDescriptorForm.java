/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.swt.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ThingRegistor;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import xworker.swt.ActionContainer;
import xworker.swt.design.Designer;
import xworker.swt.util.DialogCallback;
import xworker.swt.util.PoolableControlFactory;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.UtilSwt;
import xworker.swt.xworker.attributeEditor.AttributeEditorFactory;

/**
 * 事物表单使用新的动作上下文，原有的上下文使用parentContext变量保存。
 * 
 * @author zyx
 *
 */
public class ThingDescriptorForm {
	private static Logger log = LoggerFactory.getLogger(ThingDescriptorForm.class);
	
	/**
	 * 指定要编辑的事物和表单包含于的Composite创建一个事物编辑表单。
	 * 
	 * @param thing
	 * @param parent
	 * @return
	 */
	public static Composite createForm(Thing thing, Composite parent){
		ActionContext actionContext = new ActionContext();
		actionContext.setLabel("Thing form context");
		actionContext.put("thing", thing);
		actionContext.put("thingAttributes", thing.getAttributes());
		actionContext.put("parent", parent);		
		return createForm(actionContext);
	}
	
	public static boolean hasFocus(Control control) {
		if(control.isFocusControl()) {
			return true;
		}
		
		if(control instanceof Composite) {
			Composite com = (Composite) control;
			for(Control child : com.getChildren()) {
				if(hasFocus(child)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static void createAttributeModel(Thing model, Thing field, ActionContext editorActionContext) {
		//是否是自定义了Model
		Thing fModel = field.getThing("SwtObject@0/inputModel@0");
		if(fModel != null){
			model.addChild(fModel, false);
			return;
		}
		
		//通过编辑器生成Model，如果编辑器没有生成Model，建立默认的Model
		Thing fEditor = field.getThing("SwtObject@0/inputEditor@0");
		String inputType = field.getString("inputtype");			
    	if(fEditor == null){
    		Thing editorConfig = (Thing) ThingRegistor.get("_xworker_thing_attribute_editor_config");
    		if(editorConfig != null){
	    		Thing cfg = editorConfig.getThing("@" + inputType);
	    		if(cfg != null){
	    			fEditor = World.getInstance().getThing(cfg.getString("default"));
	    		}
    		}
    	}
    	
    	//是否通过扩展属性自定
    	if(fEditor == null){
    		if("inputAttrDefined".equals(inputType)){
    			fEditor = World.getInstance().getThing(inputType);
			}
    	}

		if(fEditor != null){
			Bindings bindings = editorActionContext.push();
			try{
				bindings.put("attribute", field);
				Object editModel = fEditor.doAction("createModel", editorActionContext);
				if(editModel != null && editModel instanceof Thing){
					fModel = (Thing) editModel;
					model.addChild(fModel, false);
					return;
				}
			}finally{
				editorActionContext.pop();
			}
		}
		
	    String fieldName = field.getMetadata().getName();
	    String inputName = fieldName + "Input";
        Thing cmodel = new Thing();
        cmodel.set("name", fieldName);	 
        String type = field.getString("type");
        if(type == null || "".equals(type)){
        	type = "String";
        }else{
        	type = UtilString.capFirst(type);
        }
        if("Int".equals(type)){
        	type = "Integer";
        }
        cmodel.set("type", type);
        cmodel.set("dataType", type);
        String pattern = field.getString("pattern");
        if(pattern == null || "".equals(pattern)){
        	pattern = field.getString("editPattern");
        }
        cmodel.set("pattern", pattern);
        cmodel.set("editPattern", field.getString("editPattern"));	        
        cmodel.set("validateAllowBlank", field.getString("validateAllowBlank"));
        cmodel.set("blankText", field.getString("blankText"));
        cmodel.set("invalidClass", field.getString("invalidClass"));
        cmodel.set("invalidText", field.getString("invalidText"));
        cmodel.set("validateOnBlur", field.getString("validateOnBlur"));
        cmodel.set("validationDelay", field.getString("validationDelay"));
        cmodel.set("maxLength", field.getString("maxLength"));
        cmodel.set("minLength", field.getString("minLength"));
        cmodel.set("regex", field.getString("regex"));
        cmodel.set("regexText", field.getString("regexText"));
        cmodel.set("allowDecimals", field.getString("allowDecimals"));
        cmodel.set("allowNegative", field.getString("allowNegative"));
        cmodel.set("maxValue", field.getString("maxValue"));
        cmodel.set("minValue", field.getString("minValue"));
        cmodel.set("validationAction", field.getString("validationAction"));
        cmodel.set("swtName", inputName);
        String propertyName = field.getString("propertyName");
        if(propertyName == null || "".equals(propertyName)){
        	propertyName = fieldName;
        }
        cmodel.set("propertyName", propertyName);
        cmodel.set("swtControl", inputName);
        if(field.getBoolean("readOnly")){
        	cmodel.set("focusColor", "#C0C0C0");
        	cmodel.set("background", "#C0C0C0");
        }
        Object defaultValue = field.getAttribute("default");
        if(defaultValue == null){
        	defaultValue = field.getAttribute("defaultValue");
        }
        cmodel.set("defaultValue", defaultValue);
        cmodel.set("descriptors", "xworker.swt.model.Model");	 
        
        cmodel.initDefaultValue();
        /*
        if(tempIndex == 0){
        	//cmodel.set("focus", "true");
        	tempIndex ++;
        }*/
        
        model.addChild(cmodel);		
	}
	
	/**
	 * 强制创建一个单行输入的表单，在传入的上下文上创建表单，还可以传入utilBrowser对象。
	 * 
	 * @param thing
	 * @param descriptor
	 * @param defaultSelection
	 * @param defaultModifyListener
	 * @param parent
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Composite createThingSingleColumnForm(Thing thing, Thing descriptor, String defaultSelection, String defaultModifyListener, Composite parent, ActionContext context){
		//long start = System.currentTimeMillis();
		Thing self = thing;

		int style = SWT.NONE;		        				
		Composite composite = new Composite(parent, style);
		
		FillLayout fillLaout = new FillLayout();
		composite.setLayout(fillLaout);				
		int numColumns = 2;
		
		//编辑器的动作上下文，表单中的控件可以访问的当前动作上下文
		ActionContext editorActionContext = context;
		composite.setData("actionContext", editorActionContext);
		composite.setData(editorActionContext);
		
		//查看描述者是否自定了编辑表单		
		Thing swtEditor = descriptor.getThing("SwtEditSetting@0/SwtEditor@0");
		/**
		 * 取消ThingGuide，2017-07-13，原有方法侵入了事物的属性
		if(swtEditor == null && "xworker.lang.ThingGuide".equals(descriptor.getMetadata().getPath())){
			//使用向导
			String guidePath = thing.getStringBlankAsNull("tg_thingGuidePath");
			swtEditor = World.getInstance().getThing(guidePath);
		}*/
		
		if(swtEditor != null){
			Composite mainComposite = new Composite(composite, SWT.NONE);			
			mainComposite.setLayout(new FillLayout());		
			
			editorActionContext.put("parent", mainComposite);
			swtEditor.doAction("create", editorActionContext);
			Object modelObj = swtEditor.doAction("createModel", editorActionContext);
			if(modelObj == null){
				modelObj = new Thing();
			}
			if(modelObj instanceof Thing){
				editorActionContext.getScope(0).put(self.getAttribute("name") + "Model", modelObj);
			}else{
				editorActionContext.getScope(0).put(self.getAttribute("name") + "Model", new Thing());
			}
			
			mainComposite.pack();
			composite.layout();
			return composite;
		}
		
        // 建立面板
		Composite mainComposite = new Composite(composite, SWT.NONE);
		GridLayout mainCompositeGridLayout = new GridLayout();
		mainCompositeGridLayout.numColumns = numColumns;
		mainComposite.setLayout(mainCompositeGridLayout);
		if(self.getMetadata().getPath() != null) {
			Designer.attach(composite, self.getMetadata().getPath(), context, true);
		}

		GC gc = new GC(mainComposite);
		FontMetrics fontMetrics = gc.getFontMetrics();
		int width = fontMetrics.getAverageCharWidth();
		int _width = width;
		int _height = fontMetrics.getHeight() + 8;
		editorActionContext.put("_widthHint", _width);
		editorActionContext.put("_heightHint", _height);

		//创建model
		Thing model = new Thing();
		model.set("type", "Map");
		model.set("dataType", "Map");
		//model.set("defaultSelection", defaultSelection);
		//model.set("defaultModify", defaultModifyListener);
		model.set("descriptors", ThingRegistor.getPath("_xworker.swt_model"));//_xworker_ui_local_swt_model"));
		model.set("dataName", "thingAttributes");
		model.set("dataSource", "thingAttributes");
		editorActionContext.put("thingAttributes", thing.getAttributes());
		
		editorActionContext.getScope(0).put("__editModel__model", model);
		editorActionContext.getScope(0).put("model", model); //以上变量重复，历史原因
		
		List<Thing> fs = new ArrayList<Thing>();
				
		//过滤相同名称的属性
		for(Thing f : descriptor.getAllChilds("attribute")){
			if(!isPublic(f)) {
				continue;
			}
			
			boolean have = false;
			for(Thing ff : fs){
				if(f.getMetadata().getName().equals(ff.getMetadata().getName())){
					have = true; 
					break;
				}
			}
			
			if(!have){
				fs.add(f);
			}
		}
		
		for(Iterator<Thing> iter = fs.iterator(); iter.hasNext();){
			Thing field = iter.next();		
			
			createAttributeModel(model, field, editorActionContext);
		}
		
		//去掉隐藏字段
		for(int i=0; i<fs.size();i++){
			Thing f = fs.get(i);
			if("hidden".equals(f.getString("inputtype"))){
				editorActionContext.put(f.getMetadata().getName() + "Input", new HiddenInput());
				fs.remove(i);
				i--;
	        }
		}
		
		//字段的分组
		List<Thing> rootList = new ArrayList<Thing>();
		List<Map<String, Object>> groups = createAttributeGroups(rootList, fs);		
		//System.out.println("创建Model: " + (System.currentTimeMillis() - start));
		//start = System.currentTimeMillis();
		
		if(groups.size() != 0){			
			int tabFolderStyle = SWT.TOP | SWT.FLAT;
			//tabFolderStyle |= SWT.BORDER;
			CTabFolder tabFolder = new CTabFolder(mainComposite, tabFolderStyle);
			GridData tabGridData = new GridData(GridData.FILL_BOTH);
			tabGridData.horizontalSpan = 2;
			tabFolder.setLayoutData(tabGridData);
			
			if(rootList.size() > 0){	
				CTabItem baseItem = new CTabItem(tabFolder, SWT.NONE);
				baseItem.setText(UtilString.getString("res:res.w_exp:baseAttribute:基础属性", context));
				ScrolledComposite itemComposite = ThingDescriptorForm.createScrolledForm(tabFolder, rootList, 
						descriptor, numColumns, _width, _height, _width, editorActionContext);
				baseItem.setControl(itemComposite);
			}
			
			for(int i=0; i<groups.size(); i++){
				CTabItem item = new CTabItem(tabFolder, SWT.NONE);
				item.setText((String) groups.get(i).get("name"));
				ScrolledComposite itemComposite = ThingDescriptorForm.createScrolledForm(tabFolder, (List<Thing>) groups.get(i).get("childs"), 
						descriptor, numColumns, _width, _height, _width, editorActionContext);
				item.setControl(itemComposite);
			}
			tabFolder.setSelection(0);			
		}else{
			ScrolledComposite itemComposite = ThingDescriptorForm.createScrolledForm(mainComposite, rootList,	descriptor, numColumns, _width, _height, _width, editorActionContext);
			GridData itemCompositeGridData = new GridData(GridData.FILL_BOTH);
			itemCompositeGridData.horizontalSpan = 2;
			itemComposite.setLayoutData(itemCompositeGridData);
			mainComposite.layout();
		}
		
		//System.out.println("创建字段: " + (System.currentTimeMillis() - start));
		//start = System.currentTimeMillis();
		
		Thing editorExtends = descriptor.getThing("SwtEditSetting@0/SwtEditorExtends@0");
		if(editorExtends != null){
			Bindings bindings = editorActionContext.push(null);
			bindings.put("parent", mainComposite);
			
			try{
				for(Thing child : editorExtends.getAllChilds()){
					child.doAction("create", editorActionContext);
				}
			}finally{
				editorActionContext.pop();
			}
		}
		
		List<Thing> swts = descriptor.getChilds("SwtObject");
		Bindings bindings = editorActionContext.push(null);
		bindings.put("parent", mainComposite);
	    
		try{
		    for(Thing swt : swts){
		    	try{
		    		swt.doAction("create", editorActionContext);
		    	}catch(Exception eee){
		    		log.error("create structure extend swt obj", eee);
		    	}
		    }
		}finally{
			editorActionContext.pop();
		}
		
	    
		model.doAction("init", editorActionContext);
		model.doAction("setValue", editorActionContext);
		
		//System.out.println("设置值: " + (System.currentTimeMillis() - start));
		//start = System.currentTimeMillis();
		parent.layout();
		return composite;
	}
	
	private static ScrolledComposite createScrolledForm(Composite parent, List<Thing> fields, Thing descriptor, int numColumns, int _width, int _height, int width, ActionContext  editorActionContext) {
		ScrolledComposite itemComposite = new ScrolledComposite(parent, SWT.NONE | SWT.H_SCROLL | SWT.V_SCROLL);
		itemComposite.setExpandHorizontal(true);
		itemComposite.setExpandVertical(true);
		itemComposite.setLayout(new FillLayout());	
		if(SwtUtils.isRWT()) {
			itemComposite.addListener(SWT.MouseWheel,  new Listener() {
				@Override
				public void handleEvent(Event event) {
					event.count = 70 * event.count;
				}
				
			});
		}else {
			itemComposite.addListener(SWT.MouseVerticalWheel, new Listener() {
				@Override
				public void handleEvent(Event event) {
					event.count = 70 * event.count;
				}
				
			});
		}
		itemComposite.addListener(SWT.Activate, new Listener() {
		    public void handleEvent(Event e) {
		    	//ScrolledComposite sc = (ScrolledComposite) e.widget;	
		    	//if(!hasFocus(sc)) {
		    	////	sc.setFocus();
		    	//}
		    }
		}); 
		
		Composite baseComposite = new Composite(itemComposite, SWT.NONE);
		GridLayout baseCompositeGridLayout = new GridLayout();
		baseCompositeGridLayout.numColumns = 2;
		baseComposite.setLayout(baseCompositeGridLayout);
		
		//List<Thing> fields = (List<Thing>) groups.get(i).get("childs");
		createInput(descriptor, fields, baseComposite, numColumns, _width, _height, width, editorActionContext);
		//baseComposite.layout();
		baseComposite.layout();
		baseComposite.setSize(baseComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		itemComposite.setContent(baseComposite);
		itemComposite.setMinSize(baseComposite.getSize());
		itemComposite.layout();

		return itemComposite;
	}
	
	/**
	 * 默认的创建事物的表单，编辑列数从描述者上取。
	 * 
	 * @param context
	 * @return
	 */
	public static Composite createForm(ActionContext context){	
		return createForm(context, -1);
	}
	
	public static List<Map<String, Object>> createAttributeGroups(List<Thing> rootList, List<Thing> fs){		
		List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
		for(Thing f : fs){
			String group = f.getString("group");
		    int groupIndex = f.getInt("groupIndex", 0);
		    if(group == null ||"".equals(group)){
		    	rootList.add(f);		        
		    }else{
		    	Map<String, Object> gp = null;
		        for(Map<String, Object> g : groups){
		        	if(g.get("name").equals(group)){
		        		gp = g;
		        		break;
		        	}
		        }
		        
		        if(gp == null){
		        	gp = new HashMap<String, Object>();
		        	gp.put("name", group);
		        	gp.put("groupIndex", groupIndex);
		        	gp.put("childs", new ArrayList<Thing>());
		        	groups.add(gp);
		        }
		        
		        ((List<Thing>) gp.get("childs")).add(f);
		    }
		}
		//字段分组排序
		Collections.sort(groups, new Comparator<Map<String, Object>>(){
			public int compare(Map<String, Object> m1, Map<String, Object> m2) {
				Integer i1 = (Integer) m1.get("groupIndex");
				Integer i2 = (Integer) m2.get("groupIndex");
				
				if(i1 < i2){
					return -1;
				}else if(i1.equals(i2)){
					return 0;
				}else{
					return 1;
				}
			}
			
		});
		
		return groups;
				
	}
	/**
	 * 表单事物的create方法，必须是表单事物的自己的调用，如thingForm。
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Composite createForm(ActionContext context, int column){			
		World world = World.getInstance();		
		
		Thing self = (Thing) context.get("self");		
		if(self == null) {
			if(log.isInfoEnabled()){
				log.info("必须以事物的行为方式运行");
			}
			return null;
		}		
		
        // 初始化结构数据对象
		Thing structObject = world.getThing(self.getString("descriptorPath"));		
		if(structObject == null){
			structObject = self.getThing("descriptorThing@0");
		}
		if(structObject == null) {
			if(log.isInfoEnabled()){
				log.info("没有设定事物描述或指定的事物描述路径不存在");
			}
			return null;
		}
				
		Composite parent = null;
		Object parentObj = context.get("parent");
		if(parentObj == null || !(parentObj instanceof Composite)){
			if(log.isInfoEnabled()){
				log.info("没有指定父SWT对象");
			}
		}else{
			parent = (Composite) parentObj;
		}
		
		int style = SWT.NONE;
		if(self.getBoolean("BORDER"))
		    style |= SWT.BORDER;		
		if(self.getBoolean("H_SCROLL"))
		    style |= SWT.H_SCROLL;
		if(self.getBoolean("V_SCROLL"))
		    style |= SWT.V_SCROLL;
		if(self.getBoolean("NO_BACKGROUND"))
		    style |= SWT.NO_BACKGROUND;        
		if(self.getBoolean("NO_FOCUS"))
		    style |= SWT.NO_FOCUS;        
		if(self.getBoolean("NO_MERGE_PAINTS"))
		    style |= SWT.NO_MERGE_PAINTS;        
		if(self.getBoolean("NO_REDRAW_RESIZE"))
		    style |= SWT.NO_REDRAW_RESIZE;        
		if(self.getBoolean("NO_RADIO_GROUP"))
		    style |= SWT.NO_RADIO_GROUP;        
		if(self.getBoolean("EMBEDDED"))
		    style |= SWT.EMBEDDED;        
		if(self.getBoolean("DOUBLE_BUFFERED"))
		    style |= SWT.DOUBLE_BUFFERED;
		        				
		ScrolledComposite composite = new ScrolledComposite(parent, style);
		composite.setExpandHorizontal(true);
		composite.setExpandVertical(true);
		
		FillLayout fillLaout = new FillLayout();
		composite.setLayout(fillLaout);				
		int numColumns = column == -1 ? structObject.getInt("editCols", 2) * 2 : column * 2;
		if(numColumns < 1) {
			numColumns = 2;
		}
		
		/*
		try {
			composite.addMouseWheelListener(new MouseWheelListener(){
				public void mouseScrolled(MouseEvent event) {				
					//System.out.println(event.button + " " + event.count + " " + event.y);
					event.count = 10 * event.count;
				}			
			});
		}catch(Throwable t) {
			//Eclipse RAP不支持该方法
		}*/
		composite.addListener(SWT.MouseWheel, new Listener() {
		    public void handleEvent(Event event) {
		    	event.count = 10 * event.count;
		    }
		});
		composite.addListener(SWT.Activate, new Listener() {
		    public void handleEvent(Event e) {
		    	//ScrolledComposite sc = (ScrolledComposite) e.widget;
		    	//if(!hasFocus(sc)) {
		    	//	sc.setFocus();
		    	//}
		    }
		}); 		
		
		//编辑器的动作上下文，表单中的控件可以访问的当前动作上下文
		ActionContext editorActionContext = new ActionContext();
		editorActionContext.setLabel("Thing inner form");
		editorActionContext.put("parentContext", context);
		editorActionContext.put("parentActionContext", context);
		editorActionContext.put("modifyListener", context.get("modifyListener"));
		editorActionContext.put("utilBrowser", context.get("utilBrowser"));
		editorActionContext.put("explorerActions", context.get("explorerActions"));
		editorActionContext.put("explorerContext", context.get("explorerContext"));
		editorActionContext.put("thingContext", context.get("thingContext"));
		editorActionContext.put("parent", context.get("parent"));
		editorActionContext.put("descriptor", structObject);//用于生成界面的描述者
		editorActionContext.put("okButtonSelection", context.get("okButtonSelection")); //历史遗留
		editorActionContext.put("thing", context.get("thing"));
		editorActionContext.put("thingAttributes", context.get("thingAttributes"));
		editorActionContext.put("defaultSelection", context.get("defaultSelection"));
		editorActionContext.put("defaultModify", context.get("defaultModify"));
		editorActionContext.put("params", context.get(self.getString("baseParams")));
		context.getScope(0).put(self.getMetadata().getName(), editorActionContext);
		
		composite.setData("actionContext", editorActionContext);
		composite.setData(editorActionContext);
		
		//查看描述者是否自定了编辑表单		
		Thing swtEditor = structObject.getThing("SwtEditSetting@0/SwtEditor@0");
		if(swtEditor != null && swtEditor.getChilds().size() >= 2){
			Composite mainComposite = new Composite(composite, SWT.NONE);			
			mainComposite.setLayout(new FillLayout());		
						
			editorActionContext.put("parent", mainComposite);
			
			//第一个子节点认为是编辑器控件节点
			swtEditor.getChilds().get(0).doAction("create", editorActionContext);
			
			//第二个子节点认为是数据模型节点
			Object modelObj = swtEditor.getChilds().get(1).doAction("create", editorActionContext);
			if(modelObj instanceof Thing){
				editorActionContext.getScope(0).put(self.getAttribute("name") + "Model", modelObj);
			}else{
				editorActionContext.getScope(0).put(self.getAttribute("name") + "Model", new Thing());
			}
			
			mainComposite.layout();
			composite.setContent(mainComposite);
			composite.setMinSize(mainComposite.getSize());
			composite.layout();
			return composite;
		}
		
        // 建立面板
		Composite mainComposite = new Composite(composite, SWT.NONE);
		//SwtBorder.attach(mainComposite);
		GridLayout mainCompositeGridLayout = new GridLayout();
		mainCompositeGridLayout.numColumns = numColumns;
		mainComposite.setLayout(mainCompositeGridLayout);
		Designer.attach(composite, self.getMetadata().getPath(), context, true);
		//mainComposite.setFont(Resources.getFont("default"));

		GC gc = new GC(mainComposite);
		FontMetrics fontMetrics = gc.getFontMetrics();
		int width = fontMetrics.getAverageCharWidth();
		int _width = width;
		int _height = fontMetrics.getHeight() + 8;
		editorActionContext.put("_widthHint", _width);
		editorActionContext.put("_heightHint", _height);

		//创建model
		Thing model = new Thing();
		model.set("type", "Map");
		model.set("dataType", "Map");
		//model.set("defaultSelection", "defaultSelection");//self.getString("defaultSelection"));
		//model.set("defaultModify", "defaultModify");//self.getString("defaultModify"));
		model.set("descriptors", ThingRegistor.getPath("_xworker.swt_model"));//_xworker_ui_local_swt_model"));
		String dataName = self.getString("dataName");
		if(dataName != null && !"".equals(dataName)){
			model.set("dataName", dataName);
			model.set("dataSource", dataName);
		}else{
			model.set("dataName", "thingAttributes");
			model.set("dataSource", "thingAttributes");
		}	
		editorActionContext.getScope(0).put(self.getAttribute("name") + "Model", model);
		editorActionContext.getScope(0).put("__editModel__model", model);
		editorActionContext.getScope(0).put("model", model); //以上变量重复，历史原因
		
		List<Thing> fs = new ArrayList<Thing>();
				
		//过滤相同名称的属性
		for(Thing f : structObject.getAllChilds("attribute")){
			//String inputType = f.getString("inputtype");
			//if("hidden".equals(inputType)){ //Hidden使用HiddenInput,2012-01-01
				//隐藏输入的等于不需要输入
				//continue;
			//}
			if(!isPublic(f)) {
				continue;
			}
			
			boolean have = false;
			for(Thing ff : fs){
				if(f.getMetadata().getName().equals(ff.getMetadata().getName())){
					have = true; 
					break;
				}
			}
			
			if(!have){
				fs.add(f);
			}
		}
		
		for(Iterator<Thing> iter = fs.iterator(); iter.hasNext();){
			Thing field = iter.next();		
			createAttributeModel(model, field, editorActionContext);
		}
		
		//去掉隐藏字段
		for(int i=0; i<fs.size();i++){
			Thing f = fs.get(i);
			if("hidden".equals(f.getString("inputtype"))){
				editorActionContext.put(f.getMetadata().getName() + "Input", new HiddenInput());
				fs.remove(i);
				i--;
	        }
		}
		
		//字段的分组
		List<Thing> rootList = new ArrayList<Thing>();
		List<Map<String, Object>> groups = createAttributeGroups(rootList, fs);
		
		if(groups.size() != 0){			
			int tabFolderStyle = SWT.TOP;
			//tabFolderStyle |= SWT.BORDER;
			TabFolder tabFolder = new TabFolder(mainComposite, tabFolderStyle);
			GridData tabGridData = new GridData(GridData.FILL_BOTH);
			tabGridData.horizontalSpan = numColumns;
			tabFolder.setLayoutData(tabGridData);
			
			if(rootList.size() > 0){	
				TabItem baseItem = new TabItem(tabFolder, SWT.NONE);
				baseItem.setText(UtilString.getString("res:res.w_exp:baseAttribute:基础属性", context));
				Composite baseComposite = new Composite(tabFolder, SWT.NONE);
				GridLayout baseCompositeGridLayout = new GridLayout();
				baseCompositeGridLayout.numColumns = numColumns;
				baseComposite.setLayout(baseCompositeGridLayout);
								
				createInput(structObject, rootList, baseComposite, numColumns, _width, _height, width, editorActionContext);
				baseItem.setControl(baseComposite);
			}
			
			for(int i=0; i<groups.size(); i++){
				TabItem item = new TabItem(tabFolder, SWT.NONE);
				item.setText((String) groups.get(i).get("name"));
				Composite itemComposite = new Composite(tabFolder, SWT.NONE);
				GridLayout itemCompositeGridLayout = new GridLayout();
				itemCompositeGridLayout.numColumns = numColumns;
				itemComposite.setLayout(itemCompositeGridLayout);
				//itemComposite.setFont(Resources.getFont("default"));
				
				/*Label groupLabel1 = new Label(itemComposite, SWT.NONE);
				groupLabel1.setText((String) groups.get(i).get("name"));
				GridData ggGridData = new GridData(GridData.FILL_HORIZONTAL);
				ggGridData.horizontalSpan = numColumns;
				groupLabel1.setLayoutData(ggGridData);
				groupLabel1.setFont(groupFont);
				groupLabel1.addPaintListener(groupPaintListener);*/
				
				List<Thing> fields = (List<Thing>) groups.get(i).get("childs");
				createInput(structObject, fields, itemComposite, numColumns, _width, _height, width, editorActionContext);
				
				item.setControl(itemComposite);
			}  
		}else{
			createInput(structObject, rootList, mainComposite, numColumns, _width, _height, width, editorActionContext);			
		}
		
		Thing editorExtends = structObject.getThing("SwtEditSetting@0/SwtEditorExtends@0");
		if(editorExtends != null){
			Bindings bindings = editorActionContext.push(null);
			bindings.put("parent", mainComposite);
			
			try{
				for(Thing child : editorExtends.getAllChilds()){
					child.doAction("create", editorActionContext);
				}
			}finally{
				editorActionContext.pop();
			}
		}
		
		mainComposite.pack();
		composite.setContent(mainComposite);
		mainComposite.setSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		List<Thing> swts = structObject.getChilds("SwtObject");
		
		
		Bindings bindings = editorActionContext.push(null);
		bindings.put("parent", mainComposite);
	    
		try{
		    for(Thing swt : swts){
		    	try{
		    		swt.doAction("create", editorActionContext);
		    	}catch(Exception eee){
		    		log.error("create structure extend swt obj", eee);
		    	}
		    }
		}finally{
			editorActionContext.pop();
		}
		
	    
		model.doAction("init", editorActionContext);
		composite.setMinSize(mainComposite.getSize());
		
		return composite;
	}
	
	public static boolean isPublic(Thing attribute) {
		return !"private".equals(attribute.getString("modifier"));
	}
	
	public static void createInput(Thing formThing, List<Thing> fs, Composite mainComposite, int numColumns, int _width, int _height, int width, ActionContext context){
		List<AttributeEditorBind> binds = new ArrayList<AttributeEditorBind>();
		context.put("_binds", binds);
		
		//浏览器地址
		//String webUrl = XWorkerUtils.getWebUrl();			
				
		ModifyListener parentModifyListener = null;
		if(context.get("modifyListener") != null && context.get("modifyListener") instanceof ModifyListener){
			parentModifyListener = (ModifyListener) context.get("modifyListener");
		}
		
		//移除编辑方式为none的，none表示没有编辑控件
		for(int i=0; i<fs.size(); i++) {
			Thing attribute = fs.get(i);
			if("none".equals(attribute.getString("inputtype"))) {
				fs.remove(i);
				i--;
			}
		}
		
		//默认选择事件
		Listener defaultSelection = context.getObject("defaultSelection");
		
		List<List<xworker.swt.form.GridData>> rows = xworker.swt.form.GridLayout.layout(fs, (int) (numColumns / 2));
		for(int i=0; i<rows.size(); i++){
			List<xworker.swt.form.GridData> row = rows.get(i);
			
		    for(int n=0; n<row.size(); n++){
		    	//long start = System.currentTimeMillis();
		    	//输入字段的标签		 
		    	xworker.swt.form.GridData xgridData = row.get(n);
		    	Thing f = (Thing) xgridData.source;
		    	
		    	FormModifyListener modifyListener = new FormModifyListener(formThing, f, context, parentModifyListener); //数据关联监听器
		    	xworker.swt.xworker.attributeEditor.AttributeEditor editor = 
		    			AttributeEditorFactory.createAttributeEditor(formThing, f, xgridData, context);
		    	if(editor != null) {
		    		editor.setWidthHeight(_width, _height);
		    		editor.create(mainComposite, modifyListener, defaultSelection);
		    	}
		    }
		}
		
		//执行绑定
		for(AttributeEditorBind bind : binds){
			bind.bind(context);
		}
	}
	
	public static int getColspan(int colspan){
	    if(colspan <= 1) return 1;
	    
	    return colspan * 2 - 1;
	}
	
	public static class FontListener implements SelectionListener{
		Text text;
		
		public FontListener(Text text){
			this.text = text;
		}

		public void widgetDefaultSelected(SelectionEvent arg0) {			
		}

		public void widgetSelected(SelectionEvent event) {
			final FontDialog dialog = new FontDialog(text.getShell());
			FontData fontData = UtilSwt.parseFontData(text.getText());
			if(fontData != null){
				dialog.setFontList(new FontData[]{fontData});
			}
			
			RGB rgb = UtilSwt.parseFontRGB(text.getText());
			if(rgb != null){
				dialog.setRGB(rgb);
			}

			if(SwtUtils.isRWT()) {
				SwtUtils.openDialog(dialog, new DialogCallback() {
					@Override
					public void dialogClosed(int returnCode) {
						FontData[] list = dialog.getFontList();
						if(list != null && list.length > 0){
							FontData fontData = list[0];
							String fontStr = fontData.getName() + "|" + fontData.getHeight() + "|" + fontData.getStyle();
							if(dialog.getRGB() != null){
								fontStr = fontStr + "|" + UtilSwt.RGBToString(dialog.getRGB());
							}
							//fontStr = fontStr;
							text.setText(fontStr);
							///UtilSwt.setFont(text, text.getText(), null);
						}
						text.setFocus();
					}
					
				}, null);
			}else {
				fontData = dialog.open();
				if(fontData != null){
					String fontStr = fontData.getName() + "|" + fontData.getHeight() + "|" + fontData.getStyle();
					if(dialog.getRGB() != null){
						fontStr = fontStr + "|" + UtilSwt.RGBToString(dialog.getRGB());
					}
					//fontStr = fontStr;
					text.setText(fontStr);
					///UtilSwt.setFont(text, text.getText(), null);
				}
				text.setFocus();
			}
		}
	}
	
	/**
	 * 获取属性上定义的数据仓库。
	 * 
	 * @param attribute
	 * @return
	 */
	public static Thing getDataStoreThing(Thing attribute){
		//是否是本地
		World world = World.getInstance();
		String inputAttrs = attribute.getString("inputattrs");        
        Thing dataStoreThing = null;
        if(inputAttrs != null && !"".equals(inputAttrs)){
        	String params[] = inputAttrs.split("[,]");
        	//第一个是数据仓库的地址
        	dataStoreThing = world.getThing(params[0]);
        }
        
        if(dataStoreThing == null){
        	//从属性设置获取        	
        	dataStoreThing = world.getThing(attribute.getString("dataStore"));
        }

        if(dataStoreThing == null){
        	//从子事物中获取
        	dataStoreThing = attribute.getThing("DataStore@0");
        	
        	if(dataStoreThing == null && attribute.getStringBlankAsNull("relationDataObject") != null){
        		//从数据对象创建
        		dataStoreThing = new Thing("xworker.swt.Commons/@DataStore");
        		dataStoreThing.initDefaultValue();                      
        		dataStoreThing.put("paging", "no"); //下拉列表不分页	    
        		dataStoreThing.put("attachToParent", "true");
        	    	
        		dataStoreThing.put("dataObject", attribute.get("relationDataObject"));
                String queryConfig = attribute.getStringBlankAsNull("relationQueryConfig");
                dataStoreThing.put("queryConfig", queryConfig);
                if(queryConfig == null){
                    Thing qcfg = attribute.getThing("SelectCondition@0");
                    //log.info("select condition =" + qcfg);
                    if(qcfg != null){
                    	dataStoreThing.put("queryConfig", qcfg.getMetadata().getPath());
                    }
                }
                dataStoreThing.put("autoLoad", "true");
                dataStoreThing.put("attachToParent", "true");
                dataStoreThing.put("loadBackground", "true");
                dataStoreThing.put("labelField", attribute.get("relationLabelField"));
        	}
        	return dataStoreThing;
        }else{
        	//实例或者没有绑定父控件，那么包装一下
        	if(UtilData.isTrue(dataStoreThing.doAction("isInstance")) || dataStoreThing.getBoolean("attachToParent") == false){
    			 Thing dataStore = new Thing("xworker.swt.Commons/@DataStore");
    			 dataStore.put("storeRef", dataStoreThing.getMetadata().getPath());
                 dataStore.put("attachToParent", "true");
                 return dataStore;
        	}else{
        		return dataStoreThing;
        	}
        	/*
        	//是指定的数据仓库，因为这里要设置绑定父控件的参数我们拷贝一份新的
        	Thing store = world.getThing(dataStoreThing.getMetadata().getPath());
        	if(store == null){
        		return null;
        	}else{
        		Thing newStore = store.detach();
        		newStore.put("attachToParent", "true");
            	return newStore;
        	}*/
        }
	}
	
	public static class ColorListener implements SelectionListener{
		Text text;
		ActionContext actioContext = new ActionContext();
		
		public ColorListener(Text text){
			this.text = text;
		}

		public void widgetDefaultSelected(SelectionEvent arg0) {			
		}

		public void widgetSelected(SelectionEvent event) {
			Shell shell = (Shell) PoolableControlFactory.borrowControl(text.getShell(), 
					"xworker.swt.widgets.ColorDialog", actioContext);
			
			//shell.setLocation(text.getLocation().x, text.getLocation().y);
			ActionContainer ac = (ActionContainer) shell.getData("actions");			
			final ColorDialog dialog = (ColorDialog) ac.doAction("getColorDialog");
			//dialog.setl
			int[] rgb = UtilSwt.parseRGB(text.getText());
			if(rgb != null){
				dialog.setRGB(new RGB(rgb[0], rgb[1], rgb[2]));
			}
			//dialog.
			if(SwtUtils.isRWT()) {
				SwtUtils.openDialog(dialog, new DialogCallback() {
					@Override
					public void dialogClosed(int returnCode) {
						RGB colorRgb = dialog.getRGB();
						if(colorRgb != null){
							String rgbStr = UtilSwt.RGBToString(colorRgb);
							text.setText(rgbStr);
							//UtilSwt.setBackground(text, text.getText(), null);
						}
						
						text.setFocus();
					}
					
				}, null);
			} else {
				RGB colorRgb = dialog.open();
				if(colorRgb != null){
					String rgbStr = UtilSwt.RGBToString(colorRgb);
					text.setText(rgbStr);
					//UtilSwt.setBackground(text, text.getText(), null);
				}
				
				text.setFocus();
			}
		}
	}	
}