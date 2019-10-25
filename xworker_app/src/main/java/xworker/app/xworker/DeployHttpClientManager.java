package xworker.app.xworker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeployHttpClientManager {
private static Logger logger = LoggerFactory.getLogger(DeployHttpClientManager.class);
	
	static String rootUrl = XWorkerDeployConfig.getUrl();
	//static String rootUrl = "http://www.xworker.org/";
	private static HttpClient defaultHttpClient = null;
	
	@SuppressWarnings("deprecation")
	public static synchronized HttpClient getDefaultHttpClient(){
		if(defaultHttpClient == null){
			PoolingClientConnectionManager cxMgr = new PoolingClientConnectionManager( SchemeRegistryFactory.createDefault());
			cxMgr.setMaxTotal(100);
			cxMgr.setDefaultMaxPerRoute(20);
			defaultHttpClient = new DefaultHttpClient(cxMgr);
		}
		
		return defaultHttpClient;
	}
	
	public static byte[] getThing(String thingPath) throws ClientProtocolException, IOException{
		if(thingPath.startsWith("_local")){
			return null;
		}
		
		String url = rootUrl + "do?sc=xworker.app.xworker.rest.Thing&thing=" + thingPath;
		HttpClient httpClient = getDefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse httpResponse = null;
		try{			
			httpResponse = httpClient.execute(get);
			if(httpResponse.getStatusLine().getStatusCode() == 200){
				HttpEntity entity = httpResponse.getEntity();
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				byte[] bytes = new byte[2048];
				int length = -1;
				while((length = entity.getContent().read(bytes)) != -1){
					bout.write(bytes, 0, length);
				}
				
				return bout.toByteArray();
			}else{
				logger.warn("GetThing statusCode is " + httpResponse.getStatusLine().getStatusCode() + ",url=" + url);
				return null;
			}

		}catch(Exception e){
			logger.error("GetThing error,url=" + url, e);
			return null;
		}finally{
			if(httpResponse != null){
				EntityUtils.consume(httpResponse.getEntity());
			}
		}
	}
	
	public static File getClass(String className) throws ClientProtocolException, IOException{
		String url = rootUrl + "do?sc=xworker.app.xworker.rest.Jar&class=" + className;
		HttpClient httpClient = getDefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse httpResponse = null;
		try{			
			httpResponse = httpClient.execute(get);
			if(httpResponse.getStatusLine().getStatusCode() == 200){
				HttpEntity entity = httpResponse.getEntity();
				
				String fileName = httpResponse.getHeaders("fileName")[0].getValue();
				File file = null;
				if(fileName.toLowerCase().endsWith(".class")){
					file = new File("./xworker/classes/" + className.replace('.', '/') + ".class");
				}else{
					file = new File("./xworker/lib/" + fileName);
				}
				if(!file.exists()){
					file.getParentFile().mkdirs();
				}
				
				byte[] bytes = new byte[4096];
				int length = -1;
				FileOutputStream fout = new FileOutputStream(file);
				while((length = entity.getContent().read(bytes)) != -1){
					fout.write(bytes, 0, length);
				}
				
				fout.close();
				
				return file;
			}else{
				logger.warn("GetClass statusCode is " + httpResponse.getStatusLine().getStatusCode() + ",url=" + url);
				return null;
			}

		}finally{
			if(httpResponse != null){
				EntityUtils.consume(httpResponse.getEntity());
			}
		}
	}
	
	public static URL getResource(String path) throws ClientProtocolException, IOException{
		String url = rootUrl + "do?sc=xworker.app.xworker.rest.Resource&path=" + URLEncoder.encode(path, "utf-8");
		HttpClient httpClient = getDefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse httpResponse = null;
		try{			
			httpResponse = httpClient.execute(get);
			if(httpResponse.getStatusLine().getStatusCode() == 200){
				HttpEntity entity = httpResponse.getEntity();
				
				File file = new File("./xworker/classes/" + path);
				if(!file.exists()){
					file.getParentFile().mkdirs();
				}
				
				byte[] bytes = new byte[4096];
				int length = -1;
				FileOutputStream fout = new FileOutputStream(file);
				while((length = entity.getContent().read(bytes)) != -1){
					fout.write(bytes, 0, length);
				}
				
				fout.close();
				
				return file.toURI().toURL();
			}else{
				logger.warn("GetResource statusCode is " + httpResponse.getStatusLine().getStatusCode() + ",url=" + url);
				return null;
			}

		}finally{
			if(httpResponse != null){
				EntityUtils.consume(httpResponse.getEntity());
			}
		}
	}
}
