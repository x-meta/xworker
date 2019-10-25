package xworker.swt.events.custom;

import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.events.BaseListener;

public class IVerifyKeyListener extends BaseListener implements VerifyKeyListener{

	public IVerifyKeyListener(ActionContext actionContext, Thing actionThing) {
		super(actionContext, actionThing);
	}

	@Override
	public void verifyKey(VerifyEvent event) {
		invokeMethod("verifyKey", new Object[]{"event", event});		
	}
	
	public static void create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	IVerifyKeyListener listener = new IVerifyKeyListener(actionContext, self);
		listener.addToParent(actionContext, "addVerifyKeyListener", VerifyKeyListener.class);
		
		actionContext.getScope(0).put(self.getString("name"), listener);       
	}

}
