package xworker.org.apache.kafka;

import java.lang.Thread;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class XWorkerKafkaConsumer {
	private Thing thing;
	private ActionContext actionContext;
	private long lastModified = 0;
	KafkaConsumerRunner runner = null;
	
	public XWorkerKafkaConsumer(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	    
    public void run() {
        ClassLoader clsLoader = Thread.currentThread().getContextClassLoader();
        try {        	
        	Thread.currentThread().setContextClassLoader(KafkaConsumerRunner.class.getClassLoader());
        	Thing thing = World.getInstance().getThing(this.thing.getMetadata().getPath());
        	if(runner == null ||runner.getClosed().get() || thing.getMetadata().getLastModified() != this.lastModified) {
        		//已经变更
        		if(runner != null && !runner.getClosed().get()) {
        			runner.shutdown();
        		}
        		
        		Properties config = thing.doAction("getConfig", actionContext);
        		config.put("key.deserializer", thing.doAction("getKeyDeserializer", actionContext));
        		config.put("value.deserializer", thing.doAction("getValueDeserializer", actionContext));    		
        		thing.doAction("beforeInit", actionContext, "config", config);    		
        		
        		KafkaConsumer<?, ?> consumer = new KafkaConsumer<>(config);
        		thing.doAction("afterInit", actionContext, "consumer", consumer);
        		
        		long pollTimeout = thing.doAction("getPollTimeout", actionContext);
                List<String> topics = thing.doAction("getTopics", actionContext);
                
                runner = new KafkaConsumerRunner(thing, actionContext, consumer, topics, pollTimeout);
                new Thread(runner, thing.getMetadata().getLabel()).start();
        	}
        } finally {
            Thread.currentThread().setContextClassLoader(clsLoader);
        }
    }
    
    public void close() {
    	if(runner != null && !runner.getClosed().get()) {
			runner.shutdown();
		}
    }
    
    public static XWorkerKafkaConsumer getXWorkerKafkaConsumer(ActionContext actionContext) {
        Thing self = actionContext.getObject("self");
        return getXWorkerKafkaConsumer(self, actionContext);
    }
    
    public static XWorkerKafkaConsumer getXWorkerKafkaConsumer(Thing self, ActionContext actionContext) {
    	synchronized(self) {
	    	String key = "CONSUMER";
	    	XWorkerKafkaConsumer consumer = (XWorkerKafkaConsumer) self.getStaticData(key);
	    	if(consumer == null) {
	    		consumer = new XWorkerKafkaConsumer(self, actionContext);
	    		self.setStaticData(key, consumer);
	    	}

	    	return consumer;
    	}
    }
    
    public static KafkaConsumerRunner run(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");    	
    	synchronized(self) {
	    	XWorkerKafkaConsumer consumer = getXWorkerKafkaConsumer(self, actionContext);	    	
	    	consumer.run();
	    	return consumer.runner;
    	}
    }
    
    public static void close(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");    	
    	XWorkerKafkaConsumer consumer = getXWorkerKafkaConsumer(self, actionContext);
    	consumer.close();
    }
    
    public static void onRecords(ActionContext actionContext) {
    	ConsumerRecords<?,?> records = actionContext.getObject("records");
    	for(ConsumerRecord<?,?> record : records) {
    		System.out.println(record.toString());
    	}
    }
    
    static public class KafkaConsumerRunner implements Runnable {
        private final AtomicBoolean closed = new AtomicBoolean(false);
        private final KafkaConsumer<?,?> consumer;
        private final List<String> topics;
        private final ActionContext actionContext;
        private final Thing thing;
        private final long pollTimeout;
        
    
        public KafkaConsumerRunner(Thing thing, ActionContext actionContext, KafkaConsumer<?,?> consumer, List<String> topics, long pollTimeout) {
            this.thing = thing;
            this.actionContext = actionContext;
            this.consumer = consumer;
            this.topics = topics;
            this.pollTimeout = pollTimeout;
        }
        
    
        @Override
        public void run() {
        	ClassLoader clsLoader = Thread.currentThread().getContextClassLoader();
            try {        	
            	Thread.currentThread().setContextClassLoader(KafkaConsumerRunner.class.getClassLoader());
                consumer.subscribe(topics);
                while (!closed.get()) {
                    ConsumerRecords<?,?> records = consumer.poll(Duration.ofMillis(pollTimeout));
                    thing.doAction("onRecords", actionContext, "records", records, "consumer", consumer, "runner", this);
                    // Handle new records
                }
            } catch (WakeupException e) {
                // Ignore exception if closing
                if (!closed.get()) throw e;
            } finally {
                consumer.close();
                Thread.currentThread().setContextClassLoader(clsLoader);
            }
        }
    
        // Shutdown hook which can be called from a separate thread
        public void shutdown() {
            closed.set(true);
            consumer.wakeup();
        }
    
    
    	public AtomicBoolean getClosed() {
    		return closed;
    	}
    
    
    	public KafkaConsumer<?, ?> getConsumer() {
    		return consumer;
    	}
    
    
    	public List<String> getTopics() {
    		return topics;
    	}
    
    
    	public ActionContext getActionContext() {
    		return actionContext;
    	}
    
    
    	public Thing getThing() {
    		return thing;
    	}
    
    
    	public long getPollTimeout() {
    		return pollTimeout;
    	}
    }
}

