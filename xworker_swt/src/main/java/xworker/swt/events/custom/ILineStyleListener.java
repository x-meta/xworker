package xworker.swt.events.custom;

import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.events.BaseListener;

public class ILineStyleListener extends BaseListener implements LineStyleListener {

	public ILineStyleListener(ActionContext actionContext, Thing actionThing) {
		super(actionContext, actionThing);
	}

	@Override
	public void lineGetStyle(LineStyleEvent event) {
		invokeMethod("lineGetStyle", new Object[]{"event", event});		
	}
	
	public static void create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	ILineStyleListener listener = new ILineStyleListener(actionContext, self);
		listener.addToParent(actionContext, "addLineStyleListener", LineStyleListener.class);
		
		actionContext.getScope(0).put(self.getString("name"), listener);       
	}

}
