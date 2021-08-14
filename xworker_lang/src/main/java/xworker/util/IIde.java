package xworker.util;

import java.io.File;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.actions.ActionContainer;

/**
 * IDE的接口，这是XWorkerUtils中常用的方法，由于XWorkerUtils集成在core项目中了，所以只要抽象成了接口，需要在IDE项目中实现。
 * 
 * @author zyx
 *
 */
public interface IIde {
	int ICON_ERROR = 1;
	int ICON_INFORMATION = 2;
	int ICON_QUESTION = 4;
	int ICON_WARNING = 8;
	int ICON_WORKING = 16;
	int ICON_SEARCH = 512;
	int ICON_CANCEL = 256;

	int OK = 32;
	int YES = 64;
	int NO = 128;
	int CANCEL = 256;
	int ABORT = 512;
	int RETRY = 1024;
	int IGNORE = 2048;
	int OPEN = 4096;
	int SAVE = 8192;

	/**
	 * 在IDE中打开一个文件。
	 * 
	 * @param file 要打开的文件
	 */
	void ideOpenFile(File file);
	
	/**
	 * 在IDE中打开一个事物。
	 * 
	 * @param thing 要打开的事物
	 */
	void ideOpenThing(Thing thing);
	
	/**
	 * 在IDE中打开一个动作事物，并且这个动作如果有代码，那么在代码编辑器中定位到指定的行上。
	 * 
	 * @param thing 事物
	 * @param codeAttrName 代码属性名
	 * @param line 代码的行号
	 */
	void ideOpenThingAndSelectCodeLine(final Thing thing, final String codeAttrName, final int line);
	
	/**
	 * 指定IDE的指定动作。
	 * 
	 * @param name 动作名
	 * @param parameters 参数
	 */
	void ideDoAction(String name, Map<String, Object> parameters);
	
	/**
	 * 返回IDE的动作容器。
	 */
	ActionContainer getActionContainer();
	
	/**
	 * 返回IDE所在的变量上下文。
	 */
	ActionContext getActionContext();
	
	/**
	 * 获取IDE的窗口。
	 * 
	 * @return 窗口对象，应该是SWT的Shell对象
	 */
	Object getIDEShell();
	
	/**
	 * 在IDE中显示一条消息，通常是一个提示信息。
	 * 
	 * @param title 消息的标题
	 * @param message 消息的内容
	 * @param style 消息窗口的类型，可参看SWT的MessageBox的类型设置
	 */
	void ideShowMessageBox(final String title, final String message, final int style, final Callback<Integer, Void> callback);
	
	/**
	 * 是否是事物管理器，如果不是返回false。如一些简单的应用可能也要实现此接口，但应该返回false。
	 */
	boolean isThingExplorer();
	
	/**
	 * 返回是否已经被销毁了。
	 */
	boolean isDisposed();
}
