package xworker.jetty.handlers;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AsyncDelayHandler;
import org.eclipse.jetty.server.handler.BufferedResponseHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.handler.HotSwapHandler;
import org.eclipse.jetty.server.handler.IdleTimeoutHandler;
import org.eclipse.jetty.server.handler.InetAccessHandler;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.server.handler.ThreadLimitHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class HandlerActions {
	public static Object createAsyncDelayHandler(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		AsyncDelayHandler handler = new AsyncDelayHandler();
		
		initWrapper(self, handler, actionContext);
		
		return handler;
	}
	
	public static Object createBufferedResponseHandler(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		BufferedResponseHandler handler = new BufferedResponseHandler();
		Server server = actionContext.getObject("server");
		if(server != null) {
			handler.setServer(server);
		}
				
		initWrapper(self, handler, actionContext);
		
		return handler;
	}
	
	public static Object createGzipHandler(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		GzipHandler handler = new GzipHandler();
		
		int compressionLevel = self.doAction("getCompressionLevel", actionContext);
		if(compressionLevel > 0){
			handler.setCompressionLevel(compressionLevel);
		}

		String[] excludedAgentPatterns = self.doAction("getExcludedAgentPatterns", actionContext);
		if(excludedAgentPatterns != null){
			handler.setExcludedAgentPatterns(excludedAgentPatterns);
		}

		String[] excludedMethods = self.doAction("getExcludedMethods", actionContext);
		if(excludedMethods != null){
			handler.setExcludedMethods(excludedMethods);
		}

		String[] excludedMimeTypes = self.doAction("getExcludedMimeTypes", actionContext);
		if(excludedMimeTypes != null){
			handler.setExcludedMimeTypes(excludedMimeTypes);
		}

		String[] excludedPaths = self.doAction("getExcludedPaths", actionContext);
		if(excludedPaths != null){
			handler.setExcludedPaths(excludedPaths);
		}

		String[] includedAgentPatterns = self.doAction("getIncludedAgentPatterns", actionContext);
		if(includedAgentPatterns != null){
			handler.setIncludedAgentPatterns(includedAgentPatterns);
		}

		String[] includedMethods = self.doAction("getIncludedMethods", actionContext);
		if(includedMethods != null){
			handler.setIncludedMethods(includedMethods);
		}

		String[] includedMimeTypes = self.doAction("getIncludedMimeTypes", actionContext);
		if(includedMimeTypes != null){
			handler.setIncludedMimeTypes(includedMimeTypes);
		}

		String[] includedPaths = self.doAction("getIncludedPaths", actionContext);
		if(includedPaths != null){
			handler.setIncludedPaths(includedPaths);
		}

		int inflateBufferSize = self.doAction("getInflateBufferSize", actionContext);
		if(inflateBufferSize  > 0){
			handler.setInflateBufferSize(inflateBufferSize);
		}

		int minGzipSize = self.doAction("getMinGzipSize", actionContext);
		if(minGzipSize > 0){
			handler.setMinGzipSize(minGzipSize);
		}

		Boolean syncFlush = self.doAction("isSyncFlush", actionContext);
		if(syncFlush != null){
			handler.setSyncFlush(syncFlush);
		}

		initWrapper(self, handler, actionContext);
		
		return handler;

	}
	
	public static Object createIdleTimeoutHandler(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		IdleTimeoutHandler handler = new IdleTimeoutHandler();
		
		Boolean applyToAsync = self.doAction("isApplyToAsync", actionContext);
		if(applyToAsync != null){
			handler.setApplyToAsync(applyToAsync);
		}

		long idleTimeoutMs = self.doAction("getIdleTimeoutMs", actionContext);
		if(idleTimeoutMs > 0){
			handler.setIdleTimeoutMs(idleTimeoutMs);
		}

		initWrapper(self, handler, actionContext);
		
		return handler;
	}
	
	public static Object createInetAccessHandler(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		InetAccessHandler handler = new InetAccessHandler();
		String[] excludePatterns = self.doAction("getExcludePatterns", actionContext);
		if(excludePatterns != null){
			handler.exclude(excludePatterns);
		}

		String[] includePatterns = self.doAction("getIncludePatterns", actionContext);
		if(includePatterns != null){
			handler.include(includePatterns);
		}

		initWrapper(self, handler, actionContext);
		
		return handler;
	}
	
	public static Object createRequestLogHandler(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		RequestLogHandler handler = new RequestLogHandler();
		Thing requestLogThing = self.doAction("getRequestLog", actionContext);
		if(requestLogThing != null) {
			RequestLog log = requestLogThing.doAction("create", actionContext);
			if(log != null) {
				handler.setRequestLog(log);
			}
		}
		
		initWrapper(self, handler, actionContext);
		
		return handler;		
	}
	
	public static Object createResourceHandler(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ResourceHandler handler = new ResourceHandler();
		Boolean acceptRanges = self.doAction("isAcceptRanges", actionContext);
		if(acceptRanges != null){
			handler.setAcceptRanges(acceptRanges);
		}

		String cacheControl = self.doAction("getCacheControl", actionContext);
		if(cacheControl != null){
			handler.setCacheControl(cacheControl);
		}

		Boolean dirAllowed = self.doAction("isDirAllowed", actionContext);
		if(dirAllowed != null){
			handler.setDirAllowed(dirAllowed);
		}

		Boolean directoriesListed = self.doAction("isDirectoriesListed", actionContext);
		if(directoriesListed != null){
			handler.setDirectoriesListed(directoriesListed);
		}

		Boolean etags = self.doAction("isEtags", actionContext);
		if(etags != null){
			handler.setEtags(etags);
		}

		Boolean pathInfoOnly = self.doAction("isPathInfoOnly", actionContext);
		if(pathInfoOnly != null){
			handler.setPathInfoOnly(pathInfoOnly);
		}

		Boolean redirectWelcome = self.doAction("isRedirectWelcome", actionContext);
		if(redirectWelcome != null){
			handler.setRedirectWelcome(redirectWelcome);
		}

		String resourceBase = self.doAction("getResourceBase", actionContext);
		if(resourceBase != null){
			handler.setResourceBase(resourceBase);
		}

		String stylesheet = self.doAction("getStylesheet", actionContext);
		if(stylesheet != null){
			handler.setStylesheet(stylesheet);
		}

		String[] welcomeFiles = self.doAction("getWelcomeFiles", actionContext);
		if(welcomeFiles != null){
			handler.setWelcomeFiles(welcomeFiles);
		}
		
		initWrapper(self, handler, actionContext);
		
		return handler;	
	}
	
	public static Object createShutdownHandler(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String shutdownToken = self.doAction("getShutdownToken", actionContext);
		ShutdownHandler handler  = new ShutdownHandler(shutdownToken);

		Boolean exitJvm = self.doAction("isExitJvm", actionContext);
		if(exitJvm != null){
			handler.setExitJvm(exitJvm);
		}

		Boolean sendShutdownAtStart = self.doAction("isSendShutdownAtStart", actionContext);
		if(sendShutdownAtStart != null){
			handler.setSendShutdownAtStart(sendShutdownAtStart);
		}

		initWrapper(self, handler, actionContext);
		
		return handler;	
	}
	
	public static Object createStatisticsHandler(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		StatisticsHandler handler = new StatisticsHandler();
		
		initWrapper(self, handler, actionContext);
		
		return handler;	
	}
	
	public static Object createThreadLimitHandler(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ThreadLimitHandler handler = new ThreadLimitHandler();
		Boolean enabled = self.doAction("isEnabled", actionContext);
		if(enabled != null){
			handler.setEnabled(enabled);
		}

		int threadLimit = self.doAction("isThreadLimit", actionContext);
		if(threadLimit > 0){
			handler.setThreadLimit(threadLimit);
		}

		initWrapper(self, handler, actionContext);
		
		return handler;	
	}
	
	public static Object createContextHandlerCollection(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ContextHandlerCollection handler = new ContextHandlerCollection();
		Server server = actionContext.getObject("server");
		if(server != null) {
			handler.setServer(server);
		}
		
		for(Thing handlers : self.getChilds("ContextHandlers")) {
			for(Thing hd : handlers.getChilds()) {
				Handler h = hd.doAction("create", actionContext);
				if(h != null) {
					handler.addHandler(h);
				}
			}
		}
		
		actionContext.g().put(self.getMetadata().getName(), handler);
		return handler;
	}
	
	public static Object createHandlerList(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		HandlerList handler = new HandlerList();
		Server server = actionContext.getObject("server");
		if(server != null) {
			handler.setServer(server);
		}
		
		for(Thing handlers : self.getChilds("Handlers")) {
			for(Thing hd : handlers.getChilds()) {
				Handler h = hd.doAction("create", actionContext);
				if(h != null) {
					handler.addHandler(h);
				}
			}
		}
		
		actionContext.g().put(self.getMetadata().getName(), handler);
		return handler;
	}
	
	public static Object createHotSwapHandler(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		HotSwapHandler handler = new HotSwapHandler();
		Server server = actionContext.getObject("server");
		if(server != null) {
			handler.setServer(server);
		}
		
		for(Thing handlers : self.getChilds("Handlers")) {
			boolean ok = false;
			for(Thing hd : handlers.getChilds()) {
				Handler h = hd.doAction("create", actionContext);
				if(h != null) {
					handler.setHandler(h);
					ok = true;
					break;
				}
			}
			
			if(ok) {
				break;
			}
		}
		
		actionContext.g().put(self.getMetadata().getName(), handler);
		return handler;
	}
	
	public static void initWrapper(Thing self, HandlerWrapper wrapper, ActionContext actionContext) {
		actionContext.g().put(self.getMetadata().getName(), wrapper);
		
		Server server = actionContext.getObject("server");
		if(server != null) {
			wrapper.setServer(server);
		}
			
		
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Handler) {
				wrapper.setHandler((Handler) obj); 
				break;
			}
		}
	}
}
