package xworker.swt.xworker.codeassist;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.VariableDesc;

/**
 * 变量提供者，能够提供变量及其类型。
 * 
 * @author zyx
 *
 */
public interface VariableProvider {
	/**
	 * 返回变量定义。
	 * 
	 * @param thing
	 * @param actionContext
	 * 
	 * @return
	 */
	public List<VariableDesc> getVariables(String code, int offset,  List<String> statements, Thing thing, ActionContext actionContext);
}
