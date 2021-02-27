package xworker.org.apache.kafka.streams.processor;

import org.apache.kafka.streams.processor.RecordContext;
import org.apache.kafka.streams.processor.TopicNameExtractor;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingTopicNameExtractor implements TopicNameExtractor<Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ThingTopicNameExtractor(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ThingTopicNameExtractor create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ThingTopicNameExtractor(self, actionContext);
	}


	@Override
	public String extract(Object key, Object value, RecordContext recordContext) {
		return thing.doAction("extract", actionContext, "key", key, "value", value, "recordContext", recordContext);
	}

}
