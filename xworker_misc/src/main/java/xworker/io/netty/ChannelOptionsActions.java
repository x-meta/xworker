package xworker.io.netty;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;

public class ChannelOptionsActions {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		AbstractBootstrap bootstrap = actionContext.getObject("bootstrap");
		if(self.getStringBlankAsNull("ALLOW_HALF_CLOSURE") != null) {
			bootstrap.option(ChannelOption.ALLOW_HALF_CLOSURE, self.getBoolean("ALLOW_HALF_CLOSURE"));
		}
		if(self.getStringBlankAsNull("AUTO_READ") != null) {
			bootstrap.option(ChannelOption.AUTO_READ, self.getBoolean("AUTO_READ"));
		}
		if(self.getStringBlankAsNull("SO_BROADCAST") != null) {
			bootstrap.option(ChannelOption.SO_BROADCAST, self.getBoolean("SO_BROADCAST"));
		}
		if(self.getStringBlankAsNull("SO_KEEPALIVE") != null) {
			bootstrap.option(ChannelOption.SO_KEEPALIVE, self.getBoolean("SO_KEEPALIVE"));
		}
		if(self.getStringBlankAsNull("SO_SNDBUF") != null) {
			bootstrap.option(ChannelOption.SO_SNDBUF, self.getInt("SO_SNDBUF"));
		}
		if(self.getStringBlankAsNull("SO_RCVBUF") != null) {
			bootstrap.option(ChannelOption.SO_RCVBUF, self.getInt("SO_RCVBUF"));
		}
		if(self.getStringBlankAsNull("SO_REUSEADDR") != null) {
			bootstrap.option(ChannelOption.SO_REUSEADDR, self.getBoolean("SO_REUSEADDR"));
		}
		if(self.getStringBlankAsNull("SO_LINGER") != null) {
			bootstrap.option(ChannelOption.SO_LINGER, self.getInt("SO_LINGER"));
		}
		if(self.getStringBlankAsNull("SO_BACKLOG") != null) {
			bootstrap.option(ChannelOption.SO_BACKLOG, self.getInt("SO_BACKLOG"));
		}
		if(self.getStringBlankAsNull("SO_TIMEOUT") != null) {
			bootstrap.option(ChannelOption.SO_TIMEOUT, self.getInt("SO_TIMEOUT"));
		}
		if(self.getStringBlankAsNull("IP_TOS") != null) {
			bootstrap.option(ChannelOption.IP_TOS, self.getInt("IP_TOS"));
		}
		if(self.getStringBlankAsNull("IP_MULTICAST_TTL") != null) {
			bootstrap.option(ChannelOption.IP_MULTICAST_TTL, self.getInt("IP_MULTICAST_TTL"));
		}
		if(self.getStringBlankAsNull("IP_MULTICAST_LOOP_DISABLED") != null) {
			bootstrap.option(ChannelOption.IP_MULTICAST_LOOP_DISABLED, self.getBoolean("IP_MULTICAST_LOOP_DISABLED"));
		}
		if(self.getStringBlankAsNull("TCP_NODELAY") != null) {
			bootstrap.option(ChannelOption.TCP_NODELAY, self.getBoolean("TCP_NODELAY"));
		}
		
	}
	
	public static void createChild(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ServerBootstrap bootstrap = actionContext.getObject("bootstrap");
		if(self.getStringBlankAsNull("ALLOW_HALF_CLOSURE") != null) {
			bootstrap.childOption(ChannelOption.ALLOW_HALF_CLOSURE, self.getBoolean("ALLOW_HALF_CLOSURE"));
		}
		if(self.getStringBlankAsNull("AUTO_READ") != null) {
			bootstrap.childOption(ChannelOption.AUTO_READ, self.getBoolean("AUTO_READ"));
		}
		if(self.getStringBlankAsNull("SO_BROADCAST") != null) {
			bootstrap.childOption(ChannelOption.SO_BROADCAST, self.getBoolean("SO_BROADCAST"));
		}
		if(self.getStringBlankAsNull("SO_KEEPALIVE") != null) {
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, self.getBoolean("SO_KEEPALIVE"));
		}
		if(self.getStringBlankAsNull("SO_SNDBUF") != null) {
			bootstrap.childOption(ChannelOption.SO_SNDBUF, self.getInt("SO_SNDBUF"));
		}
		if(self.getStringBlankAsNull("SO_RCVBUF") != null) {
			bootstrap.childOption(ChannelOption.SO_RCVBUF, self.getInt("SO_RCVBUF"));
		}
		if(self.getStringBlankAsNull("SO_REUSEADDR") != null) {
			bootstrap.childOption(ChannelOption.SO_REUSEADDR, self.getBoolean("SO_REUSEADDR"));
		}
		if(self.getStringBlankAsNull("SO_LINGER") != null) {
			bootstrap.childOption(ChannelOption.SO_LINGER, self.getInt("SO_LINGER"));
		}
		if(self.getStringBlankAsNull("SO_BACKLOG") != null) {
			bootstrap.childOption(ChannelOption.SO_BACKLOG, self.getInt("SO_BACKLOG"));
		}
		if(self.getStringBlankAsNull("SO_TIMEOUT") != null) {
			bootstrap.childOption(ChannelOption.SO_TIMEOUT, self.getInt("SO_TIMEOUT"));
		}
		if(self.getStringBlankAsNull("IP_TOS") != null) {
			bootstrap.childOption(ChannelOption.IP_TOS, self.getInt("IP_TOS"));
		}
		if(self.getStringBlankAsNull("IP_MULTICAST_TTL") != null) {
			bootstrap.childOption(ChannelOption.IP_MULTICAST_TTL, self.getInt("IP_MULTICAST_TTL"));
		}
		if(self.getStringBlankAsNull("IP_MULTICAST_LOOP_DISABLED") != null) {
			bootstrap.childOption(ChannelOption.IP_MULTICAST_LOOP_DISABLED, self.getBoolean("IP_MULTICAST_LOOP_DISABLED"));
		}
		if(self.getStringBlankAsNull("TCP_NODELAY") != null) {
			bootstrap.childOption(ChannelOption.TCP_NODELAY, self.getBoolean("TCP_NODELAY"));
		}
		
	}
}
