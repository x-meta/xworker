package xworker.lang.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.xmeta.Thing;
import org.xmeta.World;

public class StateManager {
	private static Map<String, List<State>> statesCache = new ConcurrentHashMap<String, List<State>>();
	
	public static void regist(State state) {
		String path = state.getThing().getMetadata().getPath();
		List<State> states = getStates(path);
		if(!states.contains(state)) {
			states.add(state);
		}
	}
	
	public static List<State> getStates(String path){
		synchronized(statesCache) {
			List<State> states = statesCache.get(path);
			if(states == null) {
				states = new java.util.concurrent.CopyOnWriteArrayList<>();
				statesCache.put(path, states);
			}
			
			return states;
			
		}		
	}
	
	public static List<Thing> getStateThings(){
		List<Thing> list = new ArrayList<Thing>();
		for(String path : statesCache.keySet()) {
			Thing thing = World.getInstance().getThing(path);
			if(thing != null) {
				list.add(thing);
			}
		}
		
		return list;
	}
	
	public static void unregist(State state) {
		String path = state.getThing().getMetadata().getPath();
		List<State> states = getStates(path);
		states.remove(state);
	}
	
	
}
