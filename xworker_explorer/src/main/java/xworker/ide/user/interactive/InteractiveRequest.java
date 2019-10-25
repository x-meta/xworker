package xworker.ide.user.interactive;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class InteractiveRequest implements Comparable<InteractiveRequest>{
	Thing thing;
	ActionContext actionContext;
	ActionContext requestContext;
	boolean callbak;
	boolean sync;
	long timeout;
	Date createTime = new Date();
	Composite composite;
	String key;
	int requestCount = 1;
	private Object lockObj = new Object();
	
	public InteractiveRequest(Thing thing, ActionContext actionContext, String key) {
		this.thing = thing;
		this.actionContext = actionContext;
		this.key = key;
		
		this.callbak = thing.doAction("getCallback", actionContext);
		this.sync = thing.doAction("getSync", actionContext);
		this.timeout = thing.doAction("getTimeout", actionContext);
	}

	@Override
	public int compareTo(InteractiveRequest o) {
		return o.createTime.compareTo(this.createTime);
	}
	
	public void lock() {
		synchronized(lockObj) {
			try {
				lockObj.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public String getLabel() {
		String label =  thing.getMetadata().getLabel();
		if(requestCount > 1) {
			label = label + "(" + requestCount + ")"; 					
		}
		return label;
	}
	
	public void addRequestCount() {
		requestCount++;
	}
	
	public String getCreateTime() {
		SimpleDateFormat sf = new SimpleDateFormat("MM-dd HH:mm:ss");
		return sf.format(createTime);
	}
		
	public void dispose() {
		if(composite != null && composite.isDisposed() == false) {
			composite.dispose();
		}
	}
	
	public Composite getOrCreate(ActionContext actionContext, Composite parent) {
		if(composite == null || composite.isDisposed()) {
			requestContext = new ActionContext();
			requestContext.put("parent", parent);
			requestContext.put("request", this);
			requestContext.put("parentContext", actionContext);
			
			//创建面板
			Thing mainCompositeThing = World.getInstance().getThing("xworker.ide.user.interactive.InteractivePrototypes/@contentComposite");
			composite = mainCompositeThing.doAction("create", requestContext);
			requestContext.peek().put("parent", requestContext.get("contentComposite"));		
			Thing compositeThing = thing.doAction("getComposite", actionContext);
			if(compositeThing != null) {
				compositeThing.doAction("create", requestContext);
			}
			
			//创建按钮
			requestContext.peek().put("parent", requestContext.get("buttonComposite"));		
			List<Thing> buttons = thing.doAction("getButtons", actionContext);
			if(buttons != null) {
				for(Thing button : buttons) {
					button.doAction("create", requestContext);
				}
			}else {
				//创建一个结束按钮
				Thing finishButton = World.getInstance().getThing("xworker.ide.user.interactive.InteractivePrototypes/@Buttons/@okButton");
				finishButton.doAction("create", requestContext);
			}
			
			composite.layout();
		}
		
		return composite;
	}
	
	public Object doAction(String name) {
		return thing.doAction(name, actionContext);
	}
	
	public Object doAction(String name, Object ... params) {
		return thing.doAction(name, actionContext, params);
	}
	
	public void finish() {
		try {
			//触发结束的事件
			thing.doAction("onFinished", actionContext);
			
			//在界面上删除自己
			InteractiveManager manager = InteractiveManager.instance;
			manager.removeRequest(this);
		}finally {		
			//激活等待的线程，如果请求是sync=true，那么请求线程会等待处理
			if(this.isSync()) {
				synchronized(lockObj) {
					lockObj.notifyAll();
				}
			}
		}
	}

	public Thing getThing() {
		return thing;
	}

	public ActionContext getRequestContext() {
		return requestContext;
	}

	public boolean isSync() {
		return sync;
	}

	public long getTimeout() {
		return timeout;
	}
	
	public boolean isTimeout() {
		if(sync == false || timeout <= 0) {
			return false;
		}
		
		return System.currentTimeMillis() - createTime.getTime() > (timeout * 1000);
	}

	public String getKey() {
		return key;
	}
}
