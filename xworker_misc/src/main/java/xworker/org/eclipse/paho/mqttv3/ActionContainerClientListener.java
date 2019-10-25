package xworker.org.eclipse.paho.mqttv3;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.xmeta.ActionContext;
import org.xmeta.util.ActionContainer;

public class ActionContainerClientListener implements MQTTClientListener{
	ActionContainer actions;
	ActionContext actionContext;
	
	public ActionContainerClientListener(ActionContainer actions, ActionContext actionContext) {
		this.actions = actions;
		this.actionContext = actionContext;
	}
	
	@Override
	public void connectionLost(MqttClient client, Throwable cause) {
		actions.doAction("connectionLost", actionContext, "client", client, "cause", cause);
	}

	@Override
	public void messageArrived(MqttClient client, String topic, MqttMessage message) throws Exception {
		actions.doAction("messageArrived", actionContext, "client", client,"topic", topic, "message", message);
	}

	@Override
	public void deliveryComplete(MqttClient client, IMqttDeliveryToken token) {
		actions.doAction("deliveryComplete", actionContext, "client", client,"token", token);
	}

}
