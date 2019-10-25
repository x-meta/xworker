package xworker.lang.actions.thing;

import org.xmeta.Thing;
import org.xmeta.cache.ThingCache;
import org.xmeta.ActionContext;

public class ThingCacheActions {
	public static void removeThingCache(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String thingPath = self.doAction("getThingPath", actionContext);
		if(thingPath != null) {
			ThingCache.remove(thingPath);
		}
	}
}
