package xworker.org.apache.kafka.streams.processor;

import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionStreamPartitioner implements StreamPartitioner<Object, Object>{
	Thing thing;
	ActionContext actionContext;
	ProcessorContext context;
	
	public ActionStreamPartitioner(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ActionStreamPartitioner create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ActionStreamPartitioner(self, actionContext);
	}

	@Override
	public Integer partition(String topic, Object key, Object value, int numPartitions) {
		return thing.getAction().run(actionContext, "topic", topic, "key", key, "value", value, "numPartitions", numPartitions);
	}


}
