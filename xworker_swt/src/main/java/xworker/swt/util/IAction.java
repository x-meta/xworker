package xworker.swt.util;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

/**
 * 动作接口。
 * 
 * @author zyx
 *
 */
public interface IAction {
	public Object run(Thing thing, ActionContext actionContext);
}
