package xworker.swt.reacts;

import java.util.List;

public class DataFilter {
	/**
	 * 过滤数据，返回过滤后的数据。
	 * 
	 * @param dataReactor
	 * @param event
	 * @param datas
	 * 
	 * @return
	 */
	public List<Object> filterDatas(DataReactor dataReactor, byte event, List<Object> datas){
		return datas;
	}
	
	/**
	 * 过滤事件，如果可以继续执行返回true，否则返回false。
	 * 
	 * @param dataReactor
	 * @param event
	 * @param datas
	 * @return
	 */
	public boolean filterEvents(DataReactor dataReactor, byte event, List<Object> datas) {
		return true;
	}
}
