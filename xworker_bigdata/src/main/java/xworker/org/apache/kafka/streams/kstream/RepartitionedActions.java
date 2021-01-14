package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.kstream.Repartitioned;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class RepartitionedActions {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Repartitioned rep = null;
		
		String repartitionedName = self.doAction("getRepartitionedName", actionContext);
		if(repartitionedName != null) {
			rep = Repartitioned.as(repartitionedName);
		}
		
		
		Integer numberOfPartitions = self.doAction("getNumberOfPartitions", actionContext);
		if(numberOfPartitions != null) {
			if(rep == null) {
				rep = Repartitioned.numberOfPartitions(numberOfPartitions);
			}else {
				rep.withNumberOfPartitions(numberOfPartitions);
			}
		}
		
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof StreamPartitioner) {
				if(rep == null) {
					rep = Repartitioned.streamPartitioner((StreamPartitioner) obj);
				}else {
					rep.withStreamPartitioner((StreamPartitioner) obj);
				}
				break;
			}
		}
		Serde<?> keySerde = self.doAction("getKeySerde", actionContext);
		Serde<?> valueSerde = self.doAction("getValueSerde", actionContext);
		if(rep == null) {
			if(keySerde != null && valueSerde != null) {
				rep = Repartitioned.with(keySerde, valueSerde);
			}
		}else {
			if(keySerde != null) {
				rep.withKeySerde(keySerde);
			}
			if(valueSerde != null) {
				rep.withValueSerde(valueSerde);
			}
		}
		
		actionContext.l().put(self.getMetadata().getName(), rep);
		return rep;
	}
}
