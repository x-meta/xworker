package xworker.org.eclipse.paho.mqttv3;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface MQTTClientListener {
	public void connectionLost(MqttClient client, Throwable cause) ;
	
	public void messageArrived(MqttClient client, String topic, MqttMessage message) throws Exception;
	
	public void deliveryComplete(MqttClient client, IMqttDeliveryToken token);
}
