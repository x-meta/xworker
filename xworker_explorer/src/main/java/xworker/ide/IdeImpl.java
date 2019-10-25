package xworker.ide;

import java.io.File;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilFile;
import org.xmeta.util.UtilMap;

import xworker.swt.ActionContainer;
import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.UtilBrowser;
import xworker.util.IIde;

public class IdeImpl implements IIde, Listener{
	private static Logger logger = LoggerFactory.getLogger(IdeImpl.class);
	
	Display display;
	ActionContext actionContext;
	Shell shell;
	boolean isThingExplorer = true;
	
	public IdeImpl(final Display display, ActionContext actionContext){
		this.display = display;
		this.actionContext = actionContext;
		
		//增加键盘事件的过滤器，如实现快捷点打开帮助小精灵
		final IdeImpl ide = this;
		display.asyncExec(new Runnable(){
			public void run(){
				display.addFilter(SWT.KeyDown, ide);
			}
		});
		
	}
	
	@Override
	public void ideOpenThing(final Thing thing) {
		display.asyncExec(new Runnable(){
			public void run(){
				ActionContainer actions = actionContext.getObject("actions");
				if(actions != null){
					actions.doAction("openThing", actionContext, "thing", thing);
				}
				
				if(shell != null){
					shell.forceActive();
				}
			}
		});
	}
	
	@Override
	public ActionContext getActionContext() {
		return actionContext;
	}

	@Override
	public void ideOpenThingAndSelectCodeLine(final Thing thing,
			final String codeAttrName, final int line) {
		display.asyncExec(new Runnable(){
			public void run(){
				ActionContainer actions = actionContext.getObject("actions");
				ActionContext ac = actions.getActionContext();
				Shell shell = (Shell) ac.get("shell");
				if(shell != null){
					shell.forceActive();
				}
				ActionContext bin = (ActionContext)actions.doAction("openThing", UtilMap.toMap(new Object[]{"thing", thing}));
				
				if(bin != null){
					ActionContext modelBin = (ActionContext) bin.get("currentModelContext");
					StyledText input = (StyledText) modelBin.get(codeAttrName + "Input");
	                if(input != null){
	                	try{
		                	int start = input.getOffsetAtLine(line);
		                	int end = start + input.getLine(line).length();
	                        input.setCaretOffset(start);
	                        input.setSelection(start, end);
	                        input.showSelection();
	                	}catch(Exception e){
	                		
	                	}
	                }
				}
			}
		});
	}

	@Override
	public void ideDoAction(String name,
			Map<String, Object> parameters) {
		Display explorerDisplay = display;//Designer.getExplorerDisplay();
		ActionContainer explorerActions = actionContext.getObject("actions");//Designer.getExplorerActions();
		if(explorerDisplay != null && explorerActions != null){
			explorerDisplay.asyncExec(new UtilBrowser.Run(name, parameters, explorerActions));
		}else{
			throw new ActionException("Explorer display or explorerActions not exists");
		}
	}	

	@Override
	public Object getIDEShell() {
		if(shell == null){
			return display.getActiveShell();
		}
		return shell;
	}

	public void setShell(Shell shell){
		this.shell = shell;
	}
	
	@Override
	public void ideShowMessageBox(final String title, final String message,	final int style) {
		Display explorerDisplay = display;//Designer.getExplorerDisplay();
		if(explorerDisplay != null){
			explorerDisplay.asyncExec(new Runnable(){
				public void run(){					
					MessageBox box = new MessageBox(Designer.getExplorerDisplay().getShells()[0], style);
					box.setText(title);
					box.setMessage(message);
					SwtUtils.openDialog(box, null, actionContext);
					//box.open();
				}
			});
		}else{
			throw new ActionException("Explorer display not exists");
		}
	}

	@Override
	public void ideOpenFile(final File file){
		display.asyncExec(new Runnable(){
			public void run(){
				try{
					ActionContainer actions = actionContext.getObject("actions");
					if(actions != null){
						String thingPath = UtilFile.getThingPathByFile(file);
						if(thingPath != null){
							Thing thing = World.getInstance().getThing(thingPath);
							actions.doAction("openThing", actionContext, "thing", thing);
						}else{
							actions.doAction("openTextFile", actionContext, "file", file);
						}
					}
					
					if(shell != null){
						shell.forceActive();
					}
				}catch(Exception e){
					logger.error("Open file error, file=" + file.getAbsolutePath(), e);
				}
			}
		});
	}

	@Override
	public boolean isThingExplorer() {
		return isThingExplorer;
	}
	
	public void setIsThingExplorer(boolean value){
		this.isThingExplorer = value;
	}

	@Override
	public xworker.lang.actions.ActionContainer getActionContainer() {
		return actionContext.getObject("actions");
	}
	
	@Override
	public void handleEvent(Event event) {
		if(event.type == SWT.KeyDown){
			if((event.keyCode == 'h' || event.keyCode == 'H') && (event.stateMask & SWT.CTRL) == SWT.CTRL && (event.stateMask & SWT.ALT) == SWT.ALT){
				Action action = World.getInstance().getAction("xworker.ide.worldExplorer.swt.SimpleExplorer/@shell1/@mainComposite/@mainCoolBar1/@mainCollItem/@mainToolBar/@commandAssistorItem/@listeners/@openCommander/@openAssistor");
				action.run(actionContext);
			}
		}
	}

	@Override
	public boolean isDisposed() {
		return shell == null || shell.isDisposed();
	}
}
