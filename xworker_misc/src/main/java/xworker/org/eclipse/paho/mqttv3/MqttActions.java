package xworker.org.eclipse.paho.mqttv3;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

import xworker.lang.executor.Executor;

public class MqttActions {
	public static Object run(ActionContext actionContext) throws MqttException {
		Thing self = actionContext.getObject("self");
		Mqtt mqtt = getMqtt(self, actionContext);		
		MqttClient client =  mqtt != null ? mqtt.client : null;
		if(client != null && client.isConnected()) {
			return client;
		}else if(client != null) {
			try {
				client.disconnect();
				client.close();
			}catch(Exception e) {				
			}
		}
		
		MqttConnectOptions options = new MqttConnectOptions();  
		options.setAutomaticReconnect(self.getBoolean("automaticReconnect"));
		options.setCleanSession(self.getBoolean("cleanSession"));
		options.setConnectionTimeout(self.getInt("connectionTimeout"));
		if(self.getStringBlankAsNull("executorServiceTimeout") != null){
				options.setExecutorServiceTimeout(self.getInt("executorServiceTimeout"));		
		}
		if(self.getStringBlankAsNull("keepAliveInterval") != null) {
			options.setKeepAliveInterval(self.getInt("keepAliveInterval"));
		}
		
		String userName = self.doAction("getUserName", actionContext);
		String password = self.doAction("getPassword", actionContext);
		if(password == null) {
			password = "";
		}
		
		options.setUserName(userName);
		options.setPassword(password.toCharArray());
		
		String host = self.doAction("getHost", actionContext);
		String clientId = self.doAction("getClientId", actionContext);		
		
		client = new MqttClient(host, clientId, new MemoryPersistence());
		long timeToWait = self.getLong("timeToWait");
		if(timeToWait > 0) {
			client.setTimeToWait(timeToWait);
		}
		
		client.connect(options);
		mqtt.client = client;
		mqtt.callback.setMqttClient(client);
		client.setCallback(mqtt.callback);
		
		List<String> topics = self.doAction("getTopicList", actionContext);
		if(topics != null) {
			for(String topic : topics) {
				topic = topic.trim();
				if(!"".equals(topic)) {
					//client.subscribe(topic);
				}
			}
		}
		
		return client;
	}
	
	@SuppressWarnings("resource")
	public static void disconnect(ActionContext actionContext) throws MqttException {
		Thing self = actionContext.getObject("self");
		Mqtt mqtt = getMqtt(self, actionContext);
		MqttClient client =  mqtt != null ? mqtt.client : null;
		if(client != null && client.isConnected()) {
			client.disconnect();
		}
	}
	
	public static void close(ActionContext actionContext) throws MqttException {
		Thing self = actionContext.getObject("self");
		Mqtt mqtt = getMqtt(self, actionContext);
		MqttClient client =  mqtt != null ? mqtt.client : null;
		if(client != null) {
			if(client.isConnected()) {
				client.disconnect();
			}
			
			client.close();
		}
	}
	
	public static MqttClient getClient(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Mqtt mqtt = getMqtt(self, actionContext);
		MqttClient client =  mqtt != null ? mqtt.client : null;
	
		if(client == null || client.isConnected() == false) {
			return self.doAction("run", actionContext);
		}else {
			return client;
		}
	}
	
	public static Mqtt getMqtt(Thing self, ActionContext actionContext) {
		synchronized(self) {
			Mqtt mqtt = null;
			String scope = self.getString("scope");
			if("global".equals(scope)) {
				mqtt = actionContext.getObject(self.getMetadata().getName());
			}else {
				mqtt = (Mqtt) World.getInstance().getData(self.getMetadata().getPath());
			}
			
			if(mqtt == null) {
				mqtt = new Mqtt();
				mqtt.callback = new ThingMqttCallback(self, actionContext);;
				mqtt.actionContext = actionContext;
				mqtt.listeners = mqtt.callback.listeners;
				if("global".equals(scope)) {
					actionContext.g().put(self.getMetadata().getName(), mqtt);
				}else {
					World.getInstance().setData(self.getMetadata().getPath(), mqtt);
				}
			}
			return mqtt;
		}	
	}
	
	public static boolean isConnected(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Mqtt mqtt = getMqtt(self, actionContext);
		if(mqtt == null) {
			return false;
		}else {
			return mqtt.client != null && mqtt.client.isConnected();
		}
	}
	
	public static MQTTClientListener addListener(ActionContext actionContext) {
		//避免监听器重复加入，先做移除操作
		removeListener(actionContext);
		
		Thing self = actionContext.getObject("self");
		Mqtt mqtt = getMqtt(self, actionContext);
		if(mqtt == null) {
			return null;
		}
		Object listener = actionContext.getObject("listener");
		//ThingMqttCallback callback = (ThingMqttCallback ) World.getInstance().getData(self.getMetadata().getPath() + "_callback");
		MQTTClientListener lis = null;
		if(listener instanceof Thing) {
			lis = new ThingClientListener((Thing) listener, actionContext);
		}else if(listener instanceof ActionContainer) {
			lis = new ActionContainerClientListener((ActionContainer) listener, actionContext);
		}
				
		List<MQTTClientListener> listeners = mqtt.listeners;
		if(listeners == null) {
			listeners = new ArrayList<MQTTClientListener>();
			self.setStaticData("listeners", listeners);
		}
		if(listeners.contains(lis) == false) {
			listeners.add(lis);
		}
	
		return lis;
	}
	
	public static void removeListener(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Mqtt mqtt = getMqtt(self, actionContext);
		if(mqtt == null) {
			return;
		}
		
		Object lis = actionContext.getObject("listener");
		List<MQTTClientListener> listeners = mqtt.listeners;
		if(listeners == null) {
			return;
		}
		
		MQTTClientListener listener =  null;
		if(lis instanceof MQTTClientListener) {
			listener = (MQTTClientListener) lis;
		}else if(lis instanceof ActionContainer) {
			for(MQTTClientListener clis : listeners) {
				if(clis instanceof ActionContainerClientListener) {
					ActionContainerClientListener alis = (ActionContainerClientListener) clis;
					if(alis.actions == lis) {
						listener = clis;
						break;
					}
				}
			}
		}else if(lis instanceof Thing) {
			for(MQTTClientListener clis : listeners) {
				if(clis instanceof ThingClientListener) {
					ThingClientListener alis = (ThingClientListener) clis;
					if(alis.thing == lis) {
						listener = clis;
						break;
					}
				}
			}
		}
				
		if(listeners != null && listener != null) {
			listeners.remove(listener);
		}		
	}
	
	static class ThingMqttCallback implements MqttCallback{
		Thing thing;
		ActionContext actionContext;
		MqttClient client;
		List<MQTTClientListener> listeners = new ArrayList<MQTTClientListener>();
		
		public ThingMqttCallback(Thing thing, ActionContext actionContext) {			
			this.thing = thing;
			this.actionContext = actionContext;
		}
		
		public void setMqttClient(MqttClient client) {
			this.client = client;
		}
		
		public void addListener(MQTTClientListener listener) {
			if(listeners.contains(listener) == false) {
				listeners.add(listener);
			}
		}
		
		public void removeListener(MQTTClientListener listener) {
			listeners.remove(listener);
		}
		
		@Override
		public void connectionLost(Throwable cause) {
			try {
				thing.doAction("connectionLost", actionContext, "client", client, "cause", cause);
				
				for(MQTTClientListener listener : listeners) {
					listener.connectionLost(client, cause);
				}
			}catch(Exception e) {
				Executor.error("MQTT", "Call connectionLost error", e);
			}
		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			try {
				thing.doAction("messageArrived", actionContext, "client", client,"topic", topic, "message", message);
				
				for(MQTTClientListener listener : listeners) {
					listener.messageArrived(client, topic, message);
				}
			}catch(Exception e) {
				Executor.error("MQTT", "Call messageArrived error", e);
			}
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {
			try {
				thing.doAction("deliveryComplete", actionContext, "client", client,"token", token);
				
				for(MQTTClientListener listener : listeners) {
					listener.deliveryComplete(client, token);
				}
			}catch(Exception e) {
				Executor.error("MQTT", "Call deliveryComplete error", e);
			}
		}
		
	}
}
