package xworker.ai.neuroph;

import org.neuroph.nnet.Adaline;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.BinaryDeltaRule;
import org.neuroph.nnet.learning.DynamicBackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.nnet.learning.PerceptronLearning;
import org.neuroph.nnet.learning.ResilientPropagation;
import org.neuroph.util.NeuralNetworkFactory;
import org.neuroph.util.TransferFunctionType;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class NetworkFactorys {
	public static Class<?> createLearningRule(String name){
		if("PerceptronLearning".equals(name)){
			return PerceptronLearning.class;
		}else if("BinaryDeltaRule".equals(name)){
			return BinaryDeltaRule.class;			
		}else if("BackPropagation".equals(name)){
			return BackPropagation.class;
		}else if("MomentumBackpropagation".equals(name)){
			return MomentumBackpropagation.class;
		}else if("ResilientPropagation".equals(name)){
			return ResilientPropagation.class;
		}else if("DynamicBackPropagation".equals(name)){
			return DynamicBackPropagation.class;
		}
		return null;
	}
	
	public static TransferFunctionType createTransferFunctionType(String name){
		name = name.toUpperCase();
		if("GAUSSIAN".equals(name)){
			return TransferFunctionType.GAUSSIAN;
		}else if("LINEAR".equals(name)){
			return TransferFunctionType.LINEAR;
		}else if("LOG".equals(name)){
			return TransferFunctionType.LOG;
		} else if("RAMP".equals(name)){
			return TransferFunctionType.RAMP;
		} else if("SGN".equals(name)){
			return TransferFunctionType.SGN;
		} else if("SIGMOID".equals(name)){
			return TransferFunctionType.SIGMOID;
		} else if("SIN".equals(name)){
			return TransferFunctionType.SIN;
		} else if("STEP".equals(name)){
			return TransferFunctionType.STEP;
		} else if("TANH".equals(name)){
			return TransferFunctionType.TANH;
		} else if("TRAPEZOID".equals(name)){
			return TransferFunctionType.TRAPEZOID;
		} 
		return null;
	}
	
	public static Adaline createAdaline(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return NeuralNetworkFactory.createAdaline(self.getInt("inputsCount"));
	}

	public static Object createPerceptron(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		int inputsCount = self.getInt("inputsCount");
		int outputsCount = self.getInt("outputsCount");
		Class<?> learningRule = createLearningRule(self.getString("learningRule"));
		
		return NeuralNetworkFactory.createPerceptron(inputsCount, outputsCount, TransferFunctionType.TANH, learningRule);
	}
	
	public static Object createMultiLayerPerceptron(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		int inputsCount = self.getInt("inputsCount");
		int outputsCount = self.getInt("outputsCount");
		int hiddensCount = self.getInt("hiddensCount");
		boolean useBias= self.getBoolean("useBias");
		boolean connectInputToOutput = self.getBoolean("connectInputToOutput");
		TransferFunctionType transferFunction = createTransferFunctionType(self.getString("transferFunction"));
		Class<?> learningRule = createLearningRule(self.getString("learningRule"));
		
		return NeuralNetworkFactory.createMLPerceptron(inputsCount + " " + hiddensCount + " " + outputsCount ,
				transferFunction, learningRule, useBias, connectInputToOutput);
	}
	
	public static Object createHopfield(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		int inputsCount = self.getInt("inputsCount");
		
		return NeuralNetworkFactory.createHopfield(inputsCount);
	}
	
	public static Object createBAM(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		int inputsCount = self.getInt("inputsCount");
		int outputsCount = self.getInt("outputsCount");
		
		return NeuralNetworkFactory.createBam(inputsCount, outputsCount);
	}
	
	public static Object createKohonen(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		int inputsCount = self.getInt("inputsCount");
		int outputsCount = self.getInt("outputsCount");
		
		return NeuralNetworkFactory.createKohonen(inputsCount, outputsCount);
	}
	
	public static Object createSupervisedHebbian(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		int inputsCount = self.getInt("inputsCount");
		int outputsCount = self.getInt("outputsCount");
		TransferFunctionType transferFunction = createTransferFunctionType(self.getString("transferFunction"));
		
		return NeuralNetworkFactory.createSupervisedHebbian(inputsCount, outputsCount, transferFunction);
	}
	
	public static Object createUnsupervisedHebbian(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		int inputsCount = self.getInt("inputsCount");
		int outputsCount = self.getInt("outputsCount");
		TransferFunctionType transferFunction = createTransferFunctionType(self.getString("transferFunction"));
		
		return NeuralNetworkFactory.createUnsupervisedHebbian(inputsCount, outputsCount, transferFunction);
	}

	public static Object createMaxnet(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		int neuronsCount = self.getInt("neuronsCount");
		
		return NeuralNetworkFactory.createMaxNet(neuronsCount);
	}
	
	public static Object createCompetitiveNetwork(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		int inputsCount = self.getInt("inputsCount");
		int outputsCount = self.getInt("outputsCount");
		
		return NeuralNetworkFactory.createCompetitiveNetwork(inputsCount, outputsCount);
	}
	
	public static Object createRBF(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		int inputsCount = self.getInt("inputsCount");
		int outputsCount = self.getInt("outputsCount");
		int rbfNeuronsCount = self.getInt("rbfNeuronsCount");
		
		return NeuralNetworkFactory.createRbfNetwork(inputsCount, rbfNeuronsCount, outputsCount);
	}
	
	public static Object createInstar(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		int inputsCount = self.getInt("inputsCount");
		
		return NeuralNetworkFactory.createInstar(inputsCount);
	}
	
	public static Object createOutstar(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		int outputsCount = self.getInt("outputsCount");
		
		return NeuralNetworkFactory.createOutstar(outputsCount);
	}
}
