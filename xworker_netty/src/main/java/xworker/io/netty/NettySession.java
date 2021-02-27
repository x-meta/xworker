package xworker.io.netty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class NettySession {
	protected static final String KEY = "__xworker_session_key__";
	NettyClient client;
	Channel channel;
	String remoteIp;
	boolean isClient = false;
	List<NettySessionGroup> groups = new ArrayList<NettySessionGroup>();
	String sessionId = null;
	NettySessionManager sessionManager;
	
	public NettySession(NettySessionManager sessionManager, Channel channel) {
		this.sessionManager = sessionManager;
		this.channel = channel;
		if(channel.remoteAddress() != null) {
			this.remoteIp = channel.remoteAddress().toString();
		}
	}
	
	public NettySessionManager getSessionManager() {
		return sessionManager;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
		sessionManager.putSession(sessionId, this);
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public void sendMessage(Object message) throws IOException {
		channel.writeAndFlush(message);
	}
		
	public Channel getChannel() {
		return channel;
	}

	public void setAttribute(String key, Object value) {
		AttributeKey<Object> akey =  AttributeKey.valueOf(key);
		Attribute<Object> attr = channel.attr(akey);
		attr.set(value);
	}

	public Object getAttribute(String key) {
		AttributeKey<Object> akey =  AttributeKey.valueOf(key);
		Attribute<Object> attr = channel.attr(akey);
		return attr.get();
	}

	public boolean isClosed() {
		return !channel.isActive() && !channel.isOpen();
	}

	public String getRemoteIp() {
		return remoteIp;
	}
	
	protected void addGroup(NettySessionGroup group) {
		if(groups.contains(group) == false) {
			groups.add(group);
		}
	}
	
	public void addToGroup(String groupPath) {
		sessionManager.addToGroup(groupPath, this);
	}
	
	public void removeFromGroup(String groupPath) {
		NettySessionGroup group = sessionManager.getGroup(groupPath);
		group.removeSession(this);
		for(NettySessionGroup g : groups) {
			g.removeSession(this);
		}
	}
	
	public void dispose() {
		sessionManager.removeSession(sessionId);
		
		for(NettySessionGroup group : groups) {
			group.removeSession(this);
		}
	}
	
	public static NettySession getSession(Channel ch) {
		AttributeKey<Object> akey =  AttributeKey.valueOf(KEY);
		Attribute<Object> attr = ch.attr(akey);
		return (NettySession) attr.get();
	}
}
