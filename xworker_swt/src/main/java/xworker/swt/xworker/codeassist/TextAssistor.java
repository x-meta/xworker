package xworker.swt.xworker.codeassist;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.xworker.CodeAssitContent;

/**
 * 对整个Text提供内容的辅助类。
 * 
 * @author zyx
 *
 */
public interface TextAssistor {
	/**
	 * 为代码提供辅助内容。一般是Ctrl+H的内容，如可以输入的文字列表等。
	 * 
	 * @param textAssistor 代码辅助器的名字
	 * @param code  代码
	 * @param cursorIndex 光标所在位置， -1表示没有
	 * @param thing 正在编辑的事物模型
	 * @param actionContext 变量上下文
	 * 
	 * @return 帮助内容列表
	 */
	public List<CodeAssitContent> getContents(String textAssistor, String code, int cursorIndex, Thing thing, ActionContext actionContext);
}
