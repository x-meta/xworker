package xworker.statistics.flow;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;

public class KeyManager {
	public Thing thing;
	public ActionContext actionContext;
	public FlowStatistics flowStatistics;
	
	public KeyManager(FlowStatistics flowStatistics, Thing thing, ActionContext actionContext){
		this.flowStatistics = flowStatistics;
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@SuppressWarnings("unchecked")
	public List<KeyData> generateKey(Object data){
		return (List<KeyData>) thing.doAction("generateKey", actionContext, "data", data);
	}
	
	public DataObject getDataObject(String key, long count){
		return (DataObject) thing.doAction("getDataObject", actionContext, "key", key, "count", count);
	}
	
	public boolean isCompleted(String key){
		return (Boolean) thing.doAction("isCompleted", actionContext, "key", key);
	}
}
