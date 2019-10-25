package xworker.lang.util;

import java.util.Collections;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.XWorkerUtils;

public class ThingIndexActions {
	@SuppressWarnings("unchecked")
	public static List<Thing> getThings(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		String type = actionContext.getObject("type");
		if(type == null || "".equals(type.trim())) {
			type = "child";
		}
		
		return XWorkerUtils.searchRegistThings(self, type, Collections.EMPTY_LIST, actionContext);
	}
}
