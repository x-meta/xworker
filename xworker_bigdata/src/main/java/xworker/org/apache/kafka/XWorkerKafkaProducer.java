package xworker.org.apache.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class XWorkerKafkaProducer {
	private static final String key = "PRODUCER";
	private Thing thing;
	private ActionContext actionContext;
	private long lastModified = 0;
	private KafkaProducer<?,?> producer;
	
	public XWorkerKafkaProducer(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
		
		init();
	}
	    
    public void init() {
        ClassLoader clsLoader = Thread.currentThread().getContextClassLoader();
        try {        	
        	Thread.currentThread().setContextClassLoader(XWorkerKafkaProducer.class.getClassLoader());
        	Thing thing = World.getInstance().getThing(this.thing.getMetadata().getPath());
        	if(producer == null || thing.getMetadata().getLastModified() != this.lastModified) {
        		//已经变更
        		if(producer != null) {
        			producer.close();
        		}
        		
        		Properties config = thing.doAction("getConfig", actionContext);
        		//System.out.println(config);
        		//System.out.println("serializer=" + thing.doAction("getKeySerializer", actionContext));
        		config.put("key.serializer", thing.doAction("getKeySerializer", actionContext));
        		config.put("value.serializer", thing.doAction("getValueSerializer", actionContext));    		
        		thing.doAction("beforeInit", actionContext, "config", config);    		
        		
        		producer = new KafkaProducer<>(config);
        		thing.doAction("afterInit", actionContext, "producer", producer);
        	}
        } finally {
            Thread.currentThread().setContextClassLoader(clsLoader);
        }
    }
    
    public void close() {
    	if(producer != null) {
    		producer.close();
    		producer = null;
		}
    }
    
    public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public long getLastModified() {
		return lastModified;
	}

	public KafkaProducer<?, ?> getProducer() {
		return producer;
	}

	public static XWorkerKafkaProducer getXWorkerKafkaProducer(ActionContext actionContext) {
        Thing self = actionContext.getObject("self");
        return getXWorkerKafkaProducer(self, actionContext);
    }
    
    public static XWorkerKafkaProducer getXWorkerKafkaProducer(Thing self, ActionContext actionContext) {
    	synchronized(self) {	    	
	    	XWorkerKafkaProducer consumer = (XWorkerKafkaProducer) self.getStaticData(key);
	    	if(consumer == null) {
	    		consumer = new XWorkerKafkaProducer(self, actionContext);
	    		self.setStaticData(key, consumer);
	    	}

	    	return consumer;
    	}
    }
    
    public static KafkaProducer<?,?> getKafkaProducer(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");    	
    	synchronized(self) {
    		XWorkerKafkaProducer producer = getXWorkerKafkaProducer(self, actionContext);	    	
    		return producer.getProducer();
    	}
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object send(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");    	
    	XWorkerKafkaProducer producer = getXWorkerKafkaProducer(self, actionContext);	  
    	Object key = actionContext.getObject("key");
    	Object value = actionContext.getObject("value");
    	String topic = actionContext.getObject("topic");
    	if(key == null) {
    		ProducerRecord record = new ProducerRecord<>(topic, value);
    		return producer.getProducer().send(record);
    	}else {
    		ProducerRecord record = new ProducerRecord<>(topic, key, value);
    		return producer.getProducer().send(record);
    	}
    }
    
    public static void close(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");    	
    	XWorkerKafkaProducer producer = getXWorkerKafkaProducer(self, actionContext);	   
    	producer.close();
    	self.setStaticData(key, null);
    }
}
