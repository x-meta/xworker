package xworker.swt.xwidgets.clock.tasks;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.xwidgets.clock.Clock;
import xworker.swt.xwidgets.clock.ClockTask;

public class ClockTaskGroup implements ClockTask{
	Thing thing;
	ActionContext actionContext;
	List<ClockTask> clockTasks = new ArrayList<ClockTask>();
	int index = 0;
	
	public ClockTaskGroup(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
		
		init();
	}
	
	private void init() {
		clockTasks.clear();
		index = 0;
		Thing tasks = thing.getThing("ClockTasks@0");
		if(tasks != null) {
			for(Thing child : tasks.getChilds()) {
				Object obj = child.doAction("create", actionContext);
				if(obj instanceof ClockTask) {
					clockTasks.add((ClockTask) obj);
				}
			}
		}
		
	}
	
	@Override
	public boolean isFinished(Clock clock) {
		if(clockTasks.size() == 0) {
			return true;
		}
		if(index < clockTasks.size() -1) {
			return false;
		}else if(index >= clockTasks.size()) {
			return true;
		}
		
		return clockTasks.get(index).isFinished(clock);
	}

	@Override
	public void run(Clock clock) {
		if(index >= clockTasks.size()) {
			return;
		}
		
		if(clockTasks.get(index).isFinished(clock)) {
			index++;
		}
		if(index < clockTasks.size()) {
			clockTasks.get(index).run(clock);;
		}
		
		
	}

	@Override
	public long getInterval(Clock clock) {
		if(index >= clockTasks.size()) {
			return 0;
		}
		
		return clockTasks.get(index).getInterval(clock);
	}

	public static void run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Clock clock = self.doAction("getClock", actionContext);
		clock.setTask(new ClockTaskGroup(self, actionContext));
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		return new ClockTaskGroup(self, actionContext);
	}
}
