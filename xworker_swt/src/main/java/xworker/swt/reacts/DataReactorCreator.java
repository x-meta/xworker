package xworker.swt.reacts;

import org.xmeta.ActionContext;

/**
 * 创建DataReactor的接口。
 * 
 * @author zyx
 *
 */
public interface DataReactorCreator {
	/**
	 * 创建DataReactor。 
	 * 
	 * @param control
	 * @param action 动作
	 * @param actionContext
	 * @return
	 */
	public DataReactor create(Object control, String action, ActionContext actionContext);	
}
