package xworker.lang.state;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.actions.ActionContainer;
import xworker.lang.executor.Executor;
import xworker.lang.executor.ExecutorService;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;
import xworker.util.XWorkerUtils;

public class StateComposite {
	public static final String key = "__state_composite__";
	private static final String TAG = StateComposite.class.getName();
	
	Composite contentComposite;
	ExecutorService executorService;
	ActionContext actionContext;
	SashForm sashForm;
	Shell shell;
	//内容上下文，每次创建界面是都会独立创建。
	ActionContext contentContext;
	
	public StateComposite() {
		actionContext = new ActionContext();
	}
	
	public StateComposite(ActionContext actionContext) {
		this.actionContext = actionContext;
		init();
	}
	
	private void init() {
		this.contentComposite = actionContext.getObject("contentComposite");
		this.executorService = actionContext.getObject("executorService");
		this.sashForm = actionContext.getObject("sashForm");		
	}
	
	public void createContentComposite(final Thing thing, final State state, final Thing compositeThing) {
		if(state == null || compositeThing == null) {
			return;
		}
		state.setPause(true);		
		if(isDisposed()) {
			createShell(thing, state, compositeThing);
		}else {
			contentComposite.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						if(contentComposite.isDisposed()) {
							createShell(thing, state, compositeThing);
							return;
						}
						
						//销毁现存的
						for(Control control : contentComposite.getChildren()) {
							control.dispose();
						}
						
						contentContext = new ActionContext();
						contentContext.put("parent", contentComposite);
						contentContext.put("state", state);
						contentContext.put("compositeThing", thing);
						contentContext.put("stateComposite", this);
						
						state.setExecutorService(executorService);
						compositeThing.doAction("create", contentContext);
						contentComposite.layout();
						
						ActionContainer actions = actionContext.getObject("actions");
						actions.doAction("restore", contentContext);
					}catch(Exception e) {
						Executor.warn(TAG, "Create composite error, state=" + state
								+ ",compositeThing=" + compositeThing.getMetadata().getPath(), e);
					}
				}
			});
		}
	}
	
	public Composite getContentComposite() {
		return contentComposite;
	}
	
	public ActionContext getContentContext() {
		return contentContext;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public SashForm getSashForm() {
		return sashForm;
	}
	
	public boolean isDisposed() {
		return contentComposite == null || contentComposite.isDisposed();
	}
	
	private Shell createShell(final Thing thing, final State state, final Thing compositeThing) {
		Object xshell = XWorkerUtils.getIDEShell();
		Shell parent = null;
		if(xshell != null && xshell instanceof Shell) {
			parent = (Shell) xshell;
		}
		
		Display display = null;
		actionContext = new ActionContext();
		actionContext.put("compositeThing", thing);
		actionContext.put("state", state);
		actionContext.put("stateComposite", this);
		if(parent != null && parent.isDisposed() == false) {
			actionContext.put("parent", shell);
			display = parent.getDisplay();
		}else {
			display = Display.getDefault();
			actionContext.put("parent", display);
		}

		display.asyncExec(new Runnable() {
			public void run() {
				Thing shellThing = World.getInstance().getThing("xworker.lang.state.prototypes.StateShell");
				
				//actionContext.put("parent", XWorkerUtils.getIDEShell());
				shell = shellThing.doAction("create", actionContext);
				
				shell.setVisible(true);
				shell.setText(state.getThing().getMetadata().getLabel());
				shell.addListener(SWT.Dispose, new Listener() {
					@Override
					public void handleEvent(Event arg0) {
						//自己创建的窗口如果关闭里，继续State，避免卡死
						if(state != null && state.isRunning()) {							
							state.setPause(false);
						}
					}					
				});
				
				init();
				
				createContentComposite(thing, state, compositeThing);
			}
		});
		
		return shell;
	}

	public Shell getShell() {
		return shell;
	}

	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ThingCompositeCreator cc = SwtUtils.createCompositeCreator(self, actionContext);
		cc.setCompositeThing(World.getInstance().getThing("xworker.lang.state.prototypes.StateShell/@mainComposite"));
		Object composite = cc.create();
		
		Thing stateThing = self.doAction("getState", actionContext);
		if(stateThing != null) {
			State state = new State(stateThing, actionContext);
			StateComposite sc = new StateComposite(cc.getNewActionContext());
			state.getRoot().set(key, sc);
			state.setExecutorService(sc.getExecutorService());
			state.runAsTask();
		}
		
		return composite;
	}
}
