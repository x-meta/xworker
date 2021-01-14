package xworker.org.apache.kafka.streams.kstream;

import java.time.Duration;
import java.util.Map;

import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.Suppressed.BufferConfig;
import org.apache.kafka.streams.kstream.Suppressed.EagerBufferConfig;
import org.apache.kafka.streams.kstream.Suppressed.StrictBufferConfig;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

public class SuppressedActions {
	public static EagerBufferConfig createEagerBufferConfig(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		EagerBufferConfig config = null;
		Boolean emitEarlyWhenFull = self.doAction("isEmitEarlyWhenFull", actionContext);
		Boolean loggingDisabled = self.doAction("isLoggingDisabled", actionContext);
		Integer maxBytes = self.doAction("getMaxBytes", actionContext);
		Integer maxRecords = self.doAction("getMaxRecords", actionContext);
		Map<String, String> loggingEnabled = self.doAction("getLoggingEnabled", actionContext);
		
		if(maxBytes != null) {
			config = BufferConfig.maxBytes(maxBytes);
		}
		
		if(maxRecords != null) {
			if(config == null) {
				config = BufferConfig.maxRecords(maxRecords);
			}else {
				config.withMaxRecords(maxRecords);
			}
		}
		
		if(config == null) {
			throw new ActionException("Can not create EagerBufferConfig, please set maxBytes or maxRecords, path=" 
					+ self.getMetadata().getPath());
		}
		if(loggingDisabled != null && loggingDisabled) {
			config.withLoggingDisabled();
		}
		
		if(emitEarlyWhenFull != null && emitEarlyWhenFull) {
			config.emitEarlyWhenFull();
		}
		
		if(loggingEnabled != null) {
			config.withLoggingEnabled(loggingEnabled);
		}
		
		actionContext.l().put(self.getMetadata().getName(), config);
		return config;
	}
	
	public static StrictBufferConfig createStrictBufferConfig(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		StrictBufferConfig config = null;
		Boolean loggingDisabled = self.doAction("isLoggingDisabled", actionContext);
		Integer maxBytes = self.doAction("getMaxBytes", actionContext);
		Integer maxRecords = self.doAction("getMaxRecords", actionContext);
		Map<String, String> loggingEnabled = self.doAction("getLoggingEnabled", actionContext);
		Boolean shutDownWhenFull = self.doAction("isShutDownWhenFull", actionContext);
		config = BufferConfig.unbounded();
				
		if(maxBytes != null) {
			config.withMaxBytes(maxBytes);
		}
		
		if(maxRecords != null) {
			config.withMaxRecords(maxRecords);
		}
		
		if(config == null) {
			throw new ActionException("Can not create StrictBufferConfig, please set maxBytes or maxRecords, path=" 
					+ self.getMetadata().getPath());
		}
		if(loggingDisabled != null && loggingDisabled) {
			config.withLoggingDisabled();
		}
		
		if(shutDownWhenFull != null && shutDownWhenFull) {
			config.shutDownWhenFull();
		}
		
		if(loggingEnabled != null) {
			config.withLoggingEnabled(loggingEnabled);
		}
		
		actionContext.l().put(self.getMetadata().getName(), config);
		return config;
	}
	
	@SuppressWarnings("rawtypes")
	public static Object createUntilTimeLimitSuppressed(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String suppressName = self.doAction("getSuppressName", actionContext);
		Duration timeToWaitForMoreEvents = self.doAction("getTimeToWaitForMoreEvents", actionContext);

		BufferConfig config = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof BufferConfig) {
				config = (BufferConfig) obj;
				break;
			}
		}
		Suppressed s = Suppressed.untilTimeLimit(timeToWaitForMoreEvents, config);
		if(suppressName != null && !"".equals(suppressName)) {
			s.withName(suppressName);
		}
		
		actionContext.l().put(self.getMetadata().getName(), s);
		return s;
	}
	
	@SuppressWarnings("rawtypes")
	public static Object createUntilWindowClosesSuppressed(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String suppressName = self.doAction("getSuppressName", actionContext);

		StrictBufferConfig config = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof StrictBufferConfig) {
				config = (StrictBufferConfig) obj;
				break;
			}
		}
		Suppressed s = Suppressed.untilWindowCloses(config);
		if(suppressName != null && !"".equals(suppressName)) {
			s.withName(suppressName);
		}
		
		actionContext.l().put(self.getMetadata().getName(), s);
		return s;
	}
}
