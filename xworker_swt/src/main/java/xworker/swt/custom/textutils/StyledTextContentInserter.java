package xworker.swt.custom.textutils;

import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.util.SwtDialog;
import xworker.swt.xwidgets.SelectContent;

public class StyledTextContentInserter {
	/**
	 * 打开一个内容插入的界面。
	 * 	
	 * @param title
	 * @param text
	 * @param contents
	 * @param parentContext
	 */
	public static String open(String title, Control text, List<SelectContent> contents, ActionContext parentContext){
		ActionContext actionContext;
		actionContext = new ActionContext();
		actionContext.put("parent", text.getShell());
		actionContext.put("parentContext", contents);
		actionContext.put("contents", contents);
		actionContext.put("text", text);
		
		Thing thing = World.getInstance().getThing("xworker.swt.custom.textutils.StyledTextContentInserter");
		Shell shell = thing.doAction("create", actionContext);
		if(title != null){
			shell.setText(title);
		}
		return (String) SwtDialog.open(shell, actionContext);
	}
	
	/**
	 * 打开一个内容插入的界面。
	 * 	
	 * @param title 标题
	 * @param parentShell 父Shell
	 * @param contents 可选内容
	 * @param parentContext 父变量上下文
	 */
	public static String open(String title, Shell parentShell, List<SelectContent> contents, ActionContext parentContext){
		ActionContext actionContext;
		actionContext = new ActionContext();
		actionContext.put("parent", parentShell);
		actionContext.put("parentContext", contents);
		actionContext.put("contents", contents);
		
		Thing thing = World.getInstance().getThing("xworker.swt.custom.textutils.StyledTextContentInserter");
		Shell shell = thing.doAction("create", actionContext);
		if(title != null){
			shell.setText(title);
		}
		return (String) SwtDialog.open(shell, actionContext);
	}
}
