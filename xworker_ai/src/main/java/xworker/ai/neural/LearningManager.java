package xworker.ai.neural;

import java.util.ArrayList;
import java.util.List;

public class LearningManager {
	private static List<Learning> trainingNetworks = new ArrayList<Learning>();
	
	private static List<LearningManagerListener> listeners = new ArrayList<LearningManagerListener>();
	
	public void addListener(LearningManagerListener listener){
		listeners.add(listener);
	}
	
	public void removeListener(LearningManagerListener listener){
		listeners.remove(listener);
	}
	
	public static void addTrainingNetwork(Learning network){
		trainingNetworks.add(network);
		
		for(LearningManagerListener listener : listeners){
			listener.learningAdded(network);
		}
	}
	
	public static void removeTrainningNetwork(Learning network){
		trainingNetworks.remove(network);
		
		for(LearningManagerListener listener : listeners){
			listener.learningRemoved(network);
		}
	}
	
	public static List<Learning> getTrainingNetworks(){
		return trainingNetworks;
	}
}
