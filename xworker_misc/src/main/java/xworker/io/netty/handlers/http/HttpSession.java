package xworker.io.netty.handlers.http;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
	public static final String SESSIONID = "JSESSIONID";
	
	Map<String, Object> attributes = new HashMap<String, Object>();
	String sessionId;
	boolean first = true;
	long lastActiveTime = System.currentTimeMillis();
	
	public HttpSession(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public void active() {
		lastActiveTime = System.currentTimeMillis();
	}
	
	public boolean isExpired() {
		//15分钟的过期时间
		return (System.currentTimeMillis() - lastActiveTime) > 15 * 60000; 
	}
	
	public Object getAttribute(String key) {
		return attributes.get(key);
	}
	
	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}
	
	
}
