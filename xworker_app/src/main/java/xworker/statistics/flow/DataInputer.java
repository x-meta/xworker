package xworker.statistics.flow;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class DataInputer {
	public Thing thing;
	public ActionContext actionContext;
	public FlowStatistics flowStatistics;
	public Object object;
	
	public DataInputer(FlowStatistics flowStatistics, Thing thing, ActionContext actionContext){
		this.flowStatistics = flowStatistics;
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	public void start(){
		thing.doAction("start", actionContext, "flowStatistics", flowStatistics, "dataInputer", this);
	}
	
	public void stop(){
		thing.doAction("stop", actionContext, "flowStatistics", flowStatistics, "dataInputer", this);
	}
	
	public void input(Object data){
		flowStatistics.input(data, this);
	}
	
	public void inputFinished(){
		flowStatistics.inputFinished(this);
	}
}
