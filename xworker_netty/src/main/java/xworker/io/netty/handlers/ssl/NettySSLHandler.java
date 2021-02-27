package xworker.io.netty.handlers.ssl;

import java.io.File;
import java.security.cert.CertificateException;
import java.util.List;

import javax.net.ssl.SSLException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.channel.Channel;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslProvider;

public class NettySSLHandler {
	public static Object createClientHandler(ActionContext actionContext) throws SSLException {
		return createHandler(false, actionContext);
	}

	
	public static Object createServerHandler(ActionContext actionContext) throws SSLException, CertificateException {
		return createHandler(true, actionContext);
	}
	
	public static SslHandler createHandler(boolean server, ActionContext actionContext) throws SSLException {
		Thing self = actionContext.getObject("self");
		String key = "__sslCTX__";
		SslContext sslCtx = self.getCachedData(key);
		if(sslCtx == null) {
			SslContextBuilder sslCtxBuilder = null;
			File keyCertChainFile = self.doAction("getKeyCertChainFile", actionContext);
			File keyFile = self.doAction("getKeyFile", actionContext);
			String keyPassword = self.doAction("getKeyPassword", actionContext);
			if(server) {
				if(keyPassword != null && !"".equals(keyPassword)) {
					sslCtxBuilder = SslContextBuilder.forServer(keyCertChainFile, keyFile, keyPassword);
				} else {
					sslCtxBuilder = SslContextBuilder.forServer(keyCertChainFile, keyFile);
				}
			} else {
				sslCtxBuilder = SslContextBuilder.forClient();
				if(keyCertChainFile != null && keyFile != null) {
					if(keyPassword != null && !"".equals(keyPassword)) {
						sslCtxBuilder.keyManager(keyCertChainFile, keyFile, keyPassword);
					}else {
						sslCtxBuilder.keyManager(keyCertChainFile, keyFile);
					}
				}
			}
			
			String[] protocols = self.doAction("getProtocols", actionContext);
			if(protocols != null) {
				sslCtxBuilder.protocols(protocols);
			}
			
			List<String> ciphers = self.doAction("getCiphers", actionContext);
			if(ciphers != null && ciphers.size() > 0) {
				sslCtxBuilder.ciphers(ciphers);
			}
			String clientAuth = self.doAction("getClientAuth", actionContext);
			if("NONE".equals(clientAuth)) {
				sslCtxBuilder.clientAuth(ClientAuth.NONE);
			}else if("OPTIONAL".equals(clientAuth)) {
				sslCtxBuilder.clientAuth(ClientAuth.OPTIONAL);
			}else if("REQUIRE".equals(clientAuth)) {
				sslCtxBuilder.clientAuth(ClientAuth.REQUIRE);
			}
			Boolean enableOcsp = self.doAction("getEnableOcsp", actionContext);
			if(enableOcsp != null) {
				sslCtxBuilder.enableOcsp(enableOcsp);
			}
			long sessionCacheSize = self.doAction("getSessionCacheSize", actionContext);
			if(sessionCacheSize > 0) {
				sslCtxBuilder.sessionCacheSize(sessionCacheSize);
			}
			long sessionTimeout = self.doAction("getSessionTimeout", actionContext);
			if(sessionTimeout > 0) {
				sslCtxBuilder.sessionTimeout(sessionTimeout);
			}
			String sslProvider = self.doAction("getSslProvider", actionContext);
			if("JDK".equals(sslProvider)) {
				sslCtxBuilder.sslProvider(SslProvider.JDK);
			} else if("OPENSSL".equals(sslProvider)) {
				sslCtxBuilder.sslProvider(SslProvider.OPENSSL);
			}else if("OPENSSL_REFCNT".equals(sslProvider)) {
				sslCtxBuilder.sslProvider(SslProvider.OPENSSL_REFCNT);
			}
			
			File trustCertCollectionFile = self.doAction("getTrustCertCollectionFile", actionContext);
			if(trustCertCollectionFile != null) {
				sslCtxBuilder.trustManager(trustCertCollectionFile);
			}
			
			self.doAction("init", actionContext, "builder", sslCtxBuilder);
			
			sslCtx = sslCtxBuilder.build();
			self.setCachedData(key, sslCtx);
		}
		
		Channel channel = actionContext.getObject("channel");
		if(server) {
			return sslCtx.newHandler(channel.alloc());
		}else {
			String host = self.doAction("getHost", actionContext);
			int port = self.doAction("getPort", actionContext);
			if(host != null && port > 0) {
				return sslCtx.newHandler(channel.alloc(), host, port);
			}else {
				return sslCtx.newHandler(channel.alloc());
			}
		}
	}
}
