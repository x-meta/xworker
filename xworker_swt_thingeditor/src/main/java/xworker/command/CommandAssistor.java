package xworker.command;

import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.rwt.XRWT;
import xworker.swt.util.SwtUtils;
import xworker.util.XWorkerUtils;

public class CommandAssistor {
	private static final String KEY = "__CommandAssistor__SHELL__";
	private static Shell shell = null;
	private static ActionContext actionContext = null;
	
	public static boolean isOpened() {
		if(SwtUtils.isRWT()) {			
			ActionContext actionContext = new ActionContext();
			Shell shell = (Shell) XRWT.getAttribute(KEY, actionContext);
			if(shell == null || shell.isDisposed()){
				return false;
			}else {
				return true;
			}
		}else {
			if(shell == null || shell.isDisposed()){
				return false;
			}else {
				return true;
			}
		}
	}
	
	public static void open(){
		if(SwtUtils.isRWT()) {
	
			ActionContext actionContext = new ActionContext();
			Shell shell = (Shell) XRWT.getAttribute(KEY, actionContext);
			if(shell == null || shell.isDisposed()){
				actionContext.put("parent", XWorkerUtils.getIDEShell());
				
				 Thing thing = World.getInstance().getThing("xworker.command.CommandAssistor");
			     shell = (Shell) thing.doAction("create", actionContext); 
			     XRWT.setAttribute(KEY, shell, actionContext);
			}
			
			shell.setVisible(true);
			shell.forceActive();
		}else {
			if(shell == null || shell.isDisposed()){
				actionContext = new ActionContext();
				actionContext.put("parent", XWorkerUtils.getIDEShell());
				
				 Thing thing = World.getInstance().getThing("xworker.command.CommandAssistor");
			     shell = (Shell) thing.doAction("create", actionContext); 
			}
			
			shell.setVisible(true);
			shell.forceActive();
		}
	}
}
