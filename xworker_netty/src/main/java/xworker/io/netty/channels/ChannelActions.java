package xworker.io.netty.channels;

import org.xmeta.ActionContext;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.rxtx.RxtxChannel;
import io.netty.channel.sctp.nio.NioSctpChannel;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.udt.nio.NioUdtByteAcceptorChannel;
import io.netty.channel.udt.nio.NioUdtByteConnectorChannel;
import io.netty.channel.udt.nio.NioUdtMessageAcceptorChannel;

public class ChannelActions {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createEpollDatagramChannel(ActionContext actionContext) {
		AbstractBootstrap bootstrap = actionContext.getObject("bootstrap");
		bootstrap.channel(EpollDatagramChannel.class);	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createEpollServerSocketChannel(ActionContext actionContext) {
		AbstractBootstrap bootstrap = actionContext.getObject("bootstrap");
		bootstrap.channel(EpollServerSocketChannel.class);	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createEpollSocketChannel(ActionContext actionContext) {
		AbstractBootstrap bootstrap = actionContext.getObject("bootstrap");
		bootstrap.channel(EpollSocketChannel.class);	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createLocalChannel(ActionContext actionContext) {
		AbstractBootstrap bootstrap = actionContext.getObject("bootstrap");
		bootstrap.channel(LocalChannel.class);	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createLocalServerChannel(ActionContext actionContext) {
		AbstractBootstrap bootstrap = actionContext.getObject("bootstrap");
		bootstrap.channel(LocalServerChannel.class);	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createNioDatagramChannel(ActionContext actionContext) {
		AbstractBootstrap bootstrap = actionContext.getObject("bootstrap");
		bootstrap.channel(NioDatagramChannel.class);	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createNioSctpChannel(ActionContext actionContext) {
		AbstractBootstrap bootstrap = actionContext.getObject("bootstrap");
		bootstrap.channel(NioSctpChannel.class);	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createNioSctpServerChannel(ActionContext actionContext) {
		AbstractBootstrap bootstrap = actionContext.getObject("bootstrap");
		bootstrap.channel(NioSctpServerChannel.class);	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createNioServerSocketChannel(ActionContext actionContext) {
		AbstractBootstrap bootstrap = actionContext.getObject("bootstrap");
		bootstrap.channel(NioServerSocketChannel.class);	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createNioSocketChannel(ActionContext actionContext) {
		AbstractBootstrap bootstrap = actionContext.getObject("bootstrap");
		bootstrap.channel(NioSocketChannel.class);	
	}


}
