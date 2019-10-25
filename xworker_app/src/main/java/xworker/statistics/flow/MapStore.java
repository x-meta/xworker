package xworker.statistics.flow;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class MapStore {
	public Thing thing;
	public ActionContext actionContext;
	public FlowStatistics flowStatistics;
	public Object object;
	
	public MapStore(FlowStatistics flowStatistics, Thing thing, ActionContext actionContext){
		this.flowStatistics = flowStatistics;
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	public void inc(String key, long count){
		thing.doAction("inc", actionContext, "key", key, "count", count, "mapStore", this);
	}
	
	public long getKeyCount(String key){
		Long l = thing.doAction("getKeyCount", actionContext, "key", key, "mapStore", this);
		if(l == null){
			return 0;
		}else{
			return l;
		}
	}
	
	public boolean exists(String key){
		return thing.doAction("exists", actionContext, "key", key, "mapStore", this);
	}
	
	public void addToKeyList(String key, String keyManager){
		thing.doAction("addToKeyList", actionContext, "key", key, "keyManager", keyManager, "mapStore", this);
	}
	
	public List<String> getKeyList(String keyManager){
		return thing.doAction("getKeyList", actionContext, "keyManager", keyManager, "mapStore", this);
	}
	
	public void removeKey(String key, String keyManager){
		thing.doAction("removeKey", actionContext, "key", key, "keyManager", keyManager, "mapStore", this);
	}
	
	public void close(){
		thing.doAction("close", actionContext, "mapStore", this);
	}
}
