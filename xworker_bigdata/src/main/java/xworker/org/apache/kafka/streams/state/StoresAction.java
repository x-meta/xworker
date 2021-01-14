package xworker.org.apache.kafka.streams.state;

import java.time.Duration;
import java.util.Map;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.SessionBytesStoreSupplier;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.WindowBytesStoreSupplier;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import xworker.org.apache.kafka.SerdesActions;

public class StoresAction {
	public static Object createInMemoryKeyValueStore(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String storeName = self.doAction("getStoreName", actionContext);
		return Stores.inMemoryKeyValueStore(storeName);
	}
	
	public static Object createLruMapKeyValueStore(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String storeName = self.doAction("getStoreName", actionContext);
		int maxCacheSize = self.doAction("getMaxCacheSize", actionContext);
		return Stores.lruMap(storeName, maxCacheSize);
	}
	
	public static Object createPersistentKeyValueStore(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String storeName = self.doAction("getStoreName", actionContext);
		return Stores.persistentKeyValueStore(storeName);
	}
	
	public static Object createPersistentTimestampedKeyValueStore(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String storeName = self.doAction("getStoreName", actionContext);
		return Stores.persistentTimestampedKeyValueStore(storeName);
	}
	
	public static Serde<?> getKeySerde(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		return SerdesActions.getSerde(self.getString("keySerde"));
	}
	
	public static Serde<?> getValueSerde(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		return SerdesActions.getSerde(self.getString("valueSerde"));
	}
	
	public static Serde<?> getOtherValueSerde(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		return SerdesActions.getSerde(self.getString("otherValueSerde"));
	}
	
	public static Object createKeyValueStoreBuilder(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Serde<?> keySerde = getKeySerde(actionContext);
		Serde<?> valueSerde = getValueSerde(actionContext);
		
		KeyValueBytesStoreSupplier supplier = null;
		for(Thing child : self.getChilds()) {
			supplier = child.doAction("create", actionContext);
			if(supplier != null) {
				break;
			}
		}
		
		if(supplier == null) {
			throw new ActionException("Can not create KeyValueStoreBuilder, supplier is null, path=" + self.getMetadata().getPath());
		}
		return init(self, actionContext, Stores.keyValueStoreBuilder(supplier, keySerde, valueSerde));
	}
	
	public static Object createTimestampedKeyValueStoreBuilder(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Serde<?> keySerde = getKeySerde(actionContext);
		Serde<?> valueSerde = getValueSerde(actionContext);
		
		KeyValueBytesStoreSupplier supplier = null;
		for(Thing child : self.getChilds()) {
			supplier = child.doAction("create", actionContext);
			if(supplier != null) {
				break;
			}
		}
		
		if(supplier == null) {
			throw new ActionException("Can not create KeyValueStoreBuilder, supplier is null, path=" + self.getMetadata().getPath());
		}
		return init(self, actionContext, Stores.timestampedKeyValueStoreBuilder(supplier, keySerde, valueSerde));
	}
	
	public static Object createInMemorySessionStore(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String storeName = self.doAction("getStoreName", actionContext);
		Duration retentionPeriod = self.doAction("getRetentionPeriod", actionContext);
		return Stores.inMemorySessionStore(storeName, retentionPeriod);
	}
	
	public static Object createPersistentSessionStore(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String storeName = self.doAction("getStoreName", actionContext);
		Duration retentionPeriod = self.doAction("getRetentionPeriod", actionContext);
		return Stores.persistentSessionStore(storeName, retentionPeriod);
	}
	
	public static Object createSessionStoreBuilder(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Serde<?> keySerde = getKeySerde(actionContext);
		Serde<?> valueSerde = getValueSerde(actionContext);
		
		SessionBytesStoreSupplier supplier = null;
		for(Thing child : self.getChilds()) {
			supplier = child.doAction("create", actionContext);
			if(supplier != null) {
				break;
			}
		}
		
		
		
		if(supplier == null) {
			throw new ActionException("Can not create SessionStoreBuilder, supplier is null, path=" + self.getMetadata().getPath());
		}
		return init(self, actionContext, Stores.sessionStoreBuilder(supplier, keySerde, valueSerde));
	}
	
	public static Object createInMemoryWindowStore(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String storeName = self.doAction("getStoreName", actionContext);
		Duration retentionPeriod = self.doAction("getRetentionPeriod", actionContext);
		Duration windowSize = self.doAction("getWindowSize", actionContext);
		Boolean retainDuplicates = self.doAction("isRetainDuplicates", actionContext);
		
		return Stores.inMemoryWindowStore(storeName, retentionPeriod, windowSize, retainDuplicates);
	}
	
	public static Object createPersistentTimestampedWindowStore(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String storeName = self.doAction("getStoreName", actionContext);
		Duration retentionPeriod = self.doAction("getRetentionPeriod", actionContext);
		Duration windowSize = self.doAction("getWindowSize", actionContext);
		Boolean retainDuplicates = self.doAction("isRetainDuplicates", actionContext);
		
		return Stores.persistentTimestampedWindowStore(storeName, retentionPeriod, windowSize, retainDuplicates);
	}
	
	public static Object createPersistentWindowStore(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String storeName = self.doAction("getStoreName", actionContext);
		Duration retentionPeriod = self.doAction("getRetentionPeriod", actionContext);
		Duration windowSize = self.doAction("getWindowSize", actionContext);
		Boolean retainDuplicates = self.doAction("isRetainDuplicates", actionContext);
		
		return Stores.persistentWindowStore(storeName, retentionPeriod, windowSize, retainDuplicates);
	}
	
	public static Object createTimestampedWindowStoreBuilder(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Serde<?> keySerde = getKeySerde(actionContext);
		Serde<?> valueSerde = getValueSerde(actionContext);
		
		WindowBytesStoreSupplier supplier = null;
		for(Thing child : self.getChilds()) {
			supplier = child.doAction("create", actionContext);
			if(supplier != null) {
				break;
			}
		}
		
		
		
		if(supplier == null) {
			throw new ActionException("Can not create TimestampedWindowStoreBuilder, supplier is null, path=" + self.getMetadata().getPath());
		}
		return init(self, actionContext, Stores.timestampedWindowStoreBuilder(supplier, keySerde, valueSerde));
	}

	public static Object createWindowStoreBuilder(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Serde<?> keySerde = getKeySerde(actionContext);
		Serde<?> valueSerde = getValueSerde(actionContext);
		
		WindowBytesStoreSupplier supplier = null;
		for(Thing child : self.getChilds()) {
			supplier = child.doAction("create", actionContext);
			if(supplier != null) {
				break;
			}
		}
		
		
		
		if(supplier == null) {
			throw new ActionException("Can not create WindowStoreBuilder, supplier is null, path=" + self.getMetadata().getPath());
		}
		return init(self, actionContext, Stores.windowStoreBuilder(supplier, keySerde, valueSerde));
	}
	
	public static Object init(Thing self, ActionContext actionContext,  StoreBuilder<?> storeBuilder) {
		if(UtilData.isTrue(self.doAction("isWithCachingDisabled", actionContext))) {
			storeBuilder.withCachingDisabled();			
		}
		
		if(UtilData.isTrue(self.doAction("isWithCachingEnabled", actionContext))) {
			storeBuilder.withCachingEnabled();			
		}
		
		if(UtilData.isTrue(self.doAction("isWithLoggingDisabled", actionContext))) {
			storeBuilder.withLoggingDisabled();			
		}
		
		Map<String, String> config = self.doAction("getWithLoggingEnabled", actionContext);
		if(config != null) {
			storeBuilder.withLoggingEnabled(config);
		}
		
		return storeBuilder;
	}
}
