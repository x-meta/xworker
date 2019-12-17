package xworker.dataObject.swt;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.swt.reacts.DataReactorContext;
import xworker.swt.reacts.WidgetDataReactor;

public abstract class DataObjectComposite extends WidgetDataReactor{
	public DataObjectComposite(Widget widget, Thing self, ActionContext actionContext) {
		super(widget, self, actionContext);
		
	}
	
	public DataObjectComposite(Widget widget, Thing self, ActionContext actionContext, boolean createChilds) {
		super(widget, self, actionContext, createChilds);
		
	}
	
	/**
	 * 设置一个数据对象。
	 * 
	 * @param dataObject
	 */
	public abstract void setDataObject(DataObject dataObject);
	
	/**
	 * 返回数据对象。
	 * 
	 * @return
	 */
	public abstract DataObject getDataObject();
	
	/**
	 * 设置值，比如设置表单的值。
	 * 
	 * @param values
	 */
	public abstract void setValues(Map<String, Object> values);
	
	/**
	 * 设置部分值，比如设置表单的值。和setValues方法的区别是如果表单的一个字段不在values中，则会保留原值，
	 * 而setValues方法可能会使用默认值或置为空。
	 * 
	 * @param values
	 */
	public abstract void setPartialValues(Map<String, Object> values);
	
	/**
	 * 校验。比如执行表单的校验。
	 */
	public abstract boolean validate();
		
	/**
	 * 插入一组数据对象，比如通过数据对象的表格列表等。
	 * 
	 * @param index -1表示插入到末尾
	 * @param datas
	 */
	public abstract void insert(int index, List<DataObject> datas);
	
	/**
	 * 移除一组数据对象。
	 * 
	 * @param datas
	 */
	public abstract void remove(List<DataObject> datas);
	
	/**
	 * 返回数据对象列表。
	 * 
	 * @return
	 */
	public abstract List<DataObject> getDataObjects();

	@Override
	protected void widgetDoOnSelected(List<Object> datas, DataReactorContext context) {
		super.widgetDoOnSelected(datas, context);
	}

	@Override
	protected void widgetDoOnUnselected(DataReactorContext context) {
		super.widgetDoOnUnselected(context);
	}

	@Override
	protected void widgetDoOnAdded(int index, List<Object> datas, DataReactorContext context) {
		super.widgetDoOnAdded(index, datas, context);
	}

	@Override
	protected void widgetDoOnRemoved(List<Object> datas, DataReactorContext context) {
		super.widgetDoOnRemoved(datas, context);
	}

	@Override
	protected void widgetDoOnUpdated(List<Object> datas, DataReactorContext context) {
		super.widgetDoOnUpdated(datas, context);
	}

	@Override
	protected void widgetDoOnLoaded(List<Object> datas, DataReactorContext context) {
		super.widgetDoOnLoaded(datas, context);
	}
	
	
}
