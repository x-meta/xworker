package xworker.ai.neuroph;

import java.io.File;
import java.util.Date;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.events.NeuralNetworkEvent;
import org.neuroph.core.events.NeuralNetworkEventListener;
import org.neuroph.core.learning.IterativeLearning;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.nnet.learning.BinaryDeltaRule;
import org.neuroph.nnet.learning.DynamicBackPropagation;
import org.neuroph.nnet.learning.ResilientPropagation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.ai.neural.Learning;
import xworker.ai.neural.LearningManager;
import xworker.dataObject.DataObject;
import xworker.task.TaskManager;

public class Network  implements LearningEventListener, NeuralNetworkEventListener, Learning{
	private static Logger logger = LoggerFactory.getLogger(Network.class);
	
	/** 没有准备好，即没有被训练过 */
	public static final int UNREADY = 0;
	/** 已经准备好，可以计算了*/
	public static final int READY = 1;
	/** 正在计算 */
	public static final int CALCULATING = 0;
	/** 没有训练 */
	public static final int LEARNING_STOPED = 3;
	public static final int LEARNING_FINISHED = 0;
	/** 训练中 */
	public static final int LEARNING = 1;
	/** 训练暂停 */
	public static final int LEARNING_PAUSE = 2;
	
	/** 状态 */
	private int status;
	
	/** 训练状态 */
	private int learningStatus;
	
	private Date learningStartDate;
	
	private long lastModified;
	
	private long fileLastModified;
	
	/** 学习迭代次数 */
	private int learningIteration;
	
	/** 神经网络 */
	@SuppressWarnings("rawtypes")
	private NeuralNetwork neuralNetwork;
	
	/** 专门用于学习的神经网络 */
	private NeuralNetwork<?> learnNeuralNetwork;
	
	/** 构造神经网络的事物 */
	private Thing thing;
		
	
	@SuppressWarnings("rawtypes")
	public Network(Thing thing, NeuralNetwork neuralNetwork, int status, long fileLastModified){
		this.thing = thing;
		this.lastModified = thing.getMetadata().getLastModified();
		this.neuralNetwork = neuralNetwork;
		this.status = status;		
		this.fileLastModified = fileLastModified;
	}
	
	public boolean isChanged(Thing thing){
		return this.lastModified != thing.getMetadata().getLastModified();
	}
	
	public boolean isFileChanged(File file){
		return file.lastModified() != fileLastModified;
	}
	
	/**
	 * 执行计算，如果动作action是同步的，那么可能会等待，否则通过调用action的onCaucaluted方法回调。
	 * 
	 * @param action
	 * @param input
	 * @param actionContext
	 * @return
	 */
	public double[] calculate(Thing action, double[] input, ActionContext actionContext){
		NetworkCalculateTask task = new NetworkCalculateTask(this, action, input, actionContext);
		
		if(task.action.getBoolean("sync")){
			synchronized(task){
				TaskManager.getExecutorService().execute(task);
				try {
					//这里存在线程的隐患吗？也许是没有的，因为task执行时也有synchronized
					task.wait();
				} catch (InterruptedException e) {
				}
				
				return task.getOutput();
			}
		}else{
			//启动线程执行
			TaskManager.getExecutorService().execute(task);
			return null;
		}
	}
	
	protected synchronized void calculating(NetworkCalculateTask task, NeuralNetwork nuNetwork){
		try{
			nuNetwork.setInput(task.input);
			nuNetwork.calculate();
			task.output = nuNetwork.getOutput();			
		}catch(Exception e){
			logger.error("neuroph network calculate error, actionPath=" + task.action.getMetadata().getPath() + 
					",networkPath=" + thing.getMetadata().getPath(), e);
		}
	}
	
	public void learn(Thing action, ActionContext actionContext){		
		NetworkLearningTask task = new NetworkLearningTask(this, action,  actionContext);
		
		if(task.action.getBoolean("sync")){
			synchronized(task){
				TaskManager.getExecutorService().execute(task);
				try {
					//这里存在线程的隐患吗？也许是没有的，因为task执行时也有synchronized
					task.wait();
				} catch (InterruptedException e) {
				}
			}
		}else{
			//启动线程执行
			TaskManager.getExecutorService().execute(task);
		}
	}
	
	protected DataSet doLearn(NetworkLearningTask task){
		this.learningIteration = 0;
		synchronized(this){
			if(learningStatus == Network.LEARNING){
				 throw new ActionException("Network is trainning, actionPath=" + task.action.getMetadata().getPath() + 
							",networkPath=" + thing.getMetadata().getPath());
			}else{
				learningStatus = Network.LEARNING;
			}
		}
		
		try{
			this.learningStartDate = new Date();
			LearningManager.addTrainingNetwork(this);
			return (DataSet) task.network.thing.doAction("learn", task.actionContext);
		}finally{
			learningStatus = Network.LEARNING_STOPED;
		}
	}

	@Override
	public void handleLearningEvent(LearningEvent event) {
		learningIteration++;
	}

	@Override
	public void handleNeuralNetworkEvent(NeuralNetworkEvent event) {
		
	}

	public Thing getThing() {
		return thing;
	}

	@SuppressWarnings("rawtypes")
	public NeuralNetwork getNeuralNetwork() {
		return neuralNetwork;
	}

	@Override
	public Date getLearningStartTime() {
		return learningStartDate;
	}

	@Override
	public boolean isLarning() {
		return this.learningStatus == Network.LEARNING;
	}

	@Override
	public void stopLearning() {
		this.learnNeuralNetwork.getLearningRule().stopLearning();	
		//LearningManager.removeTrainningNetwork(this);
		
		this.learningStatus = Network.LEARNING_STOPED;
	}
	
	@Override
	public Thing getNeuralThing() {
		return thing;
	}

	public int getLearningIteration() {
		return learningIteration;
	}

	public NeuralNetwork<?> getLearnNeuralNetwork() {
		return learnNeuralNetwork;
	}

	public void setLearnNeuralNetwork(NeuralNetwork<?> learnNeuralNetwork) {
		this.learnNeuralNetwork = learnNeuralNetwork;
		
		this.learnNeuralNetwork.getLearningRule().addListener(this);
	}

	public void setNeuralNetwork(NeuralNetwork<?> neuralNetwork, long lastModified, long fileLastModified) {
		this.neuralNetwork = neuralNetwork;
		this.lastModified = lastModified;
		this.fileLastModified = fileLastModified;
	}
	
	public void setLearningStatus(int status){
		this.learningStatus = status;
	}
	
	public String getLearningStatusLabel(){
		switch(this.learningStatus){
		case Network.LEARNING:
			return "正在训练";
		case Network.LEARNING_STOPED:
			return "中断中";
		case Network.LEARNING_PAUSE:
			return "暂停中";
			default: return "未知状态：" + learningStatus;				
		}
	}
	
	/**
	 * 插入学习日志到数据库中。
	 */
	public void insertLearningLog(ActionContext actionContext){
		DataObject obj = new DataObject("xworker.ai.learning.dataobjects.AILearnLog");
		
		obj.put("name", thing.getMetadata().getName());
		obj.put("label", thing.getMetadata().getLabel());
		obj.put("thingPath", thing.getMetadata().getPath());
		obj.put("networkType", neuralNetwork.getClass().getSimpleName());
		LearningRule learningRule = neuralNetwork.getLearningRule();
		obj.put("learningRule", learningRule.getClass().getSimpleName());
		obj.put("inputsCount", neuralNetwork.getInputsCount());
		obj.put("outputsCount", neuralNetwork.getOutputsCount());
		obj.put("learnTime", System.currentTimeMillis() - learningStartDate.getTime());
		obj.put("status", learningStatus);
		obj.put("createDate", new Date());
		
		//obj.put("hiddensCount", neuralNetwork.geth)
		if(learningRule instanceof IterativeLearning){
			IterativeLearning l = (IterativeLearning) learningRule;
			obj.put("learningRate", l.getLearningRate());			
			obj.put("maxIterations", l.getMaxIterations());			
		}
		
		if(learningRule instanceof SupervisedLearning){
			SupervisedLearning l = (SupervisedLearning) learningRule;
			obj.put("batchMode", l.isInBatchMode() ? 1 : 0);
			obj.put("maxError", l.getMaxError());
			double minErrorChange = l.getMinErrorChange();
			obj.put("minErrorChange", Double.isInfinite(minErrorChange) ? 0 : minErrorChange);
			obj.put("minErrorChangeIterationsLimit", l.getMinErrorChangeIterationsLimit());
			obj.put("totalNetworkError", l.getTotalNetworkError());
		}
		
		if(learningRule instanceof BinaryDeltaRule){
			BinaryDeltaRule l = (BinaryDeltaRule) learningRule;
			obj.put("errorCorrection", l.getErrorCorrection());
		}
		
		if(learningRule instanceof DynamicBackPropagation){
			DynamicBackPropagation l = (DynamicBackPropagation) learningRule;
			obj.put("learningRateChange", l.getLearningRateChange());
			obj.put("maxLearningRate", l.getMaxLearningRate());
			obj.put("maxMomentum", l.getMaxMomentum());
			obj.put("minLearningRate", l.getMaxLearningRate());
			obj.put("minMomentum", l.getMinMomentum());
			obj.put("momentumChange", l.getMomentumChange());
			obj.put("useDynamicLearningRate", l.getUseDynamicLearningRate() ? 1 : 0);
			obj.put("useDynamicMomentum", l.getUseDynamicMomentum() ? 1 : 0);
		}
		
		if(learningRule instanceof ResilientPropagation){
			ResilientPropagation l = (ResilientPropagation) learningRule;
			obj.put("decreaseFactor", l.getDecreaseFactor());
			obj.put("increaseFactor", l.getIncreaseFactor());
			obj.put("initialDelta", l.getInitialDelta());
			obj.put("maxDelta", l.getMaxDelta());
			obj.put("minDelta", l.getMinDelta());
		}
		
		obj.create(actionContext);
	}
}
