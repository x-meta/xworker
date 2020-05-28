package xworker.swt.design;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.ActionContainer;

public class DesignDialog implements DisposeListener{
	Shell parentShell;
	Shell shell;
	ActionContext actionContext;
	ActionContainer actionContainer;
	Point location = null;
	
	public DesignDialog(){		
	}
	
	public void close(){
		if(shell != null && !shell.isDisposed()){
			shell.getDisplay().syncExec(new Runnable(){
				public void run(){
					if(shell.isDisposed() == false){
						shell.setVisible(false);						
					}
				}
			});			
		}
	}
	
	/**
	 * 显示一个控件设计对话框。
	 * 
	 * @param control
	 */
	public void show(Control control, Thing tools){
		//如果shell不存在，那么创建
		if(actionContainer == null && shell != null){
			shell.dispose();
		}
		
		if(shell == null || shell.isDisposed()) {// || (parentShell != control.getShell() && control.getShell() != shell)){
			if(shell != null && shell.isDisposed() == false){
				final Shell oldShell = shell;
				oldShell.getDisplay().syncExec(new Runnable(){
					public void run(){
						oldShell.dispose();
					}
				});				
			}
			
			if(control == null || control.isDisposed()) {
				return;
			}
			
			parentShell = control.getShell();//Designer.getExplorerShell();
			actionContext = new ActionContext();
			actionContext.put("explorerActions", Designer.getExplorerActions());
			actionContext.put("explorerContext", Designer.getExplorerActionContext());
			actionContext.put("parent", parentShell);
			 			 			 
			Thing shellThing = World.getInstance().getThing("xworker.swt.design.DesignToolDialog");
			//Thing shellThing = World.getInstance().getThing("xworker.ide.worldexplorer.swt.designer.DesignerToolbarDialog");
			shell = (Shell) shellThing.doAction("create", actionContext);
			shell.addDisposeListener(this);
			if(location != null){
			    //shell.setLocation(location);
			}
			actionContainer = (ActionContainer) actionContext.get("actions");
		}
		
		//初始化Control
		actionContainer.doAction("doInit", actionContext, "newControl", control, "tools", tools);
		
		//显示窗口
	    shell.setVisible(true);
	    shell.setActive();
	}
	
	/**
	 * 如果设计对话框显示的那么展示Control。
	 * 
	 * @param control
	 */
	public void showIfOpened(Control control, Thing tools){
		//if(shell == null || shell.isDisposed() || !shell.isVisible()){
		//	return;
		//}
		
		show(control, tools);
	}
	
	public boolean isOpened(){
		return shell != null && !shell.isDisposed() && shell.isVisible();
	}

	@Override
	public void widgetDisposed(DisposeEvent event) {
		location = ((Shell) event.widget).getLocation();
	}
}
