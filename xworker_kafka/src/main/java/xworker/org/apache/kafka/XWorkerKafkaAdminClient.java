package xworker.org.apache.kafka;

import java.util.Properties;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class XWorkerKafkaAdminClient {
	private Thing thing;
	private ActionContext actionContext;
	private AdminClient client;
	private long lastModified = 0;
	
	public XWorkerKafkaAdminClient(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	private synchronized void check() {
		Thing thing = World.getInstance().getThing(this.thing.getMetadata().getPath());
    	if(client == null || thing.getMetadata().getLastModified() != this.lastModified) {
    		Properties config = thing.doAction("getConfig", actionContext);
    		client = KafkaAdminClient.create(config);
    	}
	}		
	
	public AdminClient getAdminClient() {
		check();
		
		return client;
	}
	
	public synchronized void close() {
		if(client != null) {
			client.close();
			client = null;
		}
	}
	
    public static XWorkerKafkaAdminClient getXWorkerKafkaAdminClient(Thing self, ActionContext actionContext) {
    	synchronized(self) {
	    	String key = "ADMINCLIENT";
	    	XWorkerKafkaAdminClient adminClient = (XWorkerKafkaAdminClient) self.getStaticData(key);
	    	if(adminClient == null) {
	    		adminClient = new XWorkerKafkaAdminClient(self, actionContext);
	    		self.setStaticData(key, adminClient);
	    	}

	    	return adminClient;
    	}
    }
	
	public static AdminClient getAdminClient(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		XWorkerKafkaAdminClient client = getXWorkerKafkaAdminClient(self, actionContext);
		return client.getAdminClient();
	}
	
	public static void close(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		XWorkerKafkaAdminClient client = getXWorkerKafkaAdminClient(self, actionContext);
		client.close();
	}
}
