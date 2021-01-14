package xworker.org.apache.kafka.streams.kstream;

import java.time.Duration;

import org.apache.kafka.streams.kstream.SessionWindows;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class SessionWindowsActions {
	public static SessionWindows create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Duration inactivityGap = self.doAction("getInactivityGap", actionContext);
		Duration afterWindowEnd = self.doAction("getAfterWindowEnd", actionContext);
		
		SessionWindows sw = SessionWindows.with(inactivityGap);
		if(afterWindowEnd != null) {
			sw.grace(afterWindowEnd);
		}
		
		actionContext.l().put(self.getMetadata().getName(), sw);
		
		return sw;

	}
}
