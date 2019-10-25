package xworker.statistics.flow;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;

public class DataSaver {
	public Thing thing;
	public ActionContext actionContext;
	public FlowStatistics flowStatistics;
	public Object object;
	
	public DataSaver(FlowStatistics flowStatistics, Thing thing, ActionContext actionContext){
		this.flowStatistics = flowStatistics;
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	public void init(){
		thing.doAction("init", actionContext, "dataSaver", this);
	}
	
	public void close(){
		thing.doAction("close", actionContext, "dataSaver", this);
	}
	
	public void save(DataObject data){
		thing.doAction("save", actionContext, "data", data, "dataSaver", this);
	}	
}
