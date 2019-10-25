package xworker.swt.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.ActionContainer;

public class QuickWidgetUtils {
	private static Logger logger = LoggerFactory.getLogger(QuickWidgetUtils.class);
	
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
			logger.warn("Invoke quick widget event error， path=" + thing.getMetadata().getPath(), e);
		}
	}
}
