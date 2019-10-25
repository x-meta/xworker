package xworker.swt.xwidgets.clock.tasks;

import java.util.Date;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.xwidgets.clock.Clock;
import xworker.swt.xwidgets.clock.ClockTask;

public class TimeToTimeTask implements ClockTask{
	Thing thing;
	ActionContext actionContext;
	Date start;
	Date end;	
	long currentTime = -1;
	long totalTime = 0;
	long interval = 100;
	
	public TimeToTimeTask(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
		
		init();
	}
	
	private void init() {
		start = thing.doAction("getStartTime", actionContext);
		end = thing.doAction("getEndTime", actionContext);	
		totalTime = thing.doAction("getDemoTime", actionContext);
		if(start != null) {
			currentTime = start.getTime();
		}
	}
	
	@Override
	public boolean isFinished(Clock clock) {
		if(start == null || end == null || totalTime < interval) { //totalTime最少有一个间隔
			return true;
		}		
		
		return currentTime == end.getTime();
	}

	@Override
	public void run(Clock clock) {
		//每个演示间隔的时间差
		long itime = (end.getTime() - start.getTime()) / (totalTime / interval); 
		currentTime += itime;
		clock.setDate(new Date(currentTime));
		
		if(end.getTime() > start.getTime()) {
			if(currentTime > end.getTime()) {
				currentTime = end.getTime();
				clock.setDate(end);
			}
		}else if(end.getTime() < start.getTime()) {
			if(currentTime < end.getTime()) {
				currentTime = end.getTime();
				clock.setDate(end);
			}
		}
		
	}

	@Override
	public long getInterval(Clock clock) {
		return interval;
	}

	public static void run(ActionContext actionContext) {
		//设置参数
		SetClockParameters.run(actionContext);
				
		Thing self = actionContext.getObject("self");
		Clock clock = self.doAction("getClock", actionContext);
		clock.setTask(new TimeToTimeTask(self, actionContext));
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		return new TimeToTimeTask(self, actionContext);
	}

}
