package xworker.org.apache.kafka.streams.kstream;

import java.time.Duration;
import java.util.Map;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.SessionBytesStoreSupplier;
import org.apache.kafka.streams.state.WindowBytesStoreSupplier;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

public class MaterializedActions {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Materialized<?,?,?> m = null;
		
		String storeName = self.doAction("getStoreName", actionContext);
		if(storeName != null) {
			m = Materialized.as(storeName);
		}
		
		if(m == null) {
			for(Thing child : self.getChilds()) {
				Object obj = child.doAction("create", actionContext);
				if(obj instanceof KeyValueBytesStoreSupplier) {
					m = Materialized.as((KeyValueBytesStoreSupplier) obj);
					break;
				}else if(obj instanceof SessionBytesStoreSupplier) {
					m = Materialized.as((SessionBytesStoreSupplier) obj);
					break;
				}else if(obj instanceof WindowBytesStoreSupplier) {
					m = Materialized.as((WindowBytesStoreSupplier) obj);
					break;
				}
			}
		}
		
		Serde keySerde = self.doAction("getKeySerde", actionContext);
		Serde valueSerde = self.doAction("getValueSerde", actionContext);
		if(m == null && keySerde != null && valueSerde != null) {
			m = Materialized.with(keySerde, valueSerde);
		}else {
			if(m == null) {
				throw new ActionException("Cat not create MaterializedActions, path=" + self.getMetadata().getPath());
			}
			
			if(keySerde != null) {
				m.withKeySerde(keySerde);
			}
			
			if(valueSerde != null) {
				m.withValueSerde(valueSerde);
			}
			
		}
		
		if(UtilData.isTrue(self.doAction("isWithCachingDisabled", actionContext))) {
			m.withCachingDisabled();
		}
		if(UtilData.isTrue(self.doAction("isWithCachingEnabled", actionContext))) {
			m.withCachingEnabled();
		}
		if(UtilData.isTrue(self.doAction("isWithLoggingDisabled", actionContext))) {
			m.withLoggingDisabled();
		}
			
		Duration withRetention = self.doAction("getWithRetention", actionContext);
		if(withRetention != null) {
			m.withRetention(withRetention);
		}
		Map<String, String> withLoggingEnabled = self.doAction("getWithLoggingEnabled", actionContext);
		if(withLoggingEnabled != null) {
			m.withLoggingEnabled(withLoggingEnabled);
		}
		
		return m;
	}
}
