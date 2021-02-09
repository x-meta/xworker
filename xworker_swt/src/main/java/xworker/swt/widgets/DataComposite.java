package xworker.swt.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectList;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

public class DataComposite {
	Thing thing;
	ActionContext actionContext;
	ActionContext parentContext;
	Composite mainComposite;
	DataObjectList dataList = new DataObjectList();
	Thing form;
	Thing dataObjectDescriptor;
	
	public DataComposite(Thing thing, ActionContext parentContext) {
		this.thing = thing;
		this.parentContext = parentContext;
		
		//创建根面板
		ThingCompositeCreator cc = SwtUtils.createCompositeCreator(thing, parentContext);
		cc.setCompositeThing(World.getInstance().getThing("xworker.swt.xwidgets.prototypes.DataCompositeShell/@MainComposite"));
		//用户自定义的根面板
		Thing compositeThing = thing.getThing("Composite@0");
		if(compositeThing != null) {
			cc.setReplaceCompositeThing(compositeThing);
		}
		mainComposite = cc.create();
		this.actionContext = cc.getNewActionContext();
		actionContext.put("dataList", this.dataList);
	}
	
	/**
	 * 清空主面板。
	 */
	private void clearMainComposite() {
		for(Control child : mainComposite.getChildren()) {
			child.dispose();
		}
	}
	
	/**
	 * 返回当前正在编辑或展示的对象，如果没有返回null，如果是列表模式（如list、table等）返回第一个。
	 * 
	 * @return
	 */
	public Object getData() {
		if(dataList.size() > 0) {
			return dataList.get(0).getWrappedObject();
		}
		
		return null;
	}
	
	/**
	 * 返回当前正在编辑或展示的数据对象，如果没有返回null，如果是列表模式（如list、table等）返回第一个。
	 * 
	 * @return
	 */
	public DataObject getDataObject() {
		if(dataList.size() > 0) {
			return dataList.get(0);
		}
		
		return null;
	}
	
	/**
	 * 返回数据对象列表。
	 * 
	 * @return
	 */
	public DataObjectList getDataObjectList() {
		return dataList;
	}
	
	/**
	 * 使用指定的表单编辑对象。
	 * 
	 * @param data
	 * @param formThing
	 */
	public DataObject showForm(Object data, Thing formThing) {
		clearMainComposite();
		
		if(dataObjectDescriptor == null) {
			throw new ActionException("Can not create form, dataObjectThing is null,  please set corresponding thing first, path=" + thing.getMetadata().getPath());
		}
		
		//床架数据对象
		DataObject dataObject = new DataObject(dataObjectDescriptor);
		dataObject.setWrappedObject(data);
		dataList.clear();
		dataList.add(dataObject);
		
		//创建数据对象的表单
		formThing.doAction("create", actionContext);
		
		form = actionContext.getObject(formThing.getMetadata().getName());
		form.doAction("setDataObject", actionContext, "dataObject", dataObject);
		
		return dataObject;
	}
	
	/**
	 * 添加一个数据到数据对象列表中。
	 * 
	 * @param data
	 */
	public DataObject addData(Object data) {
		if(dataObjectDescriptor == null) {
			throw new ActionException("Can not add data, dataObjectThing is null,  please set corresponding thing first, path=" + thing.getMetadata().getPath());
		}
		
		//床架数据对象
		DataObject dataObject = new DataObject(dataObjectDescriptor);
		dataObject.setWrappedObject(data);
		dataList.add(dataObject);
		
		return dataObject;
	}
	
	/**
	 * 添加一组对象，并返回已包装之后的列数据对象列表。
	 * 
	 * @param datas
	 * @return
	 */
	public List<DataObject> addDatas(Collection<Object> datas){
		if(dataObjectDescriptor == null) {
			throw new ActionException("Can not add datas, dataObjectThing is null,  please set corresponding thing first, path=" + thing.getMetadata().getPath());
		}
						
		List<DataObject> ds = new ArrayList<DataObject>();
		for(Object data : datas) {
			//创建数据对象
			DataObject dataObject = new DataObject(dataObjectDescriptor);
			dataObject.setWrappedObject(data);
			dataList.add(dataObject);
			ds.add(dataObject);
		}
		
		return ds;
	}
	
	/**
	 * 使用当前模型定义的表单模型编辑一个对象，如果表单不存在那么使用默认的。
	 * 
	 * @param data
	 * @param formName
	 */
	public DataObject showForm(Object data, String formName) {
		Thing formThing = null;
		for(Thing form : thing.getChilds("DataObjectForm")) {
			if(form.getMetadata().getName().equals(formName)) {
				formThing = form;
				break;
			}
		}
		if(formThing == null) {
			formThing = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.DataCompositeShell/@form");
		}
		
		return showForm(data, formThing);
	}
	
	/**
	 * 使用默认的表单设置编辑对象。
	 * 
	 * @param data
	 */
	public DataObject showForm(Object data) {
		Thing formThing = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.DataCompositeShell/@form");
		return showForm(data, formThing);		
	}
	
	/**
	 * 返回数据，通常是表单的数据，可能会返回null，比如其它不是表单的界面。
	 * 
	 * @return
	 */
	public Map<String, Object> getValues(){
		if(form != null) {
			return form.doAction("getValues", actionContext);
		}
		
		return null;
	}
	
	/**
	 * 返回校验结果，不是表单时总是返回true。
	 * 
	 * @return
	 */
	public boolean validate() {
		if(form != null) {
			return form.doAction("validate", actionContext);
		}
		
		return true;
	}
	
	/**
	 * 更新界面的数据到对象中，通常只对form有效，其它界面可能没任何操作。
	 */
	public void update() {
		if(form != null) {
			form.doAction("updateDataObject", actionContext);
		}
	}
	
	/**
	 * 设置新的数据对象描述者。
	 * 
	 * @param descriptor
	 */
	public void setDataObjectDescriptor(Thing descriptor) {
		if(descriptor == null) {
			throw new ActionException("DataObject descriptor cann't be null");
		}
		
		this.dataObjectDescriptor = descriptor;
		if(dataList.getDescriptor() != this.dataObjectDescriptor) {
			//数据对象类型已变更，已有的数据对象应该也变化了
			dataList.setDescriptor(dataObjectDescriptor);
		}
	}

	/**
	 * 清空数据对象列表。
	 */
	public void clearDataList() {
		dataList.clear();
	}
	
	/**
	 * 从数据对象列表中清除执行的数据对象。
	 * 
	 * @param data
	 */
	public void remove(Object data) {
		List<DataObject> removed = new ArrayList<DataObject>();
		
		for(DataObject dataObject : dataList) {
			if(dataObject.getMetadata() == data) {
				removed.add(dataObject);
			}
		}
		
		dataList.removeAll(removed);
	}
	
	/**
	 * 通过名字设置数据对象的描述者，数据对象的描述者从DataComposite模型中查找预定义的。
	 * 
	 * @param name
	 */
	public void setDataObjectDescriptor(String name) {
		for(Thing dataObject : thing.getChilds("DataObject")) {
			if(name.equals(dataObject.getString("className")) || name.equals(dataObject.getMetadata().getName())) {
				this.dataObjectDescriptor = dataObject;
				if(dataList.getDescriptor() != this.dataObjectDescriptor) {
					//数据对象类型已变更，已有的数据对象应该也变化了
					dataList.clear();
					dataList.setDescriptor(dataObjectDescriptor);
				}
				break;
			}
		}
		
		throw new ActionException("DataObjectThing is null, name=" + name 
				+ ",  please set corresponding dataobject thing, path=" + thing.getMetadata().getPath());
	}
}

