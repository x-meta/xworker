package xworker.html.module;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

public class ModuleProvider {
	ActionContext actionContext;
	Thing thing;
	
	public ModuleProvider(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	public boolean equals(Thing thing) {
		return false;
	}
	
	public boolean accept(String module, String version) {
		return thing.doAction("accept", actionContext, "module", module, "version", version);
	}
	
	public boolean resourceExists() {
		return UtilData.isTrue(thing.doAction("resourceExists", actionContext));
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public Thing getThing() {
		return thing;
	}
	
	public void init() {
		thing.doAction("init", actionContext);
	}
}
