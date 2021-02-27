package xworker.org.apache.kafka.streams.kstream;

import java.time.Duration;
import java.time.Instant;

import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.UnlimitedWindows;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class WindowsActions {
	public static TimeWindows createTimeWindows(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Duration size = self.doAction("getSize", actionContext);
		Duration advance = self.doAction("getAdvance", actionContext);
		Duration afterWindowEnd = self.doAction("getAfterWindowEnd", actionContext);

		TimeWindows sw = TimeWindows.of(size);
		if(advance != null) {
			sw.advanceBy(advance);
		}
		if(afterWindowEnd != null) {
			sw.grace(afterWindowEnd);
		}
		
		actionContext.l().put(self.getMetadata().getName(), sw);
		return sw;
	}
	
	public static UnlimitedWindows createUnlimitedWindows(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		UnlimitedWindows sw = UnlimitedWindows.of();
		Instant ins = self.doAction("getStartOn", actionContext);
		if(ins != null) {
			sw.startOn(ins);
		}
		
		actionContext.l().put(self.getMetadata().getName(), sw);
		return sw;
	}
	
	public static Object getStartOn(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String str = self.getStringBlankAsNull("startOn");
		if(str == null) {
			return null;
		}else if("now".equals(str)) {
			return Instant.now();
		}else {
			return Instant.parse(str);
		}
	}
}
