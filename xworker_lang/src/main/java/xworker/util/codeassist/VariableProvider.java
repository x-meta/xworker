package xworker.util.codeassist;

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
	 * @param code 当前代码，比如文本框中的代码
	 * @param offset 光标在代码的偏移量
	 * @param statements 当前偏移量前分析出的语句，比如方法调用等
	 * @param thing 当前模型，如果不是模型编辑器等可能为null
	 * @param actionContext 动作上下文
	 * 
	 * @return 返回可用的变量列表
	 */
	public List<VariableDesc> getVariables(String code, int offset,  List<String> statements, Thing thing, ActionContext actionContext);
}
