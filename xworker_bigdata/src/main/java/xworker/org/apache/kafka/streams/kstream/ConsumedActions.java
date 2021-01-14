package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.org.apache.kafka.streams.processor.ProcessorActions;

public class ConsumedActions {
	public static Object getTimestampExtractor(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return ProcessorActions.getTimestampExtractor(self.getString("timestampExtractor"));
	}
	
	public static Object getResetPolicy(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return ProcessorActions.getResetPolicy(self.getString("resetPolicy"));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object createConsumed(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Consumed consumed = null;
		
		String processorName = self.doAction("getProcessorName", actionContext);
		if(processorName != null) {
			consumed = Consumed.as(processorName);
		}
		
		Serde<?> keySerde = self.doAction("getKeySerde", actionContext);
		Serde<?> valueSerde = self.doAction("getValueSerde", actionContext);
		if(consumed == null) {
			if(keySerde != null && valueSerde != null) {
				consumed = Consumed.with(keySerde, valueSerde);
			}
		}
		TimestampExtractor timestampExtractor = self.doAction("getTimestampExtractor", actionContext);
		if(timestampExtractor != null) {
			if(consumed == null) {
				consumed = Consumed.with(timestampExtractor);
				if(keySerde != null) {
					consumed.withKeySerde(keySerde);
				}
				if(valueSerde != null) {
					consumed.withValueSerde(valueSerde);
				}
			}else {
				consumed.withTimestampExtractor(timestampExtractor);
			}
		}
		
		Topology.AutoOffsetReset resetPolicy = self.doAction("getResetPolicy", actionContext);
		if(resetPolicy != null) {
			if(consumed == null) {
				consumed = Consumed.with(resetPolicy);
				if(keySerde != null) {
					consumed.withKeySerde(keySerde);
				}
				if(valueSerde != null) {
					consumed.withValueSerde(valueSerde);
				}
			}else {
				consumed.withOffsetResetPolicy(resetPolicy);
			}
		}		
		
		actionContext.l().put(self.getMetadata().getName(), consumed);
		return consumed;
	}
}
