package xworker.ai.neuroph;

import java.io.File;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

import ognl.OgnlException;

public class NeurophNetwork {
	private static String key = "__NeuralNetwork__";
		
	public static Network create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Network network = (Network) self.getData(key);		
		if(network == null || network.isChanged(self)){
			//如果正在学习，那么停止学习
			if(network != null && network.isLarning()){
				network.stopLearning();
			}
			
			return (Network) self.doAction("load", actionContext);		
		}else{
			//如果文件存在，判断是否
			File file = (File) self.doAction("getFile", actionContext);
			if(file != null && file.exists()){
				if(!network.isFileChanged(file)){
					return network;
				}else{
					return (Network) self.doAction("load", actionContext);
				}
			}else{
				return network;
			}
		}
	}
	
	public static void clearCache(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		self.setData(key, null);
	}
	
	@SuppressWarnings("rawtypes")
	public static Network load(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Network work = (Network) self.getData(key);	
		
		NeuralNetwork network = null;		
		File file = (File) self.doAction("getFile", actionContext);
		
		if(file  != null && file.exists()){
			network = NeuralNetwork.createFromFile(file);
			
			if(work == null){
				work = new Network(self, network, Network.READY, file.lastModified());
				self.setData(key, work);
			}else{
				work.setNeuralNetwork(network, self.getMetadata().getLastModified(), file.lastModified());
			}
			
			return work;
		}else{			
			network = (NeuralNetwork) self.doAction("createNeuralNetwork", actionContext);
			if(work == null){
				work = new Network(self, network, Network.READY, 0);
				self.setData(key, work);
			}else{
				work.setNeuralNetwork(network, self.getMetadata().getLastModified(), 0);
			}
			//self.setData(key + "lastModified", file.lastModified());
			
			return work;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void loadLearnNeuralWork(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Network work = (Network) self.getData(key);	
		
		NeuralNetwork network = null;		
		File file = (File) self.doAction("getFile", actionContext);
		
		if(file  != null && file.exists()){
			network = NeuralNetwork.createFromFile(file);
			
			work.setLearnNeuralNetwork(network);
			
		}else{
			network = (NeuralNetwork) self.doAction("createNeuralNetwork", actionContext);
			work.setLearnNeuralNetwork(network);
		}
	}
	
	public static File getFile(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Object obj = self.getObject("filePath", actionContext);
		
		if(obj == null){
			String path = World.getInstance().getPath() + "/work/ai/Neuroph/" + self.getMetadata().getPath().replace('.', '/');
			path = path + "/" + self.getMetadata().getName() + ".nnet";
			
			return new File(path);
		}else{
			if(obj instanceof File){
				return (File) obj;
			}else if(obj instanceof String){
				return new File((String) obj);
			}else{
				return null;
			}
		}
	}
	
	public static boolean save(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Network network = (Network) self.getData(key);
		if(network == null){
			return false;
		}
		
		File file = (File) self.doAction("getFile", actionContext);
		if(file != null){
			if(!file.exists()){
				file.getParentFile().mkdirs();
			}
			
			network.getLearnNeuralNetwork().save(file.getAbsolutePath());
			
			return true;
		}else{
			return false;
		}
	}
	
	public static DataSet getDataSet(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Thing datasets = self.getThing("Datasets@0");
		if(datasets != null && datasets.getChilds().size() > 0){
			return (DataSet) datasets.getChilds().get(0).doAction("create", actionContext);
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static NeuralNetwork createNeuralNetwork(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Thing neuralNetworks = self.getThing("NeuralNetworks@0");
		if(neuralNetworks != null && neuralNetworks.getChilds().size() > 0){
			return (NeuralNetwork) neuralNetworks.getChilds().get(0).doAction("create", actionContext);
		}else{
			return null;
		}
	}
	
	public static DataSet learn(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Network network = (Network) self.doAction("create", actionContext);
		if(network != null && network.getLearnNeuralNetwork() == null){
			loadLearnNeuralWork(actionContext);
		}
		DataSet dataSet = (DataSet) self.doAction("getDataSet", actionContext);
		if(network == null){
			throw new ActionException("Can not train network, network is null, path=" + self.getMetadata().getPath());
		}
		if(dataSet == null){
			throw new ActionException("Can not train network, dataSet is null, path=" + self.getMetadata().getPath());
		}
		network.getLearnNeuralNetwork().learn(dataSet);
		
		return dataSet;
	}
}
