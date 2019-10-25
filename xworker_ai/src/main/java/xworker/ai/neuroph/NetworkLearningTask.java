package xworker.ai.neuroph;

import org.neuroph.core.data.DataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.ai.neural.LearningManager;

public class NetworkLearningTask implements Runnable{
	private static Logger logger = LoggerFactory.getLogger(NetworkCalculateTask.class);
	
	Network network;
	Thing action;
	ActionContext actionContext;
	
	public NetworkLearningTask(Network network,  Thing action, ActionContext actionContext){
		this.network = network;
		this.action = action;
		this.actionContext = actionContext;
	}
	
	@Override
	public void run() {
		try{
			//学习前是否重置
			if(network.getLearnNeuralNetwork() == null){
				network.getThing().doAction("loadLearnNetwork", actionContext);
			}
			if(action.getBoolean("resetBeforeLearning")){
				network.getLearnNeuralNetwork().reset();
			}
			
			DataSet dataSet = network.doLearn(this);
			
			//学习后是否保存
			if(action.getBoolean("saveAfterTrained")){
				network.getThing().doAction("save", actionContext);
			}
			
			if(action.getBoolean("sync")){
				synchronized(this){
					this.notify();
				}
			}else{
				action.doAction("onTrained", actionContext, UtilMap.toMap("action", action, "network", network));
			}
			
			actionContext.peek().put("dataSet", dataSet);
			network.insertLearningLog(actionContext);
		}catch(Exception e){
			logger.error("neuroph network training error, actionPath=" + action.getMetadata().getPath() + 
					",networkPath=" + network.getThing().getMetadata().getPath(), e);
		}finally{
			network.setLearningStatus(Network.LEARNING_FINISHED);
			LearningManager.removeTrainningNetwork(network);
		}
	}
}
