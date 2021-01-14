package xworker.org.apache.kafka.streams.processor;

import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingStreamPartitioner implements StreamPartitioner<Object, Object>{
	Thing thing;
	ActionContext actionContext;
	ProcessorContext context;
	
	public ThingStreamPartitioner(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ThingStreamPartitioner create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ThingStreamPartitioner(self, actionContext);
	}

	@Override
	public Integer partition(String topic, Object key, Object value, int numPartitions) {
		return thing.doAction("partition", actionContext, "topic", topic, "key", key, "value", value, "numPartitions", numPartitions);
	}

}
