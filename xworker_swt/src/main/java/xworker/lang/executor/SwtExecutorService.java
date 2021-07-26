package xworker.lang.executor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.executor.services.AbstractLogService;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;
import xworker.util.StringUtils;

public class SwtExecutorService extends AbstractLogService{
	LogViewer logViewer;
	DefaultRequestService requestService;
	CTabItem requestTabItem;
	
	public SwtExecutorService(LogViewer logViewer, DefaultRequestService requestService) {
		this.logViewer = logViewer;
		this.requestService = requestService;
	}

	private void setRequestTabItem(CTabItem requestTabItem){
		this.requestTabItem = requestTabItem;

		if(requestTabItem != null && requestService != null){
			requestService.addListener(new DefaultRequestServiceListener() {
				@Override
				public void requestAdded(DefaultRequestService defaultRequestService, Request request) {
					updateUnReadCount();
				}

				@Override
				public void requestRemoved(DefaultRequestService defaultRequestService, Request request) {
					updateUnReadCount();
				}

				@Override
				public void requestUpdated(DefaultRequestService defaultRequestService, Request request) {
					updateUnReadCount();
				}
			});

			updateUnReadCount();
		}
	}

	private void updateUnReadCount(){
		if(requestTabItem != null && requestService != null && !requestTabItem.isDisposed()){
			requestTabItem.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					try {
						String text = StringUtils.getString("lang:d=请求&en=Request", new ActionContext());

						int unreadCount = requestService.getUnreadCount();
						if(unreadCount > 0){
							text = text + "(" + unreadCount + ")";
						}

						requestTabItem.setText(text);
					} catch (IOException e) {
					}
				}
			});
		}
	}
	
	@Override
	public Request requestUI(Thing thing, ActionContext actionContext) {
		if(requestService != null){
			return requestService.requestUI(thing, actionContext);
		}

		return null;
	}

	@Override
	public void log(final byte level, final String msg) {
		if(logViewer != null && logViewer.getDisplay() != null) {
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

	@Override
	public boolean isSupportRequest() {
		return requestService != null;
	}

	@Override
	public boolean isSupportLog() {
		return logViewer != null;
	}

	public static void create(ActionContext actionContext) {
		LogViewer logViewer = actionContext.getObject("logViewer");
		DefaultRequestService requestService = actionContext.getObject("requestService");
		CTabItem requestTabItem = actionContext.getObject("requestTabItem");

		SwtExecutorService executorService = new SwtExecutorService(logViewer, requestService);
		executorService.setRequestTabItem(requestTabItem);

		actionContext.g().put("executorService", executorService);
	}

	/**
	 * 作为SWT的控件创建。
	 * 
	 * @param actionContext
	 */
	public static Object createControl(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");

		ActionContext ac = null; 
		Object parent = actionContext.get("parent");
		World world = World.getInstance();
		Object result = null;
		
		if(parent instanceof CTabFolder && self.getBoolean("createItemsOnCTabFodler")) {
			//使用独立的变量上下文
			ac = new ActionContext();
			ac.put("parent", actionContext.get("parent"));
			ac.put("parentContext", actionContext);
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
			ThingCompositeCreator c1 = SwtUtils.createCompositeCreator(self, actionContext);
			c1.setCompositeThing(prototype);
			result = c1.create();
			
			ac = c1.getNewActionContext();
		}
		
		//保存变量
		actionContext.g().put(self.getMetadata().getName(), ac.get("executorService"));
		
		return result;
	}
	
	@Override
	public Thread getThread() {
		if(logViewer != null){
			return logViewer.getDisplay().getThread();
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
	public void removeRequest(Request request) {
		if(requestService != null) {
			requestService.removeRequest(request);
		}else {
			super.removeRequest(request);
		}
	}

	@Override
	public List<Request> getRequests() {
		if(requestService != null) {
			return requestService.getRequests();
		}else {
			return Collections.emptyList();
		}
	}

}
