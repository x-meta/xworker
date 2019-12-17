package xworker.dataObject.swt.composites;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.app.view.swt.widgets.form.DataObjectForm;
import xworker.dataObject.DataObject;
import xworker.dataObject.swt.DataObjectComposite;
import xworker.dataObject.swt.DataObjectCompositeProxy;
import xworker.swt.reacts.DataReactorContext;
import xworker.swt.reacts.DataReactorUtils;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

public class FormComposite extends DataObjectComposite{
	/** 是否有按钮 */
	boolean hasButtons = false;
	/** 是否是固定的表单，固定的表单是表单模型自定义的，不固定是按照传入的数据对象自动创建的 */
	boolean fixForm = false;
	/** 数据对象组件代理 */
	DataObjectCompositeProxy proxy;
	/** 数据对象表单对象 */
	DataObjectForm form;
	/** 正在编辑的数据对象 */
	DataObject dataObject;	
	
	public FormComposite(Widget widget, Thing self, ActionContext actionContext) {
		super(widget, self, actionContext, false);
			
		World world = World.getInstance();
		this.proxy = actionContext.getObject("proxy");
		Thing dataObjectDesc = actionContext.getObject("dataObject");
		DataObject dataObject = new DataObject(dataObjectDesc);
	
		//Composite创建器
		ThingCompositeCreator cc = SwtUtils.createCompositeCreator(self, actionContext);
		cc.setNewActionContext(actionContext);
		cc.addChildFilter("DataObjectForm");
		cc.addChildFilter("Buttons");
		
		//先取用户自定义的表单
		Thing formThing = self.getThing("DataObjectForm@0");
		if(formThing == null) {
			//再取默认的表弟那
			formThing = world.getThing("xworker.dataObject.swt.composites.prototypes.FormPrototype/@form");
		}
		//首先判断是否有按钮
		hasButtons = self.getBoolean("buttonComposite") || self.getThing("Buttons@0") != null;
		if(hasButtons) {
			String buttonCompositeVerticalAlgin = self.getString("buttonCompositeVerticalAlgin");
			if("TOP".equals(buttonCompositeVerticalAlgin)) {
				//按钮在上面
				cc.setCompositeThing(World.getInstance().getThing(
						"xworker.dataObject.swt.composites.prototypes.FormPrototype/@topButtonsForm"));
			}else {
				//默认按钮在下面
				cc.setCompositeThing(World.getInstance().getThing(
						"xworker.dataObject.swt.composites.prototypes.FormPrototype/@bottomButtonsForm"));
			}
		}else {
			//只有表单
			cc.setCompositeThing(formThing);
		}
		
		//创建控件，获取变量
		actionContext.peek().put("formThing", formThing);
		cc.create();
		Thing thingForm = actionContext.getObject(formThing.getMetadata().getName());
		form = DataObjectForm.getDataObjectForm(thingForm); //获取表单对象
		
		//是否是固定的表单
		Thing formDataObjects = formThing.getThing("dataObjects@0");
		if(formDataObjects != null && formDataObjects.getChilds().size() > 0) {
			this.fixForm = true;
		}
		
		//创建按钮
		Composite buttonComposite = actionContext.getObject("buttonComposite");
		if(buttonComposite != null) {
			actionContext.peek().put("parent", buttonComposite);
			if(self.getBoolean("queryButton")) {
				Thing queryThing = world.getThing("xworker.dataObject.swt.composites.prototypes.FormPrototype/@queryButton");
				queryThing.doAction("create", actionContext);
			}
			if(self.getBoolean("newButton")) {
				Thing queryThing = world.getThing("xworker.dataObject.swt.composites.prototypes.FormPrototype/@queryButton1");
				queryThing.doAction("create", actionContext);
			}
			if(self.getBoolean("saveButton")) {
				Thing queryThing = world.getThing("xworker.dataObject.swt.composites.prototypes.FormPrototype/@queryButton2");
				queryThing.doAction("create", actionContext);
			}
			if(self.getBoolean("deleteButton")) {
				Thing queryThing = world.getThing("xworker.dataObject.swt.composites.prototypes.FormPrototype/@queryButton3");
				queryThing.doAction("create", actionContext);
			}
			
			Thing buttons = self.getThing("Buttons@0");
			if(buttons != null) {
				for(Thing buttonThing : buttons.getChilds()) {
					buttonThing.doAction("create", actionContext);
				}
			}
		}
		
		
		setDataObject(dataObject);
		
		actionContext.g().put(self.getMetadata().getName(), this);
	}
		
	public DataObjectForm getDataObjectForm() {
			return form;
	}
	
	@Override
	public void setDataObject(DataObject dataObject) {
		if(form != null && this.dataObject != null) {
			dataObject.removeListener(form);
		}
		
		this.dataObject = dataObject;
		this.dataObject.addListener(form);

		if(!fixForm) {
			form.setDataObject(dataObject);
		}else {
			form.setValues(dataObject);
			form.getFormThing().setData("dataObject", dataObject);
		}	
	}

	@Override
	public DataObject getDataObject() {		
		return form.getDataObject();
	}

	@Override
	public void setValues(Map<String, Object> values) {
		form.setValues(values);
	}

	@Override
	public void setPartialValues(Map<String, Object> values) {
		form.setPartialValues(values);
	}

	@Override
	public boolean validate() {
		return form.isValidate();
	}

	@Override
	public void insert(int index, List<DataObject> datas) {
	}

	@Override
	public void remove(List<DataObject> datas) {
	}

	@Override
	public List<DataObject> getDataObjects() {
		return null;
	}

	@Override
	protected void widgetDoOnSelected(List<Object> datas, DataReactorContext context) {
		for(Object obj : datas) {
			if(obj instanceof DataObject) {
				this.setDataObject((DataObject) obj);
				break;
			}
		}
	}

	@Override
	protected void widgetDoOnUnselected(DataReactorContext context) {
		if(fixForm == false) {
			form.setDataObject((DataObject) null); 
		}
	}

	@Override
	protected void widgetDoOnAdded(int index, List<Object> datas, DataReactorContext context) {
	}

	@Override
	protected void widgetDoOnRemoved(List<Object> datas, DataReactorContext context) {
	}

	@Override
	protected void widgetDoOnUpdated(List<Object> datas, DataReactorContext context) {
	}

	@Override
	protected void widgetDoOnLoaded(List<Object> datas, DataReactorContext context) {
	}

	/**
	 * 触发proxy的fireSelected事件。
	 */
	public static void  query(ActionContext actionContext) {
		DataObjectCompositeProxy proxy = actionContext.getObject("proxy");	
		FormComposite  ins =  (FormComposite) proxy.getInstance();
		proxy.fireSelected(DataReactorUtils.toObjectList(ins.form.getDataObject()), null);
	}
	
	public static void save(ActionContext actionContext) {
		DataObjectCompositeProxy proxy = actionContext.getObject("proxy");	
		FormComposite  ins =  (FormComposite) proxy.getInstance();
		
		DataObject dataObject = ins.form.getDataObject();
		dataObject.update(actionContext);
		proxy.fireUpdated(DataReactorUtils.toObjectList(dataObject), null);
	}
	
	public static void remove(ActionContext actionContext) {
		DataObjectCompositeProxy proxy = actionContext.getObject("proxy");	
		FormComposite  ins =  (FormComposite) proxy.getInstance();
		
		DataObject dataObject = ins.form.getDataObject();
		dataObject.delete(actionContext);
		proxy.fireRemoved(DataReactorUtils.toObjectList(dataObject), null);
	}
	
	public static void create(ActionContext actionContext) {
		DataObjectCompositeProxy proxy = actionContext.getObject("proxy");	
		FormComposite  ins =  (FormComposite) proxy.getInstance();
		
		DataObject dataObject = ins.form.getDataObject();
		dataObject.create(actionContext);
		proxy.fireAdded(-1, DataReactorUtils.toObjectList(dataObject), null);
	}
	
	public static Object createControl(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Widget parent = actionContext.getObject("parent");
		FormComposite formComposite = new FormComposite(parent, self, actionContext);		
		return formComposite;
	}
}
