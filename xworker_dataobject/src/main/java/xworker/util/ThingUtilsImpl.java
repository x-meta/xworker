package xworker.util;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingUtilsImpl implements IThingUtils{

	@Override
	public List<Thing> searchRegistThings(Thing registorThing, String registType, List<String> keywords,
			ActionContext actionContext) {		
		return ThingUtils.searchRegistThings(registorThing, registType, keywords, actionContext);
	}

	@Override
	public List<Thing> searchRegistThings(Thing registorThing, String registType, List<String> keywords, boolean parent,
			ActionContext actionContext) {
		return ThingUtils.searchRegistThings(registorThing, registType, keywords, parent, actionContext);
	}

	@Override
	public List<Thing> searchRegistThings(Thing registorThing, String registType, List<String> keywords, boolean parent,
			boolean noDescriptor, ActionContext actionContext) {
		return ThingUtils.searchRegistThings(registorThing, registType, keywords, parent, noDescriptor, actionContext);
	}

}
