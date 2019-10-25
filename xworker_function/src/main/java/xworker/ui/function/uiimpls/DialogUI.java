package xworker.ui.function.uiimpls;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.swt.ActionContainer;
import xworker.swt.design.Designer;
import xworker.ui.function.FunctionRequest;
import xworker.ui.function.FunctionRequestUI;
import xworker.ui.function.FunctionRequestUIFactory;

/**
 * 以对话框的形式运行。
 * 
 * @author Administrator
 *
 */
public class DialogUI extends FunctionRequestUI{
	private static Logger log = LoggerFactory.getLogger(DesignUI.class);
	private Shell dialogShell = null;
	private ActionContext actionContext;
	private FunctionRequest functionRequest;
	
	@Override
	public void createUI(final FunctionRequest functionRequest) {
		this.functionRequest = functionRequest;
		
		openDialog();
	}

	private void openDialog(){
		if(dialogShell != null){
			dialogShell.getDisplay().asyncExec(new Runnable(){
				public void run(){
					dialogShell.forceActive();
				}
			});
			
			return;
		}
		
		//IDE动作
		final Shell parent = Designer.getExplorerShell();
		if(parent == null){
			//log.info("Thing exlplorer shell is null, Designer.getExplorerShell()=null");
		    //return;
		}

		//要打开的事物
		String thingPath = "xworker.ui.function.FunctionRequestDialog";
		final Thing thing = World.getInstance().getThing(thingPath);
		
		if(thing == null){
		    log.info("thing not exists, thingPath=" + thingPath);
		    return;
		}
		
		if(parent != null){
			Display display = Designer.getExplorerDisplay();
			Runnable runnable = new Runnable(){
				public void run(){
					openDialog(thing, parent);
				}
			};
			
		    display.syncExec(runnable);
		}else{			
			openDialog(thing, null);
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed ()) {
			    if (!display.readAndDispatch ()) display.sleep ();
			}
			display.dispose ();
		}
	}
	
	public void openDialog(Thing thing, Shell parent){
		actionContext = new ActionContext();
		actionContext.put("parent", parent);
		
		dialogShell = (Shell) thing.doAction("create", actionContext);
		dialogShell.addDisposeListener(new DisposeListener(){

			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				FunctionRequestUIFactory.removeUI(functionRequest);
			}
			
		});
			
		//注册Handler
		ActionContainer actions = (ActionContainer) actionContext.get("actions");
		actions.doAction("initHandlers", actionContext, UtilMap.toMap(new Object[]{"handlers", handlers}));
		actions.doAction("initFunctionRequest", actionContext, UtilMap.toMap(new Object[]{"request", functionRequest}));
		actions.doAction("hideFunctionTree", actionContext);
		
		dialogShell.setText(functionRequest.getThing().getMetadata().getLabel());
		dialogShell.open();
	}
	
	@Override
	public void forceActive() {
		openDialog();
	}

	@Override
	public void updateRequestUI(final FunctionRequest functionRequest) {
		openDialog();
		
		final ActionContainer actions = (ActionContainer) actionContext.get("actions");
		Display display = Designer.getExplorerDisplay();
		Runnable runnable = new Runnable(){
			public void run(){
				actions.doAction("initFunctionRequest", actionContext, UtilMap.toMap(new Object[]{"request", functionRequest}));
			}
		};
		display.asyncExec(runnable);
	}

	@Override
	public void close() {
		dialogShell.dispose();
	}

}
