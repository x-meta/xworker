package xworker.swt.events;

import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ListenerBinder {
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		for(Thing listener : self.getChilds("Listener")){
			String bindTo = listener.getStringBlankAsNull("bindTo");
			if(bindTo == null) {
				continue;
			}
			
			SwtListener lis = new SwtListener(listener, actionContext);
			int type = EventFilterListenersCreator.getListenerType(listener.getString("type"));
			
			for(String controlName : bindTo.split("[,]")) {
				Widget widget = actionContext.getObject(controlName);
				if(widget != null) {
					widget.addListener(type, lis);
				}
			}
		}
		
	}
}
