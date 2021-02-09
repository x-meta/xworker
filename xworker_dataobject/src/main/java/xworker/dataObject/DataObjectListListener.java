package xworker.dataObject;

import java.util.Collection;

public interface DataObjectListListener {
	/**
	 * 设置了新的数据对象的事件。
	 * 
	 * @param list
	 */
	public void onReconfig(DataObjectList list);
	
	/**
	 * 数据加载的事件。
	 */
	public void onLoaded(DataObjectList list);
	
	/**
	 * 新增数据对象的事件。
	 * 
	 * @param dataObject
	 */
	public void onAdded(DataObjectList list, DataObject dataObject);
	
	/**
	 * 数据对象添加到列表中的事件。
	 * 
	 * @param dataObject
	 */
	public void onAdded(DataObjectList list, int index, DataObject dataObject);
	
	/**
	 * 添加了一个集合的事件。
	 * 
	 * @param index
	 * @param c
	 */
	public void onAddedAll(DataObjectList list, int index, Collection<? extends DataObject> c);
	
	/**
	 * 数据对象从列表中移除的事件。
	 * 
	 * @param index
	 * @param dataObject
	 */
	public void onRemoved(DataObjectList list, int index, DataObject dataObject);
	
	/**
	 * 使用新的数据对象覆盖index处的数据对象的事件。
	 * 
	 * @param index
	 * @param newDataObject
	 * @param oldDataObject
	 */
	public void onSeted(DataObjectList list, int index, DataObject newDataObject, DataObject oldDataObject);
}
