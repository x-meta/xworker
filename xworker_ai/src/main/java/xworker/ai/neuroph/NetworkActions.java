package xworker.ai.neuroph;

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
import org.xmeta.World;

import ognl.OgnlException;

public class NetworkActions {
	private static Logger logger = LoggerFactory.getLogger(NetworkActions.class);
	
	public static Network getNeurophNetwork(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String networkPath = self.getStringBlankAsNull("networkPath");
		Thing networkThing = null;
		if(networkPath != null){
			networkThing = World.getInstance().getThing(networkPath);			
		}
		
		if(networkThing == null){
			networkThing = self.getThing("NeurophNetwork@0");
		}
		
		if(networkThing != null){
			return (Network) networkThing.doAction("create", actionContext);
		}else{
			return null;
		}
	} 
	
	public static void onCalculated(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		logger.info("NeurophNetwork calculate output: " + actionContext.get("output") + ", path=" + self.getMetadata().getPath());
	}
	
	public static void onLearned(ActionContext actionContext){
		
	}
	
	public static Object getInput(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
				
		return self.getObject("input", actionContext);
	}
	
	public static Object calculate(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Network network = (Network) self.doAction("getNeurophNetwork", actionContext);
		if(network == null){
			throw new ActionException("NeurophNetwork is null, path=" + self.getMetadata().getPath());
		}else{
			return network.calculate(self, (double[]) actionContext.get("input"), actionContext);
		}
	}
	
	public static Object learn(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Network network = (Network) self.doAction("getNeurophNetwork", actionContext);
		if(network == null){
			throw new ActionException("NeurophNetwork is null, path=" + self.getMetadata().getPath());
		}else{
			//设置学习的参数
			LearningRule learningRule = network.getNeuralNetwork().getLearningRule();
			if(learningRule instanceof IterativeLearning){
				if(self.getStringBlankAsNull("learningRate") != null){
					((IterativeLearning) learningRule).setLearningRate(self.getDouble("learningRate", 0, actionContext));
				}
				
				if(self.getStringBlankAsNull("maxIterations") != null){
					((IterativeLearning) learningRule).setMaxIterations(self.getInt("maxIterations", 0, actionContext));					
				}
			}
			
			if(learningRule instanceof SupervisedLearning){
				if(self.getStringBlankAsNull("batchMode") != null){
					((SupervisedLearning) learningRule).setBatchMode(self.getBoolean("batchMode", false, actionContext));
				}
				
				if(self.getStringBlankAsNull("maxError") != null){
					((SupervisedLearning) learningRule).setMaxError(self.getDouble("maxError", 10, actionContext));
				}
				
				if(self.getStringBlankAsNull("minErrorChange") != null){
					((SupervisedLearning) learningRule).setMinErrorChange(self.getDouble("minErrorChange", 1, actionContext));
				}
				
				if(self.getStringBlankAsNull("minErrorChangeIterationsLimit") != null){
					((SupervisedLearning) learningRule).setMinErrorChangeIterationsLimit(self.getInt("minErrorChangeIterationsLimit", 1, actionContext));
				}							
			}
			
			if(learningRule instanceof BinaryDeltaRule){
				if(self.getStringBlankAsNull("errorCorrection") != null){
					((BinaryDeltaRule) learningRule).setErrorCorrection(self.getDouble("errorCorrection", 0, actionContext));
				}
			}
			
			if(learningRule instanceof DynamicBackPropagation){
				if(self.getStringBlankAsNull("learningRateChange") != null){
					((DynamicBackPropagation) learningRule).setLearningRateChange(self.getDouble("learningRateChange", 0,  actionContext));
				}
				
				if(self.getStringBlankAsNull("maxLearningRate") != null){
					((DynamicBackPropagation) learningRule).setMaxLearningRate(self.getDouble("maxLearningRate", 0, actionContext));
				}
				
				if(self.getStringBlankAsNull("maxMomentum") != null){
					((DynamicBackPropagation) learningRule).setMaxMomentum(self.getDouble("maxMomentum", 0, actionContext));
				}
				
				if(self.getStringBlankAsNull("minLearningRate") != null){
					((DynamicBackPropagation) learningRule).setMinLearningRate(self.getDouble("minLearningRate", 0, actionContext));
				}
				
				if(self.getStringBlankAsNull("minMomentum") != null){
					((DynamicBackPropagation) learningRule).setMinMomentum(self.getDouble("minMomentum", 0, actionContext));
				}
				
				if(self.getStringBlankAsNull("momentumChange") != null){
					((DynamicBackPropagation) learningRule).setMomentumChange(self.getDouble("momentumChange", 0, actionContext));
				}
				
				if(self.getStringBlankAsNull("useDynamicLearningRate") != null){
					((DynamicBackPropagation) learningRule).setUseDynamicLearningRate(self.getBoolean("useDynamicLearningRate", false, actionContext));
				}
				
				if(self.getStringBlankAsNull("useDynamicMomentum") != null){
					((DynamicBackPropagation) learningRule).setUseDynamicMomentum(self.getBoolean("useDynamicMomentum", false, actionContext));
				}
			}
			
			if(learningRule instanceof ResilientPropagation){
				if(self.getStringBlankAsNull("decreaseFactor") != null){
					((ResilientPropagation) learningRule).setDecreaseFactor(self.getDouble("decreaseFactor", 0, actionContext));
				}
				
				if(self.getStringBlankAsNull("increaseFactor") != null){
					((ResilientPropagation) learningRule).setIncreaseFactor(self.getDouble("increaseFactor", 0, actionContext));
				}
				
				if(self.getStringBlankAsNull("initialDelta") != null){
					((ResilientPropagation) learningRule).setInitialDelta(self.getDouble("initialDelta", 0, actionContext));
				}
				
				if(self.getStringBlankAsNull("maxDelta") != null){
					((ResilientPropagation) learningRule).setMaxDelta(self.getDouble("maxDelta", 0, actionContext));
				}
				
				if(self.getStringBlankAsNull("minDelta") != null){
					((ResilientPropagation) learningRule).setMinDelta(self.getDouble("minDelta", 0, actionContext));
				}
			}
			
			network.learn(self, actionContext);
			return null;
		}
	}
}
