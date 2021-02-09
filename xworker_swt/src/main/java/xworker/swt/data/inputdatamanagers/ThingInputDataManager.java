package xworker.swt.data.inputdatamanagers;

import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.data.InputDataManager;

public class ThingInputDataManager implements InputDataManager{
	Widget parent;
	Thing thing;
	ActionContext actionContext;
	
	public ThingInputDataManager(Widget parent, Thing thing, ActionContext actionContext) {
		this.parent = parent;
		this.thing = thing;
		this.actionContext = actionContext;
		
		InputDataManager.setInputDataManager(parent, this);
	}

	@Override
	public void setValue(Object value) {
		thing.doAction("setValue", actionContext, "parent", parent, "value", value);
	}

	@Override
	public Object getValue() {
		return thing.doAction("getValue", actionContext, "parent", parent);
	}
	
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Object parent = actionContext.getObject("parent");
		if(parent != null && parent instanceof Widget) {
			new ThingInputDataManager((Widget) parent, self, actionContext);
		}
	}
	
}
