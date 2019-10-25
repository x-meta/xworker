package xworker.chart.jfree.dataobject.data;

import java.util.List;

import xworker.dataObject.DataObject;

/**
 * 可重新加载数据对象的数据集。
 * 
 * @author Administrator
 *
 */
public interface DataObjectReloadableDataset {
	public void reload(List<DataObject> datas);
}
