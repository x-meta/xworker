package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.kstream.ValueTransformerWithKeySupplier;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionValueTransformerWithKeySupplier implements ValueTransformerWithKeySupplier<Object, Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ActionValueTransformerWithKeySupplier(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ActionValueTransformerWithKeySupplier create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ActionValueTransformerWithKeySupplier(self, actionContext);
	}

	@Override
	public ValueTransformerWithKey<Object, Object, Object> get() {
		return new ActionValueTransformerWithKey(thing, actionContext);
	}

}
