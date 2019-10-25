package xworker.swt.browser;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingBrowserFunction extends BrowserFunction implements DisposeListener{
	Thing thing;
	ActionContext actionContext;

	ThingBrowserFunction(Browser browser, String name, Thing thing, ActionContext actionContext) {
		super(browser, name);

		browser.addDisposeListener(this);
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public Object function(Object[] arguments) {		
		return thing.doAction("doFunction", actionContext, "args", arguments);
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Browser browser = actionContext.getObject("parent");
		String name = self.doAction("getName", actionContext);
		
		ThingBrowserFunction function =  new ThingBrowserFunction(browser, name, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), function);
		return function;
	}
	
	@Override
	public void widgetDisposed(DisposeEvent e) {
		if(this.isDisposed() == false) {
			this.dispose();
		}
	}

}
