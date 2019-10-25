package xworker.swt.events.custom;

import org.eclipse.swt.custom.MovementEvent;
import org.eclipse.swt.custom.MovementListener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.events.BaseListener;

public class IMovementListener extends BaseListener implements MovementListener{

	public IMovementListener(ActionContext actionContext, Thing actionThing) {
		super(actionContext, actionThing);
	}

	@Override
	public void getNextOffset(MovementEvent event) {
		invokeMethod("getNextOffset", new Object[]{"event", event});
	}

	@Override
	public void getPreviousOffset(MovementEvent event) {
		invokeMethod("getPreviousOffset", new Object[]{"event", event});	
	}

	public static void create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	IMovementListener listener = new IMovementListener(actionContext, self);
		listener.addToParent(actionContext, "addWordMovementListener", MovementListener.class);
		
		actionContext.getScope(0).put(self.getString("name"), listener);       
	}
}
