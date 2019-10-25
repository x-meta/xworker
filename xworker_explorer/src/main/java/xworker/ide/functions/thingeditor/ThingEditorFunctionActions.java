package xworker.ide.functions.thingeditor;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.swt.ActionContainer;
import xworker.swt.events.SwtListener;

public class ThingEditorFunctionActions {
	public static void selectThingAtOutline(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Thing thing = getThing(self, actionContext);
		ActionContainer actions = getThingActions(self, actionContext);
		
		actions.doAction("selectThing", UtilMap.toMap(new Object[]{"thing", thing, "refresh", true}));
	}
	
	public static void save(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		ActionContainer actions = getThingActions(self, actionContext);
		actions.doAction("save");		
	}

	public static void refreshOutline(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		ActionContainer actions = getThingActions(self, actionContext);
		actions.doAction("refreshOutline",  UtilMap.toMap(new Object[]{"refreshThing", null}));
	}
	
	public static  void openAddChildComposite(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		SwtListener listener = (SwtListener) getObjectFromThingContext("addChildSelectionListener", self, actionContext);
		listener.handleEvent(null);
	}
	
	public static void openEditComposite(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		SwtListener listener = (SwtListener) getObjectFromThingContext("cancelAddChildListener", self, actionContext);
		listener.handleEvent(null);
	}
	
	public static Object getObjectFromThingContext(String name, Thing self, ActionContext actionContext){
		ActionContext thingContext = (ActionContext) actionContext.get("thingContext");
		if(thingContext == null){
			throw new ActionException("may be not a thing editor enviroment, path=" + self.getMetadata().getPath());
		}
		
		return  thingContext.get(name);
	}
	
	private static ActionContainer getThingActions(Thing self, ActionContext actionContext){
		ActionContext thingContext = (ActionContext) actionContext.get("thingContext");
		if(thingContext == null){
			throw new ActionException("may be not a thing editor enviroment, path=" + self.getMetadata().getPath());
		}
		
		return (ActionContainer) thingContext.get("actions");
	}
	
	private static Thing getThing(Thing self, ActionContext actionContext){
		Object thingObj = actionContext.get("thing");
		Thing thing = null;
		if(thingObj instanceof String){
			thing = World.getInstance().getThing((String) thingObj);
		}else if(thingObj instanceof Thing){
			thing = (Thing) thingObj;
		}else{
			throw new ActionException("thing is null or not a thing, path=" + self.getMetadata().getPath());
		}
		
		return thing;
	}
}
