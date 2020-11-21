package xworker.io.netty.handlers.ssl;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.channel.Channel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class TestSSLHandler {
	public static Object createClientHandler(ActionContext actionContext) throws SSLException {
		Thing self = actionContext.getObject("self");
		
		String key = "__sslCTX__";
		SslContext sslCtx = self.getData(key);
		if(sslCtx == null) {
			sslCtx = SslContextBuilder.forClient()
		            .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
			self.setData(key, sslCtx);
		}
		
		String host = self.doAction("getHost", actionContext);
		int port = self.doAction("getPort", actionContext);
		Channel channel = actionContext.getObject("channel");
		if(host != null && port > 0) {
			return sslCtx.newHandler(channel.alloc(), host, port); 
		}else {
			return sslCtx.newHandler(channel.alloc());
		}
	}
	
	public static void clearContext(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String key = "__sslCTX__";
		self.setData(key, null);
	}
	
	public static Object createServerHandler(ActionContext actionContext) throws SSLException, CertificateException {
		Thing self = actionContext.getObject("self");
		
		String key = "__sslCTX__";
		SslContext sslCtx = self.getData(key);
		if(sslCtx == null) {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
	        sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
	            .build();
			self.setData(key, sslCtx);
		}
		
		Channel channel = actionContext.getObject("channel");
		return sslCtx.newHandler(channel.alloc());
	}
}
