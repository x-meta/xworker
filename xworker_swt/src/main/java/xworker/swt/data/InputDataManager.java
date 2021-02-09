package xworker.swt.data;

import org.eclipse.swt.widgets.Widget;

/**
 * 输入控件的管理器，主要用于从控件上获取值和设置值。
 * 
 * 输入控件管理器默认以__InputDataManager__作为可以保存到控件的data中，有数据管理器自行设置。
 * 
 * @author zhangyuxiang
 *
 */
public interface InputDataManager {
	public static final String KEY = "__InputDataManager__";
			
	public void setValue(Object value);
	
	public Object getValue();	
	
	public static void setInputDataManager(Widget widget, InputDataManager inputDataManager) {
		widget.setData(KEY, inputDataManager);
	}
	
	public static InputDataManager getInputDataManager(Widget widget) {
		return (InputDataManager) widget.getData(KEY);
	}
}
