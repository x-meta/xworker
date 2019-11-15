package xworker.swt.reacts.filters;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.reacts.DataFilter;
import xworker.swt.reacts.DataReactor;

public class ThingDataFilter extends DataFilter{
	Thing thing;
	ActionContext actionContext;
	
	public ThingDataFilter(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public List<Object> filterDatas(DataReactor dataReactor, byte event, List<Object> datas) {
		return thing.doAction("filterDatas", actionContext, "dataReactor", dataReactor, "event", event, "datas", datas);
	}

	@Override
	public boolean filterEvents(DataReactor dataReactor, byte event, List<Object> datas) {
		return thing.doAction("filterEvents", actionContext, "dataReactor", dataReactor, "event", event, "datas", datas);
	}
	

	public static List<Object> doFilterDatas(ActionContext actionContext){
		return actionContext.getObject("datas");
	}
	
	public static Boolean doFilterEvents(ActionContext actionContext) {
		return true;
	}
	
	public static ThingDataFilter create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ThingDataFilter dataFilter = new ThingDataFilter(self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), dataFilter);
		return dataFilter;
	}
}
