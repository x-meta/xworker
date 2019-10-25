package xworker.ide.assistor;

import java.util.Map;

import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Index;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.lang.command.CommandExecutor;
import xworker.swt.ActionContainer;

/**
 * IDE辅助工具，静态的类。
 * 已经忘了这个是做什么用了，应该被command.CommanderAssistor代替了。
 * 
 * @author Administrator
 *
 */
public class Assistor {
	/**
	 * 当前辅助工具的动作。
	 */
	private static ActionContainer assistorActions;
	/**
	 * 当前辅助工具的变量上下文。
	 */
	private static ActionContext asssistorContext;
	
	/** 事物管理器命令执行器 */
	private static CommandExecutor commandExecutor;
	
	public static boolean isComamndExcecutorValid(){
		if(commandExecutor == null || commandExecutor.getShell() == null ||  commandExecutor.getShell().isDisposed()){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 重新设置命令域，并且激活命令窗口。
	 * 
	 * @param commandDomain
	 * @param actionContext
	 */
	public static void setCommandDomain(Thing commandDomain, ActionContext actionContext){
		if(isComamndExcecutorValid()){
			commandExecutor.setDomain(commandDomain, actionContext);
			commandExecutor.reset();
			commandExecutor.getShell().setActive();			
		}else{
			//commandExecutor = (CommandExecutor) commandDomain.doAction("run", actionContext); 
		}
	}
	
	public static void runCommandDomain(Thing commandDomain, ActionContext actionContext){
		if(isComamndExcecutorValid()){
			commandExecutor.setDomain(commandDomain, actionContext);
			commandExecutor.reset();
			commandExecutor.getShell().setActive();			
		}else{
			commandExecutor = (CommandExecutor) commandDomain.doAction("run", actionContext); 
		}
	}
	
	/**
	 * 设置辅助者。
	 * 
	 * @param assistorActions
	 * @param assistorContext
	 */
	public static void setAssistor(ActionContainer assistorActions, ActionContext assistorContext){
		Assistor.assistorActions = assistorActions;
		Assistor.asssistorContext = assistorContext;
	}
	
	/**
	 * 移除辅助者，不再接收监听。
	 * 
	 * @param assistorActions
	 */
	public static void removeAssistor(ActionContainer assistorActions){
		if(assistorActions == Assistor.assistorActions){
			Assistor.assistorActions = null;
			Assistor.asssistorContext = null;
		}
	}
	/**
	 * 项目树的选择的事件。
	 * 
	 * @param treeItem 树节点 
	 * @param index    节点对应的索引
	 */
	public static void projectTreeSelected(TreeItem treeItem, Index index){
		fireEvent("projectTreeSelected", UtilMap.toMap(new Object[]{"treeItem", treeItem, "index", index}));
	}
	
	/**
	 * 选中了新的事物编辑器，在打开事物或者编辑切换时触发。
	 * 
	 * @param tabItem
	 * @param editorActionContxt
	 * @param editorActions
	 */
	public static void thingEditorSelected(Object tabItem, ActionContext editorActionContxt, ActionContainer editorActions){
		fireEvent("thingEditorSelected", UtilMap.toMap(new Object[]{"tabItem", tabItem, 
				"editorActionContext", editorActionContxt, "editorActions", editorActions}));
	}
	
	/**
	 * 事物编辑器被关掉之后触发的事件。
	 * 
	 * @param tabItem
	 * @param editorActionContxt
	 * @param editorActions
	 */
	public static void thingEditorClosed(Object tabItem, ActionContext editorActionContxt, ActionContainer editorActions){
		fireEvent("thingEditorClosed", UtilMap.toMap(new Object[]{"tabItem", tabItem, 
				"editorActionContext", editorActionContxt, "editorActions", editorActions}));
	}
	
	/**
	 * 在事物编辑器里选中了一个事物后的事件。
	 * 
	 * @param tabItem
	 * @param treeItem
	 * @param thing
	 * @param editorActionContxt
	 * @param editorActions
	 */
	public static void thingEditorThingSelected(Object tabItem, TreeItem treeItem, Thing thing, ActionContext editorActionContxt, ActionContainer editorActions){
		fireEvent("thingEditorThingSelected", UtilMap.toMap(new Object[]{"tabItem", tabItem, 
				"editorActionContext", editorActionContxt, "editorActions", editorActions,
				"thing", thing, "treeItem", treeItem}));
	}
	
	/**
	 * 触发事件。
	 * 
	 * @param eventName
	 * @param params
	 */
	private static void fireEvent(String eventName, Map<String, Object> params){
		if(assistorActions != null){
			assistorActions.doAction(eventName, asssistorContext, params);
		}
	}
}
