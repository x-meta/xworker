package xworker.io.netty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class NettySessionGroup {
	List<NettySession> sessions = new CopyOnWriteArrayList<>();
	Map<String, NettySessionGroup> childGroups = new HashMap<String, NettySessionGroup>();
	
	public NettySessionGroup getGroup(String name) {
		NettySessionGroup group = childGroups.get(name);
		if(group == null) {
			group = new NettySessionGroup();
			childGroups.put(name, group);
		}
		
		return group;
	}
	
	public void addSession(NettySession session) {
		if(!sessions.contains(session)) {
			sessions.add(session);
		}
	}
	
	public void removeSession(NettySession session) {
		sessions.remove(session);
	}
	
	public void sendMessage(Object message) throws IOException {
		List<NettySession> closed = new ArrayList<NettySession>();
		Iterator<NettySession> iter = sessions.iterator();		
		while(iter.hasNext()) {
			NettySession session = iter.next();
			if(session.isClosed()) {
				//iter.remove();
				closed.add(session);
			}else {
				session.sendMessage(message);
			}
		}
		
		for(NettySession s : closed) {
			sessions.remove(s);
		}
	}
	
	public List<NettySession> getSessions(){
		return sessions;
	}
}
