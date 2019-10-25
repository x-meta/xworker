package xworker.swt.events.custom;

import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.events.BaseListener;

public class ILineBackgroundListener extends BaseListener implements LineBackgroundListener{

	public ILineBackgroundListener(ActionContext actionContext,
			Thing actionThing) {
		super(actionContext, actionThing);
	}

	@Override
	public void lineGetBackground(LineBackgroundEvent event) {
		invokeMethod("lineGetBackground", new Object[]{"event", event});		
	}
	
	public static void create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	ILineBackgroundListener listener = new ILineBackgroundListener(actionContext, self);
		listener.addToParent(actionContext, "addLineBackgroundListener", LineBackgroundListener.class);
		
		actionContext.getScope(0).put(self.getString("name"), listener);       
	}
}
