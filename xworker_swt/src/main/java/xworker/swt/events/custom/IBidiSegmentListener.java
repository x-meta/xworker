package xworker.swt.events.custom;

import org.eclipse.swt.custom.BidiSegmentEvent;
import org.eclipse.swt.custom.BidiSegmentListener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.events.BaseListener;

public class IBidiSegmentListener extends BaseListener implements BidiSegmentListener{

	public IBidiSegmentListener(ActionContext actionContext, Thing actionThing) {
		super(actionContext, actionThing);
	}

	@Override
	public void lineGetSegments(BidiSegmentEvent event) {
		invokeMethod("lineGetSegments", new Object[]{"event", event});		
	}
	
	public static void create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	IBidiSegmentListener listener = new IBidiSegmentListener(actionContext, self);
		listener.addToParent(actionContext, "addBidiSegmentListener", BidiSegmentListener.class);
		
		actionContext.getScope(0).put(self.getString("name"), listener);       
	}

}
