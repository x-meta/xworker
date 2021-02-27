package xworker.swt.util;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;
import xworker.swt.ActionContainer;

public class QuickWidgetUtils {
	private static final String TAG = QuickWidgetUtils.class.getName();
	
	public static void invokeEvent(Object event, Thing thing, String name, ActionContext actionContext) {
		try{
			String actionContainer = thing.getStringBlankAsNull("actionContainer");
			String actionName = thing.getStringBlankAsNull("actionName");
			if(actionName != null && actionContainer != null){
				ActionContainer ac = (ActionContainer) actionContext.get(actionContainer);
				if(ac != null){
					ac.doAction(actionName, actionContext, "event", event, "widgetThing", thing, "menuThing", thing);
				}
			}else{
				Thing acThing = thing.doAction("getActionThing", actionContext);
				if(acThing != null) {
					acThing.getAction().run(actionContext, "event", event, "widgetThing", thing, "menuThing", thing);
				}else {
					thing.doAction(name, actionContext, "event", event, "widgetThing", thing, "menuThing", thing);
				}
			}
		}catch(Exception e){
			Executor.warn(TAG, "Invoke quick widget event error， path=" + thing.getMetadata().getPath(), e);
		}
	}
}
