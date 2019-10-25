package xworker.dataObject;

public interface DataObjectListener {
	/**
	 * 数据对象发生了变化。
	 */
	public void changed(DataObject dataObject);
}
