package xworker.swt.util;

import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

/**
 * 兼容RWT的ServerPaushSession;
 * 
 * @author zyx
 *
 */
public class ServerPushSession {
	private static Logger logger = LoggerFactory.getLogger(ServerPushSession.class);
	
	private static Class<?> cls;
	private static Method start;
	private static Method stop;
	
	static {
		if(SwtUtils.isRWT()) {
			try {
				cls = Class.forName("org.eclipse.rap.rwt.service.ServerPushSession");
				start = cls.getDeclaredMethod("start", new Class<?>[] {});
				stop = cls.getDeclaredMethod("stop", new Class<?>[] {});
			}catch(Exception e) {
				logger.error("Init ServerPushSession  exception", e);
			}
		}
	}
	
	protected Object instance = null;
	public ServerPushSession(){
		if(SwtUtils.isRWT()) {
			try {
				instance = cls.getConstructor(new Class<?>[0]).newInstance(new Object[0]);
			} catch (Exception e) {
				logger.error("Init ServerPushSession  exception", e);
			} 
		}
	}
	
	public void start() {
		if(SwtUtils.isRWT() && instance != null) {
			try {
				start.invoke(instance, new Object[] {});
			} catch (Exception e) {
				logger.error("Execute ServerPushSession start exception", e);
			} 
		}
	}
	
	public void stop() {
		if(SwtUtils.isRWT() && instance != null) {
			try {
				stop.invoke(instance, new Object[] {});
			} catch (Exception e) {
				logger.error("Execute ServerPushSession stop exception", e);
			} 
		}
	}

	public static Object create(ActionContext actionContext) {
		if(!SwtUtils.isRWT()) {
			return null;
		}
		
		Thing self = actionContext.getObject("self");
		long interval = self.doAction("getInterval", actionContext);
		if(interval <= 0) {
			return null;
		}
		
		Thing task = World.getInstance().getThing("xworker.swt.rap.ServerPushSession/@actions/@startTask").detach();
		ServerPushSession pushSession = new ServerPushSession();
		pushSession.start();
		task.put("pushSession", pushSession);
		task.put("control", actionContext.get("parent"));
		task.put("period", interval);
		task.put("thing", self);
		Object taskObj = task.getAction().run(actionContext);
		actionContext.g().put(self.getMetadata().getName(), taskObj);
		return taskObj;
	}
	
	public static void doTask(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Thing thing = (Thing) self.get("thing");
		xworker.task.Task task = actionContext.getObject("task");
		try {
			Control control = (Control) self.get("control");
			if(control == null || control.isDisposed()) {
				task.cancel(true);
				return;
			}
			
			ServerPushSession session = (ServerPushSession) self.get("pushSession");
			
			control.getDisplay().asyncExec(new SessionRunable(thing, session));
			control.getDisplay().wake();
		}catch(Exception e) {
			logger.error("ServerPushSession task error, thing=" + thing.getMetadata().getPath(), e);
		}
	}
	
	static class SessionRunable implements Runnable{
		Thing thing;
		ServerPushSession session;
		
		public SessionRunable(Thing thing, ServerPushSession session) {
			this.thing = thing;
			this.session = session;
		}
		
		public void run() {
			try {
				session.stop();
				session.start();
				//System.out.println("Server Push Session run");
			}catch(Exception e) {
				logger.error("ServerPushSession task error, thing=" + thing.getMetadata().getPath(), e);
			}
		}
	}
}
