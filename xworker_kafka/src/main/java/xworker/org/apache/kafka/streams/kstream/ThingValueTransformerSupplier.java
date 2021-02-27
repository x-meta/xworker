package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.kstream.ValueTransformerSupplier;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingValueTransformerSupplier implements ValueTransformerSupplier<Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ThingValueTransformerSupplier(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ThingValueTransformerSupplier create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ThingValueTransformerSupplier(self, actionContext);
	}

	@Override
	public ValueTransformer<Object, Object> get() {
		return new ThingValueTransformer(thing, actionContext);
	}


}
