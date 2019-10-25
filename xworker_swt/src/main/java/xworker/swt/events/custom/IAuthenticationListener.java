package xworker.swt.events.custom;

import org.eclipse.swt.browser.AuthenticationEvent;
import org.eclipse.swt.browser.AuthenticationListener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.events.BaseListener;

public class IAuthenticationListener extends BaseListener  implements AuthenticationListener{

	public IAuthenticationListener(ActionContext actionContext,	Thing actionThing) {
		super(actionContext, actionThing);
	}

	@Override
	public void authenticate(AuthenticationEvent event) {
		invokeMethod("authenticate", new Object[]{"event", event});
	}
	
    public static void create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	IAuthenticationListener listener = new IAuthenticationListener(actionContext, self);
		listener.addToParent(actionContext, "addAuthenticationListener", AuthenticationListener.class);
		
		actionContext.getScope(0).put(self.getString("name"), listener);       
	}

}
