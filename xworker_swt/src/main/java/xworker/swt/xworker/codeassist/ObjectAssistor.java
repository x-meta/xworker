package xworker.swt.xworker.codeassist;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.VariableDesc;
import xworker.swt.xworker.CodeAssitContent;

/**
 * 提供代码辅助内容的提供者。
 * 
 * @author zyx
 *
 */
public interface ObjectAssistor {
	/**
	 * 返回对应的辅助内容。
	 * 
	 * @param thing
	 * @param actionContext
	 * @return
	 */
	public List<CodeAssitContent> getContents(VariableDesc var, Thing thing, ActionContext actionContext);
}
