package xworker.org.apache.kafka.streams.kstream;

import java.time.Duration;

import org.apache.kafka.streams.kstream.JoinWindows;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class JoinWindowsActions {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Duration timeDifference = self.doAction("getTimeDifference", actionContext);
		Duration after = self.doAction("getAfter", actionContext);
		Duration before = self.doAction("getBefore", actionContext);
		Duration grace = self.doAction("getGrace", actionContext);
		
		JoinWindows windows = null;
		if(timeDifference != null) {
			windows = JoinWindows.of(timeDifference);
			
			if(after != null) {
				windows.after(after);
			}
			
			if(before != null) {
				windows.before(before);
			}
			
			if(grace != null) {
				windows.grace(grace);
			}
		}
		
		actionContext.l().put(self.getMetadata().getName(), windows);
		return windows;

	}
}
