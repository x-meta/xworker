package xworker.org.eclipse.paho.mqttv3;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

public class MQTTClientActions {
	public static void publish(ActionContext actionContext) throws MqttPersistenceException, MqttException {
		Thing self = actionContext.getObject("self");
		
		MqttClient client = getClient(self, actionContext);
		
		byte[] payload = self.doAction("getPayload", actionContext);		
		MqttMessage message = new MqttMessage (payload);
		
		int qos = self.doAction("getQos", actionContext);
		boolean retained = self.doAction("isRetained", actionContext);
		message.setQos(qos);
		message.setRetained(retained);
		
		String topic = self.doAction("getTopic", actionContext);
		client.publish(topic, message);;
	}
	
	private static MqttClient getClient(Thing self, ActionContext actionContext) {
		MqttClient client = null;
		Object c = self.doAction("getClient", actionContext);
		if(c instanceof MqttClient) {
			client = (MqttClient) c;
		}else if(c instanceof Thing) {
			client = ((Thing) c).doAction("getClient", actionContext);
		}else if(c instanceof String) {
			Thing  clientThing = World.getInstance().getThing((String) c);
			if(clientThing != null) {
				client = clientThing.doAction("getClient", actionContext);
			}
		}
		if(client == null) {
			throw new ActionException("MQTTClient is null, action=" + self.getMetadata().getPath());
		}
		
		return client;
	}
	
	public static void subscribe(ActionContext actionContext) throws MqttException {
		Thing self = actionContext.getObject("self");
		
		MqttClient client = getClient(self, actionContext);
		String topics = self.doAction("getTopics", actionContext);
		if(topics != null) {
			client.subscribe(topics.split("[,]"));
		}
	}
	
	public static void unsubscribe(ActionContext actionContext) throws MqttException {
		Thing self = actionContext.getObject("self");
		
		MqttClient client = getClient(self, actionContext);
		String topics = self.doAction("getTopics", actionContext);
		if(topics != null) {
			client.unsubscribe(topics.split("[,]"));
		}
	}
}
