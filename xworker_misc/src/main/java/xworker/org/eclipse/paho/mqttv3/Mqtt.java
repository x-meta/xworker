package xworker.org.eclipse.paho.mqttv3;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.xmeta.ActionContext;

import xworker.org.eclipse.paho.mqttv3.MqttActions.ThingMqttCallback;

/**
 * 用来保存
 * 
 * @author zyx
 *
 */
public class Mqtt {
	public MqttClient client;
	public List<MQTTClientListener> listeners = new ArrayList<MQTTClientListener>();
	public ThingMqttCallback callback;
	public ActionContext actionContext;
}
