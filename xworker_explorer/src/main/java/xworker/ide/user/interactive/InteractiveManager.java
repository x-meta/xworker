package xworker.ide.user.interactive;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

import xworker.task.TaskManager;
import xworker.util.XWorkerUtils;

public class InteractiveManager implements Runnable{
	private static Logger logger = LoggerFactory.getLogger(InteractiveManager.class);
	
	Shell shell;
	ActionContext actionContext = new ActionContext();
	static InteractiveManager instance = new InteractiveManager();
	List<InteractiveRequest> requests = new ArrayList<InteractiveRequest>();
	
	public InteractiveManager() {
		actionContext.put("requests", requests);
		
		TaskManager.getScheduledExecutorService().scheduleWithFixedDelay(this, 0, 1, TimeUnit.SECONDS);
	}
	
	public static void sendRequest(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		sendRequest(self, actionContext);
	}
	
	public static void sendRequest(Thing thing, ActionContext actionContext) {
		instance.doRequest(thing, actionContext);
	}
	
	public void run() {
		//System.out.println("check time out");
		
		List<InteractiveRequest> timeOutRequests = new ArrayList<InteractiveRequest>();
		//检测超时
		synchronized(requests) {
			for(InteractiveRequest request : requests) {
				if(request.isTimeout()) {
					timeOutRequests.add(request);
				}
			}
		}
		
		if(timeOutRequests.size() > 0) {
			shell.getDisplay().syncExec(new TimeoutFinisher(timeOutRequests));
		}
	}
	
	private InteractiveRequest getRequest(Thing thing, ActionContext actionContext) {
		String key = thing.doAction("getKey", actionContext);
		if(key == null || "".equals(key)) {
			return createRequest(thing, actionContext, null);
		}
		
		for(InteractiveRequest request : requests) {
			if(key.equals(request.getKey())) {
				request.addRequestCount();
				return request;
			}
		}
		
		return createRequest(thing, actionContext, key);
	}
	
	private InteractiveRequest createRequest(Thing thing, ActionContext actionContext, String key) {
		InteractiveRequest request = new InteractiveRequest(thing, actionContext, key);
		
		synchronized(requests) {
			requests.add(0, request);
		}
		
		return request;
	}
	public void doRequest(Thing thing, ActionContext actionContext) {
		if(XWorkerUtils.getIDEShell() == null && XWorkerUtils.getIDEShell() instanceof Shell) {
			//不在IDE里不操作，返回，以后需要加提示么？暂时看起来不需要。
			return;
		}
	
		final InteractiveRequest request = getRequest(thing, actionContext);
		Shell ideShell = (Shell) XWorkerUtils.getIDEShell();
		ideShell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					_doRequest(request);
				}catch(Exception e) {
					logger.error("Hanlde user interactive request error, thing=" + request.thing.getMetadata().getPath(), e);
				}
			}
		});
		
		if(request.sync) {
			if(ideShell.getDisplay().getThread() != Thread.currentThread()) {
				request.lock();
				//System.out.println("waked");
			}
		}
	}
	public void _doRequest(InteractiveRequest request) {

		if(shell == null || shell.isDisposed()) {
			createShell();
		}
		
		shell.setVisible(true);
		
		ActionContainer ac = actionContext.getObject("actions");
		ac.doAction("refresh");		
	}
	
	protected void createShell() {
		Shell ideShell = (Shell) XWorkerUtils.getIDEShell();
		actionContext.peek().put("parent", ideShell);		
		
		Thing shellThing = World.getInstance().getThing("xworker.ide.user.interactive.InteractiveUI");
		shell = shellThing.doAction("create", actionContext);
	}
	
	public void removeRequest(final InteractiveRequest request) {
		requests.remove(request);
		
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				ActionContainer ac = actionContext.getObject("actions");
				ac.doAction("removeRequest", actionContext, "request", request);
				ac.doAction("refresh", actionContext);
			}
		});
		
	}

	public static class TimeoutFinisher implements Runnable{
		List<InteractiveRequest> requests;
		public TimeoutFinisher(List<InteractiveRequest> requests) {
			this.requests = requests;
		}
		
		public void run() {
			for(InteractiveRequest request : requests) {
				try {							
					request.finish();
				}catch(Exception e) {
					logger.error("finish timeout request error, request=" 
								+ request.getThing().getMetadata().getPath(), e);
				}
			}
		}
	}
}
