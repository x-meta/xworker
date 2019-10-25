package xworker.swt.xworker.attributeEditor;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.ThingMetadata;

import xworker.swt.design.Designer;
import xworker.swt.editor.LabelToolTipListener;
import xworker.swt.form.FormModifyListener;
import xworker.swt.util.SwtUtils;
import xworker.swt.xworker.CodeAssistor;
import xworker.util.XWorkerUtils;

/**
 * 事物模型表单的属性编辑器的基础类。
 * 
 * @author zyx
 *
 */
public abstract class AttributeEditor {
	private static Logger log = LoggerFactory.getLogger(AttributeEditor.class);
	
	/** 事物模型 */
	public Thing formThing;
	/** 属性模型 */
	public Thing attribute;
	/** 表单的变量上下文 */
	public ActionContext context;
	/** 布局数据 */
	public xworker.swt.form.GridData xgridData;
	/** 属性路径 */
	public String path;
	/** 属性标签 */
	public String label;
	/** 属性名 */
	public String name;
	/** 属性的描述 */
	public String description;
	/** 输入控件的变量名 */
	public String inputName;
	/** 字体的单个字的宽度 */
	public int _width;
	/** 字体的高度 */
	public int _height;
	
	
	public AttributeEditor(Thing formThing, Thing attribute, xworker.swt.form.GridData gridData, ActionContext actionContext) {
		this.formThing = formThing;
		this.attribute = attribute;
		ThingMetadata metadata = attribute.getMetadata();
		this.path = metadata.getPath();
	    if(attribute.getData("_originalityAttributePath") != null){
	    	//比如DatObjectForm生成的属性已经是detach了，使用现有属性无法找到原始属性
	    	path = (String) attribute.getData("_originalityAttributePath");
	    }
	    
		this.label = metadata.getLabel();
		this.name = metadata.getName();
		this.inputName = this.name + "Input";
		this.description = metadata.getDescription();
		this.xgridData = gridData;
		this.context = actionContext;
	}
	
	/**
	 * 设置字体的高度和宽度。
	 * 
	 * @param _width
	 * @param _height
	 */
	public void setWidthHeight(int _width, int _height) {
		this._width = _width;
		this._height = _height;
	}
	
	public static int getColspan(int colspan){
	    if(colspan <= 1) return 1;
	    
	    return colspan * 2 - 1;
	}
	
	protected static void initCodeAssistor(Object text, ActionContext context ){
		//查找变量上下文
		ActionContext varAc = XWorkerUtils.getParentVar(context, "variablesActionContext");
		Thing thing = (Thing) context.get("thing");
		CodeAssistor.attach(thing, (Control) text, varAc);
		/*
		if(thing != null && SwtUtils.isRWT() == false) {
			StyledTextProxy.initCodeAssistor(text, thing, varAc);
		}*/
	}
	
	protected void addDefaultSelection(Control control, Listener listener) {
		if(listener == null || control == null) {
			return;
		}
		
		Listener[] lis = control.getListeners(SWT.DefaultSelection);
		for(int i=0; i<lis.length; i++) {
			if(lis[i] == listener) {
				//已经添加过了
				return;
			}
		}
		
		control.addListener(SWT.DefaultSelection, listener);
	}
	
	@SuppressWarnings("unchecked")
	protected static List<Thing> getAttributeValues(Thing attributeDesc, ActionContext eidtorActionContext){
		List<Thing> values = attributeDesc.getAllChilds("value"); 
		if(values == null || values.size() == 0){
			Object vs = attributeDesc.doAction("getValues", eidtorActionContext);
			if(vs instanceof List){
				try{
					values = (List<Thing>) vs;
				}catch(Exception e){
					//log.warn("get attribute values(getValues)", e);
				}
			}
		}
		
		return values;
	}
	
	private void setAddonLabel(Composite parent, Control editorControl, String labelStr) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginBottom = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		
		composite.setLayoutData(editorControl.getLayoutData());
		Label label = new Label(composite, SWT.None);
		label.setText(labelStr);
		editorControl.moveAbove(label);
		
		composite.pack();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void create(Composite parent, FormModifyListener modifyListener, Listener defaultSelection) {
		createLabel(parent);
		
		Control editorControl = null;
		String addonLabel = attribute.getStringBlankAsNull("addonLabel");
		if(addonLabel != null) {
			Composite composite = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			layout.marginBottom = 0;
			layout.marginHeight = 0;
			layout.marginLeft = 0;
			layout.marginRight = 0;
			layout.marginTop = 0;
			layout.marginWidth = 0;
			composite.setLayout(layout);
			
			//composite.setLayoutData(editorControl.getLayoutData());
			
			Control control = createControl(composite, modifyListener, defaultSelection);
			
			Label label = new Label(composite, SWT.None);
			label.setText(addonLabel);
			
			Color color = SwtUtils.createColor(label, attribute.getStringBlankAsNull("addonLabelColor"), context);
		    if(color != null) {
		    	label.setForeground(color);
		    }
		    Font font = SwtUtils.createFont(label, attribute.getStringBlankAsNull("addonLabelFont"), context);
		    if(font != null) {
		    	label.setFont(font);
		    }
		    
		    while(control.getParent() != composite && control.getParent() != null) {
		    	control = control.getParent();
		    }
		    
		    GridData data = (GridData) control.getLayoutData();
		    System.out.println(data);
		    composite.setLayoutData(control.getLayoutData());
			editorControl = composite;
			
		}else {		
			editorControl = createControl(parent, modifyListener, defaultSelection);
		}
		
		List<Thing> swts = (List) attribute.get("SwtObject@0/inputExtend@");			    
	    try{
	    	Bindings bindings = context.push(null);
	    	Object control = context.get(inputName);
	    	if(control instanceof Control){
	    		bindings.put("parent", control);
	    	}else{
	    		bindings.put("parent", context.get(name + "SwtInput"));
	    	}
	    	bindings.put("attribute", attribute);	
	    	for(Thing swt : swts){
		    	try{
		    		for(Thing schild : swt.getChilds()){
		    			schild.doAction("create", context);
		    		}
		    	}catch(Exception eee){
		    		log.error("create field extend swt obj", eee);
		    	}
		    }
	    }catch(Exception eee){
	    	log.error("craete field extend set obj", eee);			    	
	    }finally{
	    	context.pop();
	    }	    
	}
	
	public Label createLabel(Composite composite) {
		if(attribute.getBoolean("showLabel", true) != false){		    	
		    int textLabelStyle = SWT.NONE;
		    textLabelStyle |= SWT.HORIZONTAL;
		    textLabelStyle |= SWT.RIGHT;
		    Label textLabel = new Label(composite, textLabelStyle);
		    //textLabel.setFont(Resources.getFont("default"));
		    textLabel.setText(label + ":");
		    
		    Designer.attach(textLabel, path, context, true);				    
		    GridData textLabelGridData = new GridData();
		    textLabelGridData.verticalSpan = xgridData.rowspan;
		    textLabelGridData.horizontalAlignment = GridData.END;
		    String labelAglin = attribute.getString("labelAlign");
		    if(labelAglin != null){
		    	if("left".equals(labelAglin)){
		    		textLabelGridData.horizontalAlignment = GridData.BEGINNING;
		    	}else if("right".equals(labelAglin)){
		    		textLabelGridData.horizontalAlignment = GridData.END;
		    	}else if("center".equals(labelAglin)){
		    		textLabelGridData.horizontalAlignment = GridData.CENTER;
		    	}else if("justify".equals(labelAglin)){
		    		textLabelGridData.horizontalAlignment = GridData.FILL;
		    	}
		    }
		    String labelVAlign = attribute.getString("labelVAlign");
		    if(labelVAlign != null){
		    	if("top".equals(labelVAlign)){
		    		textLabelGridData.horizontalAlignment = GridData.BEGINNING;
		    	}else if("bottom".equals(labelVAlign)){
		    		textLabelGridData.horizontalAlignment = GridData.END;
		    	}else if("center".equals(labelVAlign)){
		    		textLabelGridData.horizontalAlignment = GridData.CENTER;
		    	}else if("baseline".equals(labelVAlign)){
		    		textLabelGridData.horizontalAlignment = GridData.FILL;
		    	}
		    }
		    
		    Color color = SwtUtils.createColor(textLabel, attribute.getStringBlankAsNull("labelColor"), context);
		    if(color != null) {
		    	textLabel.setForeground(color);
		    }
		    Font font = SwtUtils.createFont(textLabel, attribute.getStringBlankAsNull("labelFont"), context);
		    if(font != null) {
		    	textLabel.setFont(font);
		    }
		    // println textLabelGridData.horizontalSpan;
		    textLabel.setLayoutData(textLabelGridData);
		    LabelToolTipListener tipListener = LabelToolTipListener.getInstance(Thread.currentThread());
		    tipListener.setUtilBrowser(context.get("utilBrowser"));
		    try {
		    	textLabel.addMouseTrackListener(tipListener);
		    }catch(Throwable t) {
		    	//Eclipse RAP不支持该方法
		    }
		    textLabel.addMouseListener(tipListener);
		    String toolTipText = "thing=" +  path;
		    /*
		    		"<b><u><a href=\"" + webUrl + "do?sc=xworker.ide.worldExplorer.swt.http.ThingDoc/@desc&thing=" 
		    	+ f.getMetadata().getPath() + "\">"+ f.getMetadata().getName() + "</a></u></b><br/><br/>";*/    
		    
		    if(description != null){
		        //String ttText = f.getMetadata().getDescription();
		        textLabel.setData("toolTip", toolTipText);// + ttText);
		    }else{
		        textLabel.setData("toolTip", toolTipText);
		    }
		    context.getScope(0).put(name + "Label", textLabel);
		    
		    return textLabel;
    	}else{
    		xgridData.colspan += 1;
    		return null;
    	}
	}
	
	public abstract Control createControl(Composite parent, FormModifyListener modifyListener, Listener defaultSelection); 
}
