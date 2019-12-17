package xworker.dataObject.swt;

import java.util.List;
import java.util.Map;

import xworker.dataObject.DataObject;

/**
 * DataObjectCompsoite的接口定义。
 * 
 * @author zhangyuxiang
 *
 */
public interface IDataObjectComposite {
	/**
	 * 设置数据对象。
	 * 
	 * @param dataObject
	 */
	public void setDataObject(DataObject dataObject);
	
	/**
	 * 返回数据对象。
	 * 
	 * @return
	 */
	public DataObject getDataObject();
	
	/**
	 * 设置值，比如设置表单的值。
	 * 
	 * @param values
	 */
	public void setValues(Map<String, Object> values);
	
	/**
	 * 设置部分值，比如设置表单的值。和setValues方法的区别是如果表单的一个字段不在values中，则会保留原值，
	 * 而setValues方法可能会使用默认值或置为空。
	 * 
	 * @param values
	 */
	public void setPartialValues(Map<String, Object> values);
	
	/**
	 * 校验。比如执行表单的校验。
	 */
	public boolean validate();
		
	/**
	 * 插入一组数据对象，比如通过数据对象的表格列表等。
	 * 
	 * @param index -1表示插入到末尾
	 * @param datas
	 */
	public void insert(int index, List<DataObject> datas);
	
	/**
	 * 移除一组数据对象。
	 * 
	 * @param datas
	 */
	public void remove(List<DataObject> datas);
	
	/**
	 * 返回数据对象列表。
	 * 
	 * @return
	 */
	public List<DataObject> getDatas();
}
