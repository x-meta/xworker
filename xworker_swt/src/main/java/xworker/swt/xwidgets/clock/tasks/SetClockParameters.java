package xworker.swt.xwidgets.clock.tasks;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.xwidgets.clock.Clock;
import xworker.swt.xwidgets.clock.ClockDrawer;

public class SetClockParameters {
	public static void run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Clock clock = self.doAction("getClock", actionContext);
		ClockDrawer clockDrawer = clock.getClockDrawer();
		
		clockDrawer.setShowAmPm((Boolean) self.doAction("isShowAmPm", actionContext));
		clockDrawer.setShowArea((Boolean) self.doAction("isShowArea", actionContext));
		clockDrawer.setShowSecond((Boolean) self.doAction("isShowSecond", actionContext));
		clockDrawer.setShowMinute((Boolean) self.doAction("isShowMinute", actionContext));
		clockDrawer.setShowHour((Boolean) self.doAction("isShowHour", actionContext));
	}
}
