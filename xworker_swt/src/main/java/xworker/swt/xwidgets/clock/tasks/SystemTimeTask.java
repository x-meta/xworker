package xworker.swt.xwidgets.clock.tasks;

import java.util.Date;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.xwidgets.clock.Clock;
import xworker.swt.xwidgets.clock.ClockTask;

/**
 * 定时显示系统时间的任务，间隔500毫秒，不会自动停止，除非Clock用其它任务覆盖了。
 * 
 * @author zyx
 *
 */
public class SystemTimeTask implements ClockTask{

	@Override
	public boolean isFinished(Clock clock) {
		return false;
	}

	@Override
	public void run(Clock clock) {
		clock.setDate(new Date());
	}

	@Override
	public long getInterval(Clock clock) {
		return 500;
	}

	public static void run(ActionContext actionContext) {
		//设置参数
		SetClockParameters.run(actionContext);
		
		Thing self = actionContext.getObject("self");
		Clock clock = self.doAction("getClock", actionContext);
		clock.setTask(new SystemTimeTask());
	}
	
	public static Object create(ActionContext actionContext) {
		return new SystemTimeTask();
	}
}
