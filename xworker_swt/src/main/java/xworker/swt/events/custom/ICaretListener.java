package xworker.swt.events.custom;

import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.events.BaseListener;

public class ICaretListener extends BaseListener implements CaretListener{

	public ICaretListener(ActionContext actionContext, Thing actionThing) {
		super(actionContext, actionThing);
	}

	@Override
	public void caretMoved(CaretEvent event) {
		invokeMethod("caretMoved", new Object[]{"event", event});		
	}
	
	public static void create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	ICaretListener listener = new ICaretListener(actionContext, self);
		listener.addToParent(actionContext, "addCaretListener", CaretListener.class);
		
		actionContext.getScope(0).put(self.getString("name"), listener);       
	}
}
