package xworker.org.apache.kafka.streams.kstream;

import org.xmeta.Thing;

import org.apache.kafka.streams.kstream.Named;

import org.xmeta.ActionContext;

public class NamedActions {
	public static Named create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return Named.as(self.getMetadata().getName());
	}
}
