package xworker.org.apache.kafka;

import java.util.List;
import java.util.Properties;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.apache.kafka.streams.state.StoreBuilder;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;

public class XWorkerKafkaStream {
	private static final String key = "STREAM";
	
	private Thing thing;
	private ActionContext actionContext;
	private KafkaStreams streams;
	
	public XWorkerKafkaStream(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;		
	}
	
	public synchronized void start() {
		if(streams != null && streams.state().isRunningOrRebalancing()) {
			throw new ActionException("KafkaStream already running, path=" + thing.getMetadata().getPath());
		}
		
		if(streams != null) {
			streams.close();
		}
		
		Properties config = thing.doAction("getConfig", actionContext);
		//System.out.println(config);
		//System.out.println("serializer=" + thing.doAction("getKeySerializer", actionContext));
		config.put("application.id", thing.doAction("getApplicationId", actionContext));	
		config.put("default key.serde", thing.doAction("getKeySerdeClass", actionContext));
		config.put("default value.serde", thing.doAction("getValueSerdeClass", actionContext));    		
		    		
		
		Bindings bindings = actionContext.push();
		try {
			bindings.setVarScopeFlag();
			thing.doAction("beforeInit", actionContext, "config", config, "xstream", this);
			
			StreamsBuilder builder = new StreamsBuilder();
			for(Thing child : thing.getChilds()) {
				child.doAction("build", actionContext, "builder", builder, "xstream", this);
			}
			
			streams = new KafkaStreams(builder.build(), config);
			thing.doAction("afterInit", actionContext, "streams", streams, "xstream", this);
		}finally {
			actionContext.pop();
		}
		
		streams.start();		
	}

	public void close() {
		if(streams != null) {
			streams.close();
		}
	}
	
	public KafkaStreams getKafkaStreams() {
		return streams;
	}
	
	public Thing getThing() {
		return thing;
	}
	
	public ActionContext getActionContext() {
		return actionContext;
	}
	
	public synchronized static XWorkerKafkaStream getKafkaStreams(Thing self, ActionContext actionContext) {
		XWorkerKafkaStream streams = self.getStaticData(key);
		if(streams == null) {
			streams = new XWorkerKafkaStream(self, actionContext);
			self.setStaticData(key, streams);
		}
		
		return streams;
	}
	
	public static Class<?> getKeySerdeClass(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		return SerdesActions.getSerdeClass(self.getString("keySerdeClass"));
	}
	
	public static Class<?> getValueSerdeClass(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		return SerdesActions.getSerdeClass(self.getString("valueSerdeClass"));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildAddGlobalStore(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String topic = self.doAction("getTopic", actionContext);
		StoreBuilder<?> storeBuilder = null;
		Consumed consumed = null;
		ProcessorSupplier stateUpdateSupplier = null;
		StreamsBuilder builder = actionContext.getObject("builder");
		
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof StoreBuilder<?> && storeBuilder != null) {
				storeBuilder = (StoreBuilder<?>) obj;
			}
			
			if(obj instanceof Consumed<?,?> && consumed != null) {
				consumed = (Consumed<?,?>) obj;
			}
			
			if(obj instanceof ProcessorSupplier<?,?,?,?> && stateUpdateSupplier != null) {
				stateUpdateSupplier = (ProcessorSupplier<?,?,Void, Void>) obj;
			}
		}
		
		StreamsBuilder sb = builder.addGlobalStore(storeBuilder, topic,
				(Consumed<?,?>) consumed, stateUpdateSupplier);
		for(Thing child : self.getChilds()) {
			child.doAction("build", actionContext, "builder", sb);
		}
	}
	
	public static void buildAddStateStore(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		StoreBuilder<?> storeBuilder = null;
		StreamsBuilder builder = actionContext.getObject("builder");
		
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof StoreBuilder<?> && storeBuilder != null) {
				storeBuilder = (StoreBuilder<?>) obj;
				break;
			}
		}
		if(storeBuilder == null) {
			return;
		}
		
		StreamsBuilder sb = builder.addStateStore(storeBuilder);
		for(Thing child : self.getChilds()) {
			child.doAction("build", actionContext, "builder", sb);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void buildGlobalTable(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String topic = self.doAction("getTopic", actionContext);
		if(topic == null) {
			throw new ActionException("Can not create GlobalTable, topic is null, path=" + self.getMetadata().getPath());
		}
		StreamsBuilder builder = actionContext.getObject("builder");
		Consumed c = null;
		Materialized m = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Consumed && c != null) {
				c = (Consumed) obj;
			}
			
			if(obj instanceof Materialized) {
				m = (Materialized) obj;
			}
			
			if(c != null && m != null) {
				break;
			}
		}
		
		GlobalKTable table = null;
		if(c != null && m != null) {
			table = builder.globalTable(topic, c, m);
		}else if(c != null) {
			table = builder.globalTable(topic, c);
		}else if(m != null) {
			table = builder.globalTable(topic, m);
		}else {
			table = builder.globalTable(topic);
		}
		
		actionContext.l().put(self.getMetadata().getName(), table);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void buildTable(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String topic = self.doAction("getTopic", actionContext);
		if(topic == null) {
			throw new ActionException("Can not create GlobalTable, topic is null, path=" + self.getMetadata().getPath());
		}
		StreamsBuilder builder = actionContext.getObject("builder");
		Consumed c = null;
		Materialized m = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Consumed && c != null) {
				c = (Consumed) obj;
			}
			
			if(obj instanceof Materialized) {
				m = (Materialized) obj;
			}
			
			if(c != null && m != null) {
				break;
			}
		}
		
		KTable table = null;
		if(c != null && m != null) {
			table = builder.table(topic, c, m);
		}else if(c != null) {
			table = builder.table(topic, c);
		}else if(m != null) {
			table = builder.table(topic, m);
		}else {
			table = builder.table(topic);
		}
		
		actionContext.l().put(self.getMetadata().getName(), table);
		
		for(Thing child : self.getChilds()) {
			child.doAction("build", actionContext, "ktable", table);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildStream(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		List<String> topics = self.doAction("getTopics", actionContext);
		
		StreamsBuilder builder = actionContext.getObject("builder");
		Consumed consumed = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Consumed && consumed != null) {
				consumed = (Consumed) obj;
				break;
			}			
		}
		
		KStream kstream = null;
		if(consumed != null) {
			kstream = builder.stream(topics, consumed);
		}else {
			kstream = builder.stream(topics);
		}
		
		actionContext.l().put(self.getMetadata().getName(), kstream);
		for(Thing child : self.getChilds()) {
			child.doAction("build", actionContext, "kstream", kstream);
		}
	}
}