package xworker.io.netty.handlers.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.handler.ssl.SslHandler;

public class JavaSSLHandler {
	public static Object createClientHandler(ActionContext actionContext) throws Exception {
		return createHandler(actionContext, false);
	}
	
	public static Object createServerHandler(ActionContext actionContext) throws Exception {
		return createHandler(actionContext, true);
	}
	
	public static Object createHandler(ActionContext actionContext, boolean server) throws Exception {
		Thing self = actionContext.getObject("self");
		
		String key = "__sslCTX__";
		SSLContext sslCtx = self.getCachedData(key);		
		boolean needClientAuth = self.doAction("isNeedClientAuth", actionContext);
		if(sslCtx == null) {
			String keyStoreType = self.doAction("getKeyStoreType", actionContext);
			File keyStore = self.doAction("getKeyStore", actionContext);
			String keyStorePassword = self.doAction("getKeyStorePassword", actionContext);
			String keyManagerAlgorithm  = self.doAction("getKeyManagerAlgorithm", actionContext);			
			String protocol = self.doAction("getProtocol", actionContext);
			KeyManagerFactory kmf = null;
			FileInputStream in = null;
			if(server || needClientAuth) {
				//服务器是一定要加载key，客户端只有在需要客户端验证是才加载
				KeyStore ks = KeyStore.getInstance(keyStoreType);
				in = new FileInputStream(keyStore);
				try {
					ks.load(in, keyStorePassword.toCharArray());
					
					kmf = KeyManagerFactory.getInstance(keyManagerAlgorithm);
					kmf.init(ks, keyStorePassword.toCharArray());
				}finally {
					in.close();
				}
			}
			
			//信任库
			TrustManagerFactory tf = null;
			if(((server && needClientAuth) || (!server))  && keyStore!= null && keyStore.exists()) {	
				in = new FileInputStream(keyStore);
				try {
					KeyStore tks = KeyStore.getInstance(keyStoreType);
					tks.load(in, keyStorePassword.toCharArray());
					
					tf = TrustManagerFactory.getInstance(keyManagerAlgorithm);
					tf.init(tks);;
				}finally {
					in.close();
				}					
			}
			
			sslCtx = SSLContext.getInstance(protocol);
			//初始化此上下文
			sslCtx.init(kmf != null ? kmf.getKeyManagers() : null, tf != null ? tf.getTrustManagers() : null, null);
			self.setCachedData(key, sslCtx);			
		}
		
		SSLEngine engine = sslCtx.createSSLEngine();
		engine.setUseClientMode(!server);
		if(needClientAuth && server) {
			engine.setNeedClientAuth(true);
		}
		
		return new SslHandler(engine);
	}
}
