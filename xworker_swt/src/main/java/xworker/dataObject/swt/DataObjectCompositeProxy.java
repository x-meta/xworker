package xworker.dataObject.swt;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.dataObject.DataObject;
import xworker.lang.executor.Executor;
import xworker.swt.reacts.DataReactorContext;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

public class DataObjectCompositeProxy extends DataObjectComposite{
	private static final String TAG = DataObjectCompositeProxy.class.getName();
	DataObjectComposite instance = null;
	Thing dataObject = null;
	ActionContext proxyContext;
	ActionContext parentContext;
	Composite rootComposite;
	
	public DataObjectCompositeProxy(Widget widget, Thing self, ActionContext actionContext) {
		super(widget, new Thing(), actionContext, false);
		
		this.self = self;
		
		ThingCompositeCreator sc = SwtUtils.createCompositeCreator(self, actionContext);
		sc.setCompositeThing(World.getInstance().getThing(
				"xworker.dataObject.swt.composites.prototypes.ProxyPrototype/@mainComposite"));
		sc.addChildFilter("DataObjects");
		rootComposite = sc.create();
		proxyContext = sc.getNewActionContext();
		proxyContext.put("parentContext", actionContext);
		proxyContext.put("parent",rootComposite);
		proxyContext.put("proxy", this);
		
		this.dataObject = self.doAction("getDataObject", actionContext);
		if(dataObject == null) {
			Executor.warn(TAG, "dataObject is null, please set it, thing=" + self.getMetadata().getPath());
			return;
		}
		
		proxyContext.put("dataObject", dataObject);
		for(Thing child : self.getChilds()) {
			if(child.isThing("xworker.dataObject.swt.DataObjectComposite")) {
				instance = child.doAction("createControl", proxyContext);
				break;
			}
		}
	}

	public DataObjectComposite getInstance() {
		return instance;
	}
	
	@Override
	public void setDataObject(DataObject dataObject) {
		if(instance != null) {
			instance.setDataObject(dataObject);
		}else {
			Executor.info(TAG, "Can not call setDataObject, instance is null");
		}
	}

	@Override
	public DataObject getDataObject() {
		if(instance != null) {
			return instance.getDataObject();
		}else {
			Executor.info(TAG, "Can not call getDataObject, instance is null");
			return null;
		}
	}

	@Override
	public void setValues(Map<String, Object> values) {
		if(instance != null) {
			instance.setValues(values);
		}else {
			Executor.info(TAG, "Can not call setValues, instance is null");
		}
	}

	@Override
	public void setPartialValues(Map<String, Object> values) {
		if(instance != null) {
			instance.setPartialValues(values);
		}else {
			Executor.info(TAG, "Can not call setPartialValues, instance is null");
		}
	}

	public Composite getControl() {
		return rootComposite;
	}
	
	@Override
	public boolean validate() {
		if(instance != null) {
			return instance.validate();
		}else {
			Executor.info(TAG, "Can not call validate, instance is null");
			return false;
		}
	}

	@Override
	public void insert(int index, List<DataObject> datas) {
		if(instance != null) {
			instance.insert(index, datas);
		}else {
			Executor.info(TAG, "Can not call insert, instance is null");
		}
	}

	@Override
	public void remove(List<DataObject> datas) {
		if(instance != null) {
			instance.remove(datas);
		}else {
			Executor.info(TAG, "Can not call remove, instance is null");
		}
	}

	@Override
	public List<DataObject> getDataObjects() {
		if(instance != null) {
			return instance.getDataObjects();
		}else {
			Executor.info(TAG, "Can not call getDataObjects, instance is null");
			return Collections.emptyList();
		}
	}

	@Override
	protected void widgetDoOnSelected(List<Object> datas, DataReactorContext context) {
		if(instance != null) {
			instance.widgetDoOnSelected(datas, context);
		}else {
			Executor.info(TAG, "Can not call widgetDoOnSelected, instance is null");
		}
	}

	@Override
	protected void widgetDoOnUnselected(DataReactorContext context) {
		if(instance != null) {
			instance.widgetDoOnUnselected(context);
		}else {
			Executor.info(TAG, "Can not call widgetDoOnUnselected, instance is null");
		}
	}

	@Override
	protected void widgetDoOnAdded(int index, List<Object> datas, DataReactorContext context) {
		if(instance != null) {
			instance.widgetDoOnAdded(index, datas, context);
		}else {
			Executor.info(TAG, "Can not call widgetDoOnAdded, instance is null");
		}
	}

	@Override
	protected void widgetDoOnRemoved(List<Object> datas, DataReactorContext context) {
		if(instance != null) {
			instance.widgetDoOnRemoved(datas, context);
		}else {
			Executor.info(TAG, "Can not call widgetDoOnRemoved, instance is null");
		}
	}

	@Override
	protected void widgetDoOnUpdated(List<Object> datas, DataReactorContext context) {
		if(instance != null) {
			instance.widgetDoOnUpdated(datas, context);
		}else {
			Executor.info(TAG, "Can not call widgetDoOnUpdated, instance is null");
		}
	}

	@Override
	protected void widgetDoOnLoaded(List<Object> datas, DataReactorContext context) {
		if(instance != null) {
			instance.widgetDoOnLoaded(datas, context);
		}else {
			Executor.info(TAG, "Can not call widgetDoOnLoaded, instance is null");
		}
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Widget parent = actionContext.getObject("parent");
		
		DataObjectCompositeProxy proxy = new DataObjectCompositeProxy(parent, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), proxy);
		return proxy.getControl();
	}
}
