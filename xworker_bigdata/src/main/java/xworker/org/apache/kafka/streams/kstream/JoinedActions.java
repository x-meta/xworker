package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.kstream.Joined;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class JoinedActions {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Joined joined = null;
		
		String joinName = self.doAction("getJoinName", actionContext);
		if(joinName != null) {
			joined = Joined.as(joinName);
		}
		
		Serde<?> keySerde = self.doAction("getKeySerde", actionContext);
		Serde<?> valueSerde = self.doAction("getValueSerde", actionContext);
		Serde<?> otherValueSerde = self.doAction("getOtherValueSerde", actionContext);
		if(keySerde != null) {
			if(joined == null) {
				joined = Joined.keySerde(keySerde);
			}else {
				joined.withKeySerde(keySerde);
			}
		}
		if(valueSerde != null) {
			if(joined == null) {
				joined = Joined.valueSerde(valueSerde);
			}else {
				joined.withValueSerde(valueSerde);
			}
		}
		if(otherValueSerde != null) {
			if(joined == null) {
				joined = Joined.otherValueSerde(otherValueSerde);
			}else {
				joined.withOtherValueSerde(otherValueSerde);
			}
		}
		
		actionContext.l().put(self.getMetadata().getName(), joined);
		return joined;
	}
}
