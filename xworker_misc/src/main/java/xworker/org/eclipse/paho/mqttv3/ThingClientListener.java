package xworker.org.eclipse.paho.mqttv3;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingClientListener implements MQTTClientListener{
	Thing thing;
	ActionContext actionContext;
	
	public ThingClientListener(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public void connectionLost(MqttClient client, Throwable cause) {
		thing.doAction("connectionLost", actionContext, "client", client, "cause", cause);
	}

	@Override
	public void messageArrived(MqttClient client, String topic, MqttMessage message) throws Exception {
		thing.doAction("messageArrived", actionContext, "client", client,"topic", topic, "message", message);
	}

	@Override
	public void deliveryComplete(MqttClient client, IMqttDeliveryToken token) {
		thing.doAction("deliveryComplete", actionContext, "client", client,"token", token);
	}
}
