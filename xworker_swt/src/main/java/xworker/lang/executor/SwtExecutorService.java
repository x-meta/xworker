package xworker.lang.executor;

import org.eclipse.swt.custom.CTabFolder;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.executor.services.AbstractLogService;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

public class SwtExecutorService extends AbstractLogService{
	LogViewer logViewer;
	UIHandler uiHandler;
	
	public SwtExecutorService(LogViewer logViewer, UIHandler uiHandler) {
		this.logViewer = logViewer;
		this.uiHandler = uiHandler;
	}
	
	@Override
	public void requestUI(Thing request, ActionContext actionContext) {
		if(uiHandler != null) {
			uiHandler.handleUIRequest(request, actionContext);
		}
		
	}

	@Override
	public void log(final byte level, final String msg) {
		if(logViewer != null) {
			logViewer.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						logViewer.log(level, msg);
					}catch(Exception e) {						
					}
				}
			});			
		}
	}

	public static void create(ActionContext actionContext) {
		LogViewer logViewer = actionContext.getObject("logViewer");
		UIHandler uiHandler = actionContext.getObject("uiHandler");
		
		SwtExecutorService executorService = new SwtExecutorService(logViewer, uiHandler);
		actionContext.g().put("executorService", executorService);
	}

	/**
	 * 作为SWT的控件创建。
	 * 
	 * @param actionContext
	 */
	public static void createControl(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		//使用独立的变量上下文
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		ac.put("parentContext", actionContext);
		
		Object parent = actionContext.get("parent");
		World world = World.getInstance();
		
		if(parent instanceof CTabFolder && self.getBoolean("createItemsOnCTabFodler")) {
			String[] things = new String[] {
					"xworker.lang.executor.swt.SWTExecutor/@mainTabFolder/@requestTabItem",
					"xworker.lang.executor.swt.SWTExecutor/@mainTabFolder/@logItem"
			};
			for(String path : things) {
				ac.peek().put("parent", actionContext.get("parent"));
				Thing thing = World.getInstance().getThing(path);
				thing.doAction("create", ac);
			}
			
			Thing init = world.getThing("xworker.lang.executor.swt.SWTExecutor/@mainTabFolder/@init");
			init.getAction().run(ac);
		}else {
			Thing prototype = world.getThing("xworker.lang.executor.swt.SWTExecutor/@mainTabFolder");
			ThingCompositeCreator c1 = SwtUtils.createCompositeCreator(self, ac);
			c1.setCompositeThing(prototype);
			c1.create();
		}
		
		//保存变量
		actionContext.g().put(self.getMetadata().getName(), ac.get("executorService"));
	}
	
	@Override
	public Thread getThread() {
		if(uiHandler != null) {
			return uiHandler.getThread();
		}else {
			return null;
		}
	}

	@Override
	public void print(final Object message) {
		if(logViewer != null) {
			logViewer.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						logViewer.print(message);
					}catch(Exception e) {						
					}
				}
			});		
			
		}
	}

	@Override
	public void println(final Object message) {
		if(logViewer != null) {
			logViewer.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						logViewer.println(message);
					}catch(Exception e) {						
					}
				}
			});		
		}
	}

	@Override
	public void errPrint(final Object message) {
		if(logViewer != null) {
			logViewer.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						logViewer.errPrint(message);
					}catch(Exception e) {						
					}
				}
			});		
		}
	}

	@Override
	public void errPrintln(final Object message) {
		if(logViewer != null) {
			logViewer.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						logViewer.errPrintln(message);
					}catch(Exception e) {						
					}
				}
			});		
		}
	}

	@Override
	public void removeRequest(Thing request, ActionContext actionContext) {
		if(uiHandler != null) {
			uiHandler.removeRequest(request, actionContext);
		}
	}

}
