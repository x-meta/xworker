package xworker.notification;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

public class SwtNotificationManagerListener implements NotificationManagerListener{
	Thing thing;
	ActionContext actionContext;
	
	public SwtNotificationManagerListener(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	public boolean isDisposed(){
		return UtilData.isTrue(thing.doAction("isDisposed", actionContext));
	}
	
	@Override
	public void added(Notification event) {
		if(isDisposed()){
			NotificationManager.removeListener(this);
		}else{
			thing.doAction("added", actionContext, "notification", event);
		}
	}

	@Override
	public void removed(Notification event) {
		if(isDisposed()){
			NotificationManager.removeListener(this);
		}else{
			thing.doAction("removed", actionContext, "notification", event);
		}
	}

	@Override
	public void updated(Notification event) {
		if(isDisposed()){
			NotificationManager.removeListener(this);
		}else{
			thing.doAction("updated", actionContext, "notification", event);
		}
	}
}
