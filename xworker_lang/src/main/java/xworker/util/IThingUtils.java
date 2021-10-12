package xworker.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;
import xworker.lang.executor.Executor;

public interface IThingUtils {
	void searchRegistThings(List<Thing> thingList, Map<String, Thing> context, Thing registorThing, String keyStr, String registType, boolean noDescriptor, boolean parent, ActionContext actionContext);
}
