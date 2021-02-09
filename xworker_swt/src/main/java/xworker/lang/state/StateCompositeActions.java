package xworker.lang.state;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.XWorkerUtils;

public class StateCompositeActions {
	public static void execComposite(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		State state = actionContext.getObject("state");
		
		StateComposite sc = state.getRoot().get(StateComposite.key);
		if(sc == null) {
			sc = new StateComposite();
			state.getRoot().set(StateComposite.key, sc);
		}
				
		state.setExecutorService(sc.getExecutorService());
		Thing compositeThing = self.doAction("getComposite", actionContext);
		sc.createContentComposite(self, state, compositeThing);
	}
	
	public static void execQuickComponent(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		State state = actionContext.getObject("state");
		
		StateComposite sc = state.getRoot().get(StateComposite.key);
		if(sc == null) {
			sc = new StateComposite();
			state.getRoot().set(StateComposite.key, sc);
		}
				
		state.setExecutorService(sc.getExecutorService());
		sc.createContentComposite(self, state, self);
	}
	
	public static void execDisplayExec(final ActionContext actionContext) {
		final Thing self = actionContext.getObject("self");
		final State state = actionContext.getObject("state");			
		
		Widget widget = self.doAction("getWidgetForDisplay", actionContext);
		Boolean sync = self.doAction("isAysnc", actionContext);
		
		Display display = null;
		if(widget != null) {
			display = widget.getDisplay();
		}else {			
			StateComposite sc = state.getRoot().get(StateComposite.key);
			if(sc != null && sc.isDisposed() == false) {
				display = sc.getContentComposite().getDisplay();
			}
			
			if(display == null) {
				display = ((Shell) XWorkerUtils.getIDEShell()).getDisplay();
			}
		}
		
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					for(Thing child : self.getChilds()) {
						child.getAction().run(actionContext, "state", state);
						
						if(state.doit == false) {
							break;
						}
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		};
		if(sync != null && sync) {
			display.syncExec(runnable);
		}else {
			display.asyncExec(runnable);
		}
	}
}
