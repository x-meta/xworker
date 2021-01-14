package xworker.org.apache.kafka.streams.processor;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionProcessor implements Processor<Object, Object>{
	Thing thing;
	ActionContext actionContext;
	ProcessorContext context;
	
	public ActionProcessor(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ActionProcessor create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ActionProcessor(self, actionContext);
	}


	@Override
	public void close() {
		thing.doAction("close", actionContext, "valueTransformer", this);
	}

	@Override
	public void process(Object key, Object value) {
		thing.getAction().run(actionContext, "key", key, "value", value, "context", context);
		
	}

	@Override
	public void init(ProcessorContext context) {
		this.context = context;
		thing.doAction("init", actionContext, "context", context);
		
	}

}
