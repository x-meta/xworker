package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.state.WindowBytesStoreSupplier;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class StreamJoinedActions {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		StreamJoined joined = null;
		
		String storeName = self.doAction("getStoreName", actionContext);
		if(storeName != null) {
			joined = StreamJoined.as(storeName);
		}
		
		Serde<?> keySerde = self.doAction("getKeySerde", actionContext);
		Serde<?> valueSerde = self.doAction("getValueSerde", actionContext);
		Serde<?> otherValueSerde = self.doAction("getOtherValueSerde", actionContext);
		if(joined == null) {
			if(keySerde != null && valueSerde != null && otherValueSerde != null) {
				joined = StreamJoined.with(keySerde, valueSerde, otherValueSerde);
			}
		}else {
			if(keySerde != null) {
				joined.withKeySerde(keySerde);
			}
			if(valueSerde != null) {
				joined.withValueSerde(valueSerde);
			}
			if(otherValueSerde != null) {
				joined.withOtherValueSerde(otherValueSerde);
			}
		}
		
		WindowBytesStoreSupplier thisSupplier = null;
		Thing thisThing = self.getThing("ThisWindowBytesStoreSupplier@0/@0");
		if(thisThing != null) {
			thisSupplier = thisThing.doAction("create", actionContext);
		}
		
		WindowBytesStoreSupplier otherSupplier = null;
		Thing otherThing = self.getThing("OtherWindowBytesStoreSupplier@0/@0");
		if(otherThing != null) {
			otherSupplier = otherThing.doAction("create", actionContext);
		}
		
		if(joined == null) {
			joined = StreamJoined.with(thisSupplier, otherSupplier);
		}else {
			if(thisSupplier != null) {
				joined.withThisStoreSupplier(thisSupplier);
			}
			
			if(otherSupplier != null) {
				joined.withOtherStoreSupplier(otherSupplier);
			}
		}
		actionContext.l().put(self.getMetadata().getName(), joined);
		return joined;
	}
}
