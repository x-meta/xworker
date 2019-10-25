package xworker.swt.events.custom;

import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.events.BaseListener;

public class IExtendedModifyListener extends BaseListener implements ExtendedModifyListener{

	public IExtendedModifyListener(ActionContext actionContext,
			Thing actionThing) {
		super(actionContext, actionThing);
	}

	@Override
	public void modifyText(ExtendedModifyEvent event) {
		invokeMethod("lineGetSegments", new Object[]{"event", event});		
	}
	
	public static void create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	IExtendedModifyListener listener = new IExtendedModifyListener(actionContext, self);
		listener.addToParent(actionContext, "addExtendedModifyListener", ExtendedModifyListener.class);
		
		actionContext.getScope(0).put(self.getString("name"), listener);       
	}
}
