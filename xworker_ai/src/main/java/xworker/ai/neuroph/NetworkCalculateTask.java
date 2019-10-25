package xworker.ai.neuroph;

import org.neuroph.core.NeuralNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

public class NetworkCalculateTask implements Runnable{
	private static Logger logger = LoggerFactory.getLogger(NetworkCalculateTask.class);
	
	Network network;
	Thing action;
	ActionContext actionContext;
	double[] input;
	double[] output = null;
	@SuppressWarnings("rawtypes")
	private NeuralNetwork neuralNetwork;
	
	public NetworkCalculateTask(Network network, Thing action, double[] input, ActionContext actionContext){
		this.network = network;
		this.action = action;
		this.input = input;
		this.actionContext = actionContext;
		this.neuralNetwork = network.getNeuralNetwork();
	}
	
	public double[] getOutput(){
		return output;
	}
	
	@Override
	public void run() {
		try{
			network.calculating(this, neuralNetwork);
			
			if(action.getBoolean("sync")){
				synchronized(this){
					this.notify();
				}
			}else{
				action.doAction("onCalculated", actionContext, UtilMap.toMap("input", input, 
						"output", output, "action", action, "network", network));
			}
		}catch(Exception e){
			logger.error("neuroph network calculate error, actionPath=" + action.getMetadata().getPath() + 
					",networkPath=" + network.getThing().getMetadata().getPath(), e);
		}
	}

}
