package xworker.app.view.swt.widgets.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilData;

import xworker.app.view.swt.data.DataStoreSelectionListener;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectListener;
import xworker.swt.design.Designer;
import xworker.swt.editor.EditorModifyListener;
import xworker.swt.events.SwtListener;
import xworker.swt.form.ThingDescriptorForm;
import xworker.swt.util.DelayExecutor;
import xworker.swt.util.SwtUtils;

public class DataObjectForm implements DataObjectListener, DisposeListener, DataStoreSelectionListener{
	private static Logger logger = LoggerFactory.getLogger(DataObjectForm.class);
	private static final String KEY_DATAOBJECTFORM = "dataObjectForm";
	/** 为了避免数据对象回填数据修改表单的值 */
	private static ThreadLocal<ThingFormModifyListener> updater = new ThreadLocal<ThingFormModifyListener>();
	
	Thing self;
	Thing form;
	ActionContext actionContext;
	ThingFormModifyListener modifyListener;
	DataObject dataObject;
	List<DataObjectFormListener> listeners = null;
	
	public DataObjectForm(Thing self, Thing form, ThingFormModifyListener modifyListener, ActionContext actionContext) {
		this.self = self;
		this.form = form;
		this.modifyListener = modifyListener;
		this.actionContext = actionContext;
		
		modifyListener.setDataObjectForm(this);
	}
	
	public void addListener(DataObjectFormListener listener) {
		if(listeners == null) {
			listeners = new ArrayList<DataObjectFormListener>();
			
		}
		listeners.add(listener);
	}
	
	public void removeListener(DataObjectFormListener listener) {
		if(listeners != null) {
			listeners.remove(listener);
		}
	}
	
	public static Object create(final ActionContext actionContext){
		World world = World.getInstance();
		final Thing self = actionContext.getObject("self");

		//获取数据对象
		Object dataObject = null;
		String dataObjectStr = self.getStringBlankAsNull("dataObject");
		if(dataObjectStr != null) {
			if(dataObjectStr.indexOf(":") != -1) {
				dataObject = UtilData.getData(dataObjectStr, actionContext);
			}else {
				dataObject = world.getThing(dataObjectStr);
			}
			
			if(dataObject instanceof String) {
				dataObject = world.getThing((String) dataObject);
			}
		}		
		if(dataObject == null){
		    Thing dos = self.getThing("dataObjects@0");
		    if(dos != null && dos.getChilds().size() > 0){
		        dataObject = dos.getChilds().get(0);
		    }
		}

		//创建面板
		Thing compositeThing = world.getThing("xworker.swt.widgets.Composite");
		Composite composite = null;
		Designer.pushCreator(self);
		try{
			composite = (Composite) compositeThing.run("create", actionContext);
		}finally {
			Designer.popCreator();
		}
		FillLayout fillLayout = new FillLayout();
		composite.setLayout(fillLayout);

			//创建事物定义，使用事物编辑器
		Thing form = new Thing("xworker.app.view.swt.widgets.form.DataObjectForm");
		form.setData("formThing", self);
		form.setData("parent", composite);
		form.set("extends", self.getMetadata().getPath());
		form.put("H_SCROLL","true");//self.H_SCROLL;
		form.put("V_SCROLL", "true");//self.V_SCROLL;
		form.setData("defaultSelection",  new Listener() {
			Event lastEvent;
			
			@Override
			public void handleEvent(Event event) {
				if(lastEvent == event) {
					return;
				}else {
					lastEvent = event;
				}
				try {
					//Widget widget = event.widget;
					//Listener[] listeners = widget.getListeners(SWT.DefaultSelection);
					//System.out.println(listeners);
					//System.out.println(event);
					self.doAction("defaultSelection", actionContext, "event", event);
				}catch(Exception e) {
					logger.warn("DefaultSelection exception, form=" + self.getMetadata().getPath(), e);
				}
			}
			
		});
		//form.put("defaultModify", self.get("defaultModify"));
		
		//修改事件
		//String defaultModify = self.getStringBlankAsNull("defaultModify");
		Object defaultModifyObj = null;//defaultModify == null ? null : actionContext.get(defaultModify);
		ThingFormModifyListener modifyListener = new ThingFormModifyListener(composite, self, form, defaultModifyObj, actionContext);
		form.setData("defaultModify", modifyListener);
		
		//表单对象和设置监听等
		DataObjectForm dataObjectForm = new DataObjectForm(self, form, modifyListener, actionContext);
		composite.addDisposeListener(dataObjectForm);
		form.setData(KEY_DATAOBJECTFORM, dataObjectForm);
		
		try {
			if(dataObject != null){
			    form.doAction("setDataObject", actionContext, "dataObject", dataObject);
			}
	
			actionContext.getScope(0).put(self.getMetadata().getName(), form);
			Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
			SwtUtils.checkLayoutError(composite.getShell());
		}finally {
			modifyListener.setEnabled(true);
		}
		
		try{
		    //创建子事物，通常是布局数据
		    Bindings bindings = actionContext.push(null);
		    bindings.put("parent", composite);
		    bindings.put("dataObjectForm", form);
		    for(Thing child : self.getChilds()){
		        child.doAction("create", actionContext);
		    }
		}finally{
		    actionContext.pop();
		}
		
		//绑定到DataStore
		String dataStore = self.getStringBlankAsNull("dataStore");
		if(dataStore != null) {
			Thing ds = actionContext.getObject(dataStore);
			if(ds != null) {
				ds.doAction("addSelectionListener", actionContext, "listener", dataObjectForm);
			}
		}
		return composite;
	}
	
	public static void setDataObject(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Object dobj = actionContext.get("dataObject");
		
		//表单定义
		Thing formThing = (Thing) self.getData("formThing");
		ThingFormModifyListener modifyListener = (ThingFormModifyListener) self.getData("defaultModify");
	    try{	    	
	    	//先清空父控件的内容
			//if(self.getData("formComposite") != null){
	//		    self.getData("formComposite").dispose();
			//}
			Composite parentComposite = (Composite) self.getData("parent");
			if(parentComposite.isDisposed()) {
				return;
			}
			for(Control control : parentComposite.getChildren()){
			    //log.info("control=" + control);
			    control.dispose();
			    //parentComposite.removeControl(control);
			}

			if(dobj == null) {
				parentComposite.layout();
				self.setData("formContext", null);
				self.setData("model", null);
				return;
			}
			
		    modifyListener.setEnabled(false);
			String type = actionContext.getObject("formType");
			if(type == null || "".equals(type)){
			   type = formThing.getString("formType");
			}
			if(type == null || "".equals(type)){
			   type = "edit";
			}
	
			//数据对象结构定义
			Thing dataObject = null;
			if(dobj instanceof DataObject){
			    self.setData("values", dobj);
			    dataObject = ((DataObject) dobj).getMetadata().getDescriptor();
			    self.setData("dataObject", dobj);
			}else{
				//数据对象的描述者
				Thing desc = (Thing) dobj;
				DataObject dataObj = desc.doAction("newInstance", actionContext);
				if(dataObj == null) {
					dataObj = new DataObject(desc);
					dataObj.init(actionContext);
				}
				
				self.setData("values", dataObj);
			    dataObject = desc;
			    self.setData("dataObject", dataObj);
				
			    /*
				dataObject = (Thing) dobj;
				//System.out.println(dataObject.getRoot().getMetadata().getPath());
				//System.out.println(dataObject.getRoot().getMetadata().getLastModified()); 
				
			    DataObject values = new DataObject((Thing) dataObject);
			    values.init(actionContext);
			    self.setData("values", values);
			    self.setData("dataObject", dataObject);*/
			}
			
			//自定义编辑器部分
			Thing swtEdit = self.getThing("SwtEditSetting@0");
			if(swtEdit != null){
			    self.removeChild(swtEdit);
			}
			swtEdit = dataObject.getThing("SwtEditSetting@0");
			if(swtEdit != null){
			    self.addChild(swtEdit, false);
			}
			//log.info("swtEdit=" + swtEdit);
	
			//生成表单
			String descriptors = self.getString("descriptors");
			self.getAttributes().putAll(dataObject.getAttributes());
			self.set("descriptors", descriptors);
			self.set("extends", ((Thing) self.getData("formThing")).getMetadata().getPath());
			self.set("descriptorPath", self.getMetadata().getPath());		
			String editCols = formThing.getStringBlankAsNull("editCols");
			if(editCols != null){
			    self.set("editCols", editCols);
			}
			List<Thing> childs = self.getChilds();
			for(int i=0; i<childs.size(); i++){
			    if(childs.get(i).getThingName().equals("attribute")){
			        childs.remove(i);
			        i--;
			    }
			}
	
			for(Thing attribute : dataObject.getChilds("attribute")){
			    addAttribute(attribute, self, type, dataObject, actionContext);
			}
	
					//创建表单
			Bindings bindings = actionContext.push(null);
			try{
			    bindings.put("thing", self);
			    bindings.put("thingAttributes", new HashMap<String, Object>());
			    bindings.put("parent", self.getData("parent"));
			    if(self.getData("defaultModify")  != null){
			    	bindings.put("modifyListener", self.getData("defaultModify"));
			    	bindings.put("defaultModify", self.getData("defaultModify"));
			    }
			    if(self.getData("defaultSelection")  != null){
			    	bindings.put("defaultSelection", self.getData("defaultSelection"));
			    }
			    
			    //self.H_SCROLL = "true";
			    //self.V_SCROLL = "true";
			    Composite composite = ThingDescriptorForm.createForm(actionContext);
			    //def ac = new ActionContext();
			    //ac.put("parent", composite);    
			    //for(child in formThing.getChilds()){
			    //    child.doAction("create", ac);
			    //}
			    ActionContext formContext = (ActionContext) composite.getData("actionContext");
			    formContext.put("__dataObjectForm", self);
			    self.setData("formContext", formContext);
			    self.setData("model", formContext.get(self.getString("name") + "Model"));
			    self.setData("formComposite", composite);    
			    
			    //其他设置属性而常规没有的
			    for(Thing attr : self.getChilds("attribute")){
			        if(attr.get("SwtObject@0") != null){
			            continue;
			        }
			        try{
			            Object input = formContext.get(attr.getString("name") + "Input");
			            if(input != null && input instanceof Control){
			                if(attr.getBoolean("readOnly")){
			                	OgnlUtil.setValue("editable", input, false);
			                }
			            }
			        }catch(Exception e){
			            logger.warn("set input editable, editor=" + attr.getString("name") + "Input, exception=" + e.getMessage());
			        }
			    }
			    
			    ((Composite) self.getData("parent")).layout();
			}finally{
			    actionContext.pop();
			}
			
			Object values = self.getData("values");
	
			//如果数据对象已改变，增加监听
			DataObjectForm form = (DataObjectForm) self.getData(KEY_DATAOBJECTFORM);
			if(form != null && form.dataObject != values) {
				if(values instanceof DataObject) {
					if(form.dataObject != null) {
						form.dataObject.removeListener(form);
					}
					
					form.dataObject = (DataObject) values;
					form.dataObject.addListener(form);
				}
			}
			
			//刷数据
			if(values != null){
			    self.doAction("setValues", actionContext, "values", values);
			}
			
			//触发DataObjectForm的监听器的事件
			if(form.listeners != null) {
				for(DataObjectFormListener listener : form.listeners) {
					listener.onSetDataObject(form, form.dataObject);
				}
			}
	    }finally {
	    	modifyListener.setEnabled(true);
	    }
	}
	
	public static void addAttribute(Thing attr, Thing form, String type, Thing dataObject, ActionContext actionContext){
	    Thing attribute = attr;
	    Thing at = null;
	    boolean add = true;
	    if("create".equals(type)){
            if(attr.getBoolean("createEditor") == false){
                add = false;
            }
            at = attr.getThing("CreateCofnig@0");
	    }else if("edit".equals(type)){
            if(attr.getBoolean("editEditor") == false){
                add = false;
            }
            at = attr.getThing("EditConfig@0");
	    }else if("view".equals(type)){
            if(attr.getBoolean("viewEditor") == false){
                add = false;
            }
            at = attr.getThing("ViewConifg@0");
	    }else if("query".equals(type)){
            if(attr.getBoolean("queryEditor") == false){
                add = false;
            }
            at = attr.getThing("QueryConfig@0");
	    }
	    
	    //log.info("type=" + type + ",add=" + add);
	    if(!add){
	        return;
	    }else{
	        String attributePath = attribute.getMetadata().getPath(); //保存原有的事物路径，方便Desinger能够正确打开原先的属性
	        //log.info("attributePath=" + attributePath);
	        if(at != null){
	            //由于config中只定义了样式，没有属性的基本数据等，所以复制
	            Thing attemp = at.detach();
	            Map<String, Object> temp = new HashMap<String, Object>();
	            temp.putAll(attribute.getAttributes());
	            temp.putAll(attemp.getAttributes());
	            attemp.getAttributes().putAll(temp);
	            attemp.put("name", attribute.getString("name"));
	            attemp.put("label", attribute.get("label"));
	            attemp.put("pattern", attribute.get("editPattern"));
	            attemp.put("descriptors", attribute.getString("descriptors"));
	            attribute = attemp;            
	        }else{
	            attribute = attribute.detach();     
	            attribute.put("pattern", attribute.get("editPattern"));      
	        }
	        
	        //attribute已detach
	        attribute.setData("_originalityAttributePath", attributePath);       
	        String inputtype = attribute.getString("inputtype");
	        		
	        if("select".equals(inputtype) || "inputSelect".equals(inputtype) || "multSelect".equals(inputtype)){             
	             //log.info("dddd=" + attribute.inputattrs);
	        	String inputattrs = attribute.getStringBlankAsNull("inputattrs");
	             String dobj = attribute.getString("relationDataObject");     
	             if((dobj == null || dobj == "") && (inputattrs == null)){
	                 //查看是否自定义了数据对象
	                 Thing dataStore = attribute.getThing("DataStore@0");
	                 if(dataStore != null){
	                     inputattrs = dataStore.getMetadata().getPath();
	                 }
	             }else if(dobj != null && dobj != "" && (inputattrs == null )){
	                 //如果属性是多对一关联其他属性的，并且是下拉选择框，那么初始化相关下拉框的功能
	            	  Thing dataStore = new Thing("xworker.swt.Commons/@DataStore");
	                  dataStore.initDefaultValue();                      
	                  dataStore.put("paging", "no"); //下拉列表不分页	                  
	                  String store = attr.getStringBlankAsNull("store"); //直接引用其它已存在的sotre
	                  if(store != null){
	                	  try{
	                		  Thing refstore = (Thing) OgnlUtil.getValue(store, actionContext);
	                		  if(refstore != null){
	                			  store = refstore.getMetadata().getPath();
	                		  }else{
	                			  logger.info("get ref data store return null, ognl=" + store + ", attribute=" + attr.getMetadata().getPath());
	                			  store = null;
	                		  }
	                	  }catch(Exception e){
	                		  logger.warn("get ref data store error, ognl=" + store + ", attribute=" + attr.getMetadata().getPath() + ", exception=" + e.getMessage());
	                		  store = null;
	                	  }
	                  }
	                  if(store != null){
	                      //字段定义了引擎其他数据仓库
	                      dataStore.put("storeRef", store);
	                      dataStore.put("attachToParent", "true");
	                      dataStore.put("loadBackground", "true");
	                  }else{
	                      //创建数据仓库
	                      dataStore.put("dataObject", attribute.get("relationDataObject"));
	                      String queryConfig = attribute.getStringBlankAsNull("relationQueryConfig");
	                      dataStore.put("queryConfig", queryConfig);
	                      if(queryConfig == null){
	                          Thing qcfg = attr.getThing("SelectCondition@0");
	                          //log.info("select condition =" + qcfg);
	                          if(qcfg != null){
	                              dataStore.put("queryConfig", qcfg.getMetadata().getPath());
	                          }
	                      }
	                      dataStore.put("autoLoad", "true");
	                      dataStore.put("attachToParent", "true");
	                      dataStore.put("loadBackground", "true");
	                      dataStore.put("labelField", attribute.get("relationLabelField"));
	                  }
	                  attribute.setData("dataStore", dataStore);
	                  attribute.put("inputattrs",  dataStore.getMetadata().getPath());
	             }             
	        }
	        
	        form.addChild(attribute, false);
	    }
	}
	
	public static Object getDataObject(ActionContext actionContext){
	    return saveObject(actionContext);
	}
	
	//对应DataObjectForm的updateDataObject方法
	@SuppressWarnings("unchecked")
	public static Object saveObject(ActionContext actionContext){		
		Thing self = actionContext.getObject("self");
		    
		ThingFormModifyListener modifyListener = (ThingFormModifyListener) self.getData("defaultModify");
		try {
			updater.set(modifyListener);
			ActionContext formContext = (ActionContext) self.getData("formContext");
			Thing model = (Thing) self.getData("model");
			if(formContext != null && model != null){
			    Map<String, Object> values = (Map<String, Object>) model.doAction("getValue", formContext);
			    Object dataObject = self.get("values");
			    if(!(dataObject instanceof DataObject)){
			    	Object obj = self.getData("dataObject");
			    	if(obj instanceof DataObject){
			    		dataObject = (DataObject) obj;
			    	}else{
			    		dataObject = new DataObject((Thing) self.getData("dataObject"));
			    	}
			    }
			    
			    DataObject dobj = (DataObject) dataObject;
			    for(String key : values.keySet()){
			        dobj.put(key, values.get(key));
			    }
			    
			    return dobj;
			}else{
			    return null;
			}
		}finally {
			updater.set(null);
		}
	}
	
	public static void setValues(ActionContext actionContext){
	    Thing self = actionContext.getObject("self");	    
	    ThingFormModifyListener modifyListener = (ThingFormModifyListener) self.getData("defaultModify");
	    modifyListener.setEnabled(false);
	    try{
		    ActionContext formContext = (ActionContext) self.getData("formContext");
			Thing model = (Thing) self.getData("model");
			if(formContext == null || model == null) {
				return;
			}
			
			Object values = actionContext.get("values");
		    self.put("values", values);
		    if(formContext != null && model != null){
		        formContext.put("thingAttributes", values);    
		        model.doAction("setValue", formContext);
		    }
	    }finally{
	    	modifyListener.setEnabled(true);
	    }
	}
	
	@SuppressWarnings("unchecked")
	public static void setPartialValues(ActionContext actionContext){
	    Thing self = actionContext.getObject("self");	    
	    ThingFormModifyListener modifyListener = (ThingFormModifyListener) self.getData("defaultModify");
	    modifyListener.setEnabled(false);
	    try{
		    ActionContext formContext = (ActionContext) self.getData("formContext");
			Thing model = (Thing) self.getData("model");
			if(formContext == null || model == null) {
				return;
			}
			
			Object values = actionContext.get("values");
			Map<String, Object> v = (Map<String, Object>) self.get("values");
		    if(v == null){
		        v = new HashMap<String, Object>();
		        self.put("values", v);
		    }
		    
		    v.putAll((Map<String, Object>) values);
		    if(formContext != null && model != null){
		        formContext.put("thingAttributes", v);    
		        model.doAction("setValue", formContext);
		    }
	    }finally {
	    	modifyListener.setEnabled(true);
	    }
	}
	
	public static Object getValues(ActionContext actionContext){
	    Thing self = actionContext.getObject("self");	    
	    ActionContext formContext = (ActionContext) self.getData("formContext");
		Thing model = (Thing) self.getData("model");
		
		if(formContext != null && model != null){
		    return model.doAction("getValue", formContext);
		}else{
		    return null;
		}
	}
	
	public static Object validate(ActionContext actionContext){
	    Thing self = actionContext.getObject("self");	    
	    ActionContext formContext = (ActionContext) self.getData("formContext");
		Thing model = (Thing) self.getData("model");
		
		if(formContext != null && model != null){
		    return model.doAction("validate", formContext);
		}else{
		    return false;
		}
	}
	
	/**
	 * 从Form模型上获取DataObjectForm对象。
	 * 
	 * @param form
	 * @return
	 */
	public static DataObjectForm getDataObjectForm(Thing form) {
		return (DataObjectForm) form.getData(KEY_DATAOBJECTFORM);
	}
	
	public static Object createDataStoreSelectionListener(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");	
		return self.getData(DataObjectForm.KEY_DATAOBJECTFORM);
	}
	
	public static Object getActionContext(ActionContext actionContext){
	    Thing self = actionContext.getObject("self");	    
	    return self.getData("formContext");
	}
	

	@Override
	public void widgetDisposed(DisposeEvent e) {
		if(dataObject != null) {
			dataObject.removeListener(this);
		}
	}
	
	/**
	 * 返回表单的根Control。
	 * 
	 * @return
	 */
	public Control getControl() {
		return (Control) form.getData("parent");
	}

	/**
	 * 返回具体表单的上下文。
	 * 
	 * @return
	 */
	public ActionContext getFormContext( ) {
		return (ActionContext) form.getData("formContext");
	}
	
	/**
	 * 返回表单的数据模型。
	 * 
	 * @return
	 */
	public Thing getModel()	{
		return (Thing) form.getData("model");
	}
	
	/**
	 * 设置表单的数据。
	 * 
	 * @param values Map<String, Object> 或 DataObject
	 */
	public void setValues(Object values) {
		form.doAction("setValues", actionContext, "values", values);
	}
	
	/**
	 * 设置数据对象。
	 * 
	 * @param dataObject
	 */
	public void setDataObject(DataObject dataObject) {
		form.doAction("setDataObject", actionContext, "dataObject", dataObject);
	}
	
	/**
	 * 通过描述者创建一个新的数据对象，并设置到表单里。
	 * 
	 * @param dataObjectDescriptor
	 */
	public void setDataObject(Thing dataObjectDescriptor) {
		DataObject dataObject = new DataObject(dataObjectDescriptor);
		dataObject.init(actionContext);
		
		setDataObject(dataObject);
	}
	
	/**
	 * 设置表单的部分数据。
	 * 
	 * @param values Map<String, Object> 或 DataObject
	 */
	public void setPartialValues(Object values) {
		form.doAction("setPartialValues", actionContext, "values", values);
	}
	
	/**
	 * 返回表单的值。
	 * 
	 * @return
	 */
	public Map<String, Object> getValues(){
		return form.doAction("getValues", actionContext);
	}
	
	/**
	 * 保存并返回表单的数据对象。
	 * 
	 * @return
	 */
	public DataObject getDataObject() {
		return form.doAction("getDataObject", actionContext);
	}
	
	@Override
	public void selected(List<DataObject> dataObjects) {
		if(dataObjects != null && dataObjects.size() > 0) {
			form.doAction("setDataObject", actionContext, "dataObject", dataObjects.get(0));
		}
	}
	
	static class ThingFormModifyListener extends EditorModifyListener{
    	Object defaultModifyObj;
    	/** 原生的表单事物 */
    	Thing self;
    	/** 创建时生成的表单事物 */
    	Thing form;
    	ActionContext actionContext;
    	boolean enabled = false;
    	DelayExecutor executor = null;
    	Composite composite;
    	DataObjectForm dataObjectForm;
    	
    	public ThingFormModifyListener(Composite composite, Thing self, Thing form,  Object defaultModifyObj, ActionContext actionContext) {
    		super(null);
    		
    		this.composite = composite;
    		this.self = self;
    		this.form = form;
    		this.defaultModifyObj = defaultModifyObj;
    		this.actionContext = actionContext;
    	}
    	
    	protected void setDataObjectForm(DataObjectForm dataObjectForm) {
    		this.dataObjectForm = dataObjectForm;
    	}
    	
		@Override
		protected void handlerEvent(TypedEvent event) {
			if(!enabled) {
				return;
			}
			
			//触发默认修改事件
			if(defaultModifyObj instanceof ModifyListener){
				if(event instanceof ModifyEvent) {
					((ModifyListener) defaultModifyObj).modifyText((ModifyEvent) event);
				}else {
					Event newEvent = new Event();
					if(event != null){
						newEvent.item = event.widget;
						newEvent.widget = event.widget;
					}
					((ModifyListener) defaultModifyObj).modifyText(new ModifyEvent(newEvent) );
				}
			}else if(defaultModifyObj instanceof SwtListener){
				Event newEvent = new Event();
				if(event != null){
					newEvent.item = event.widget;
					newEvent.widget = event.widget;
				}
				((SwtListener) defaultModifyObj).handleEvent(newEvent);
			}
						
			//触发自身的修改事件
			self.doAction("modified", actionContext);
			//触发添加到DataObjectForm中的监听器
			if(dataObjectForm.listeners != null) {
				for(DataObjectFormListener listener : dataObjectForm.listeners) {
					listener.onMidified(dataObjectForm);
				}
			}
			
			//触发数据对象修改
			if(self.getBoolean("fireDataObjectMidify")) {
				if(executor == null) {
					executor = new DelayExecutor(composite.getDisplay(), 500) {
						public void doTask() {
							updater.set(ThingFormModifyListener.this);
							try {
								form.doAction("getDataObject", actionContext);
							}finally {
								updater.remove();
							}
						}
					};
				}
				
				executor.execute();
			}
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
			setEnable(enabled);
		}
    }

	@Override
	public void changed(final DataObject dataObject) {
		if(updater.get() == modifyListener) {
			//是表单自身触发的修改，忽略不处理
			return;
		}
		
		Composite parent = form.getData("parent");
		if(parent != null && parent.isDisposed() == false) {
			parent.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						Object oldDataObject =  form.getData("dataObject");
						if(oldDataObject == dataObject) {
							//只是数据对象的值修改了
							form.doAction("setValues", actionContext, "values", dataObject);
						}else {
							//放置了新的数据对象
							form.doAction("setDataObject", actionContext, "dataObject", dataObject);
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			});			
		}
	}

}
