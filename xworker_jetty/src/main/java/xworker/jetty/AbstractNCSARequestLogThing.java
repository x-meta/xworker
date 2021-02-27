package xworker.jetty;

import java.io.IOException;
import java.util.Locale;

import org.eclipse.jetty.server.AbstractNCSARequestLog;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.cache.ThingEntry;

public class AbstractNCSARequestLogThing extends AbstractNCSARequestLog{
	boolean enabled;
	ThingEntry entry;
	ActionContext actionContext;
	
	public AbstractNCSARequestLogThing(Thing thing, ActionContext actionContext) {
		entry = new ThingEntry(thing);
		this.actionContext = actionContext;
		
		enabled = thing.doAction("isEnabled", actionContext);
	}

	@Override
	protected boolean isEnabled() {
		if(entry.isChanged()) {
			Thing thing = entry.getThing();
			enabled = thing.doAction("isEnabled", actionContext);
		}
		
		return enabled;
	}

	@Override
	public void write(String requestEntry) throws IOException {
		Thing thing = entry.getThing();
		thing.doAction("write", actionContext, "requestEntry", requestEntry);
	}
	
	public static void init(AbstractNCSARequestLog log, Thing self, ActionContext actionContext) {
		Locale logLocale = self.doAction("getLogLocale", actionContext);
		if(logLocale != null) {
			log.setLogLocale(logLocale);
		}
		
		String[] ignorePaths = self.doAction("getIgnorePaths", actionContext);
		if(ignorePaths != null && ignorePaths.length > 0) {
			log.setIgnorePaths(ignorePaths);
		}
		
		Boolean extended = self.doAction("isExtended", actionContext);
		if(extended != null) {
			log.setExtended(extended);
		}
		
		Boolean logCookies = self.doAction("isLogCookies", actionContext);
		if(logCookies != null) {
			log.setLogCookies(logCookies);
		}
		
		Boolean preferProxiedForAddress = self.doAction("isPreferProxiedForAddress", actionContext);
		if(preferProxiedForAddress != null) {
			log.setPreferProxiedForAddress(preferProxiedForAddress);
		}
		
		Boolean logLatency = self.doAction("isLogLatency", actionContext);
		if(logLatency != null) {
			log.setLogLatency(logLatency);
		}
		
		Boolean logServer = self.doAction("isLogServer", actionContext);
		if(logServer != null) {
			log.setLogServer(logServer);
		}
		
		String logDateFormat = self.doAction("getLogDateFormat", actionContext);
		if(logDateFormat != null) {
			log.setLogDateFormat(logDateFormat);
		}
		
	}
	
	public static AbstractNCSARequestLogThing create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		AbstractNCSARequestLogThing log =  new AbstractNCSARequestLogThing(self, actionContext);
		AbstractNCSARequestLogThing.init(log, self, actionContext);
		return log;
	}
}
