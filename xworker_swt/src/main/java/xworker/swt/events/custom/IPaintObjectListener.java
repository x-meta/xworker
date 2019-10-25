package xworker.swt.events.custom;

import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.PaintObjectListener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.events.BaseListener;

public class IPaintObjectListener extends BaseListener implements PaintObjectListener{

	public IPaintObjectListener(ActionContext actionContext, Thing actionThing) {
		super(actionContext, actionThing);
	}

	@Override
	public void paintObject(PaintObjectEvent event) {
		invokeMethod("paintObject", new Object[]{"event", event});		
	}
	
	public static void create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	IPaintObjectListener listener = new IPaintObjectListener(actionContext, self);
		listener.addToParent(actionContext, "addPaintObjectListener", PaintObjectListener.class);
		
		actionContext.getScope(0).put(self.getString("name"), listener);       
	}

}
