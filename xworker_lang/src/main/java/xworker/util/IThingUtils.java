package xworker.util;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public interface IThingUtils {
	public List<Thing> searchRegistThings(Thing registorThing, String registType, List<String> keywords, ActionContext actionContext);
	
	public List<Thing> searchRegistThings(Thing registorThing, String registType, List<String> keywords, boolean parent,  ActionContext actionContext);
	
	public List<Thing> searchRegistThings(Thing registorThing, String registType, List<String> keywords, boolean parent,  boolean noDescriptor, ActionContext actionContext);
}
