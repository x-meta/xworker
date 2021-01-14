package xworker.io.netty;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettySessionManager {
	Map<String, NettySession> sessions = new ConcurrentHashMap<String, NettySession>();
	Map<String, NettySessionGroup> groups = new HashMap<String, NettySessionGroup>();
	
	public NettySessionManager() {		
	}
	
	public void putSession(String sessionId, NettySession session) {
		sessions.put(sessionId, session);
	}
	
	public NettySession removeSession(String sessionId) {
		return sessions.remove(sessionId);
	}
	
	public NettySession getSession(String sessionId) {
		return sessions.get(sessionId);
	}
	
	public boolean sendMessage(String sessionId, Object message) throws IOException {
		NettySession session = sessions.get(sessionId);
		if(session != null) {
			session.sendMessage(message);
			return true;
		}else {
			return false;
		}
	}
	
	public void sendMessageToGroup(String groupPath, Object message) throws IOException {
		NettySessionGroup group = getGroup(groupPath);
		group.sendMessage(message);
	}
	
	public void addToGroup(String groupPath, NettySession session) {
		NettySessionGroup group = getGroup(groupPath);
		
		group.addSession(session);
	}
	
	public NettySessionGroup getGroup(String groupPath) {
		synchronized(groups) {
			String[] paths = groupPath.split("[.]");
			NettySessionGroup group = groups.get(paths[0]);
			if(group == null) {
				group = new NettySessionGroup();
				groups.put(paths[0], group);
			}
			
			for(int i=1; i<paths.length; i++) {
				group = group.getGroup(paths[i]);
			}
			
			return group;
		}
	}
}

