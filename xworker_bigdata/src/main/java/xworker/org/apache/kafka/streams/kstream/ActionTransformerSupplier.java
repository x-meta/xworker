package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionTransformerSupplier implements TransformerSupplier<Object, Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ActionTransformerSupplier(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ActionValueTransformerSupplier create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ActionValueTransformerSupplier(self, actionContext);
	}

	@Override
	public Transformer<Object, Object, Object> get() {
		return new ActionTransformer(thing, actionContext);
	}


}
