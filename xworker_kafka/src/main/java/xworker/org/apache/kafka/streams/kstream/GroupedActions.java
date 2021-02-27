package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.kstream.Grouped;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class GroupedActions {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object createGrouped(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Grouped grouped = null;
		
		String groupName = self.doAction("getGroupName", actionContext);
		if(groupName != null) {
			grouped = Grouped.as(groupName);
		}
		
		Serde<?> keySerde = self.doAction("getKeySerde", actionContext);
		Serde<?> valueSerde = self.doAction("getValueSerde", actionContext);
		
		if(keySerde != null) {
			if(grouped == null) {
				grouped = Grouped.keySerde(keySerde);
			}else {
				grouped.withKeySerde(keySerde);
			}
		}
		
		if(valueSerde != null) {
			if(grouped == null) {
				grouped = Grouped.valueSerde(valueSerde);
			}else {
				grouped.withValueSerde(valueSerde);
			}
		}
		
		actionContext.l().put(self.getMetadata().getName(), grouped);
		
		return grouped;
	}
}
