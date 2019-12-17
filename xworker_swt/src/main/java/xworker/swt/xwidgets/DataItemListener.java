package xworker.swt.xwidgets;

public interface DataItemListener {
	/**
	 * 选中了一个DataItem。
	 * 
	 * @param container
	 * @param dataItem
	 */
	public void onSelection(DataItemContainer container, DataItem dataItem);
	
	/**
	 * 选中了一个DataItem。
	 * 
	 * @param container
	 * @param dataItem
	 */
	public void onDefaultSelection(DataItemContainer container, DataItem dataItem);
}
