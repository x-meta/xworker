package xworker.org.jsoup;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ConnectionActions {
	public static Connection run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String url = self.doAction("getUrl", actionContext);
		
		Connection connection = Jsoup.connect(url);		
		initConnection(connection, self, actionContext);
		
		return connection;	
	}
	
	public static void initConnection(Connection connection, Thing self, ActionContext actionContext) {
		Integer timeoutMillis = self.doAction("getTimeoutMillis", actionContext);
		String userAgent = self.doAction("getUserAgent", actionContext);
		Integer maxBodySize = self.doAction("getMaxBodySize", actionContext);
		Boolean ignoreContentType = self.doAction("isIgnoreContentType", actionContext);
		Boolean ignoreHttpErrors = self.doAction("isIgnoreHttpErrors", actionContext);
		Boolean followRedirects = self.doAction("isFollowRedirects", actionContext);
		Map<String, Object> datas = self.doAction("getDatas", actionContext);
		Map<String, Object> heads = self.doAction("getHeads", actionContext);
		Map<String, Object> cookies = self.doAction("getCookies", actionContext);
		
		if(timeoutMillis != null && timeoutMillis > 0) {
			connection.timeout(timeoutMillis);
		}
		if(userAgent != null && !"".equals(userAgent)) {
			connection.userAgent(userAgent);
		}
		if(maxBodySize != null &&maxBodySize > 0) {
			connection.maxBodySize(maxBodySize);
		}
		if(ignoreContentType != null) {
			connection.ignoreContentType(ignoreContentType);
		}
		if(ignoreHttpErrors != null) {
			connection.ignoreHttpErrors(ignoreHttpErrors);
		}
		if(followRedirects != null) {
			connection.followRedirects(followRedirects);
		}
		if(datas != null) {
			for(String key : datas.keySet()) {
				Object value = datas.get(key);
				if(value != null) {
					connection.data(key, String.valueOf(value));
				}
			}
		}
		if(heads != null) {
			for(String key : heads.keySet()) {
				Object value = heads.get(key);
				if(value != null) {
					connection.header(key, String.valueOf(value));
				}
			}
		}
		if(cookies != null) {
			for(String key : cookies.keySet()) {
				Object value = cookies.get(key);
				if(value != null) {
					connection.cookie(key, String.valueOf(value));
				}
			}
		}
		
		for(Thing child : self.getChilds()) {
			child.doAction("run", actionContext, "connection", connection);
		}
	}
	
	public static void header(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Connection connection = actionContext.getObject("connection");
		String name = self.doAction("getName", actionContext);
		String value = self.doAction("getValue", actionContext);

		if(value != null) {
			connection.header(name, value);
		}		
	}
	
	public static void data(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Connection connection = actionContext.getObject("connection");
		String name = self.doAction("getName", actionContext);
		String value = self.doAction("getValue", actionContext);

		if(value != null) {
			connection.data(name, value);
		}		
	}
	
	public static void cookie(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Connection connection = actionContext.getObject("connection");
		String name = self.doAction("getName", actionContext);
		String value = self.doAction("getValue", actionContext);

		if(value != null) {
			connection.cookie(name, value);
		}		
	}
	
	public static void post(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		Connection connection = actionContext.getObject("connection");
		try {
			Document document = connection.post();
			for(Thing child : self.getChilds()) {
				if("Exception".equals(child.getThingName())) {
					continue;
				}
				child.doAction("run", actionContext, "document", document);
			}
		}catch(Exception e) {
			for(Thing child : self.getChilds()) {
				if(!"Exception".equals(child.getThingName())) {
					continue;
				}
				child.doAction("run", actionContext, "exception", e);
			}
		}
	}
	
	public static void get(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		Connection connection = actionContext.getObject("connection");
		try {
			Document document = connection.get();
			for(Thing child : self.getChilds()) {
				if("Exception".equals(child.getThingName())) {
					continue;
				}
				child.doAction("run", actionContext, "document", document);
			}
		}catch(Exception e) {
			for(Thing child : self.getChilds()) {
				if(!"Exception".equals(child.getThingName())) {
					continue;
				}
				child.doAction("run", actionContext, "exception", e);
			}
		}
	}
}
