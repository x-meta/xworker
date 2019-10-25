package xworker.util.monitor.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class HttpRequest {
	/**
	 * 通过GET请求一个URL，使用默认的HttpClient，xworker.httpclient.DefaultHttpClient。
	 * 
	 * @param url
	 * @return
	 */
	public static HttpResult request(String url){
		Thing httpClient = World.getInstance().getThing("xworker.httpclient.DefaultHttpClient");
		return request(url, httpClient);
	}
	
	/**
	 * 通过GET请求一个URL。
	 * 
	 * @param url
	 * @param httpClient
	 * @return
	 */
	public static HttpResult request(String url, Thing httpClient){
		HttpResult result = new HttpResult();
		long start = System.currentTimeMillis();
		try{
			HttpClient client = (HttpClient) httpClient.doAction("getHttpClient");
			
			HttpGet httpGet = new HttpGet(url);		
			HttpResponse httpResponse = null;	
			
			try{		
				httpResponse = client.execute(httpGet);
				result.statusCode = httpResponse.getStatusLine().getStatusCode();				
				result.content = EntityUtils.toString(httpResponse.getEntity());
			}catch(Exception e){
				result.content = e.getMessage();
			}finally{
				if(httpResponse != null){
					try {
						EntityUtils.consume(httpResponse.getEntity());
					} catch (IOException e) {					
					}
				}
			}
		}catch(Exception e){
			result.time = System.currentTimeMillis() - start;
			result.content = e.getMessage();			
		}
		
		return result;
	}
	
	public static HttpResult doFunction(ActionContext actionContext){
		String url = (String) actionContext.get("url");
		return request(url);
	}
}
