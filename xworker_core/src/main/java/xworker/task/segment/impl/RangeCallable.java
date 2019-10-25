package xworker.task.segment.impl;

import java.util.concurrent.Callable;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import xworker.task.segment.Range;
import xworker.task.segment.SegmentTask;

public class RangeCallable implements Callable<Boolean>{
	ActionContext actionContext;
	Thing thing;
	Range range;
	SegmentTask task;
	
	public RangeCallable(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
		
		this.range = actionContext.getObject("range");
		this.task = actionContext.getObject("task");
	}
	
	@Override
	public Boolean call() throws Exception {
		return UtilData.isTrue(thing.doAction("doRange", actionContext, "range", range, "task", task));
	}

	public static RangeCallable run(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		RangeCallable call = new RangeCallable(self, actionContext);		
		return call;
	}
}
