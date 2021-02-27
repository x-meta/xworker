package xworker.org.apache.kafka.streams.kstream;

import java.time.Duration;

import org.apache.kafka.streams.kstream.SlidingWindows;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class SlidingWindowsActions {
	public static SlidingWindows create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Duration timeDifference = self.doAction("getTimeDifference", actionContext);
		Duration grace = self.doAction("getGrace", actionContext);

		SlidingWindows sw = SlidingWindows.withTimeDifferenceAndGrace(timeDifference, grace);
		
		actionContext.l().put(self.getMetadata().getName(), sw);
		return sw;
	}
}
