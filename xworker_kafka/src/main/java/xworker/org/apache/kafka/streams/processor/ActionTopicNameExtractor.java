package xworker.org.apache.kafka.streams.processor;

import org.apache.kafka.streams.processor.RecordContext;
import org.apache.kafka.streams.processor.TopicNameExtractor;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionTopicNameExtractor implements TopicNameExtractor<Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ActionTopicNameExtractor(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ActionTopicNameExtractor create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ActionTopicNameExtractor(self, actionContext);
	}


	@Override
	public String extract(Object key, Object value, RecordContext recordContext) {
		return thing.getAction().run(actionContext, "key", key, "value", value, "recordContext", recordContext);
	}

}
