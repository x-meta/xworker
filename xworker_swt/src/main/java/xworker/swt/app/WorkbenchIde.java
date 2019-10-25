package xworker.swt.app;

import org.eclipse.swt.custom.CTabItem;
import org.xmeta.ActionContext;
import org.xmeta.util.ActionContainer;

/**
 * Workbench作为IDE时要实现的动作和功能等等。
 * 
 * @author zyx
 *
 */
public class WorkbenchIde {
	/**
	 * 控制台的ViewID，默认为console。
	 */
	public static final String CONSOLE_VIEW_ID = "console";
	
	public static void setProcess(ActionContext actionContext) {
		Workbench workbench = actionContext.getObject("workbench");
		if(workbench == null) {
			return;
		}
		
		Process process = actionContext.getObject("process");
		String name = actionContext.getObject("name");
		
		//获取Console对应的Item
		CTabItem consoleItem = workbench.getViewItem(CONSOLE_VIEW_ID);
		if(consoleItem != null) {
			ActionContainer ac = (ActionContainer) consoleItem.getData("component");
			if(ac != null) {
				ac.doAction("setProcess", actionContext, "process", process, "name", name);
			}
		}
	}
}
