/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.httpclient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.xmeta.Thing;

public class HttpClientManager {
	private static Map<String, HttpClientEntry> clients = new HashMap<String, HttpClientEntry>();
	
	private static HttpClient defaultHttpClient = null;
	
	static{
		 Thread th = new Thread(new Runnable(){
				public void run(){
					while(true){
						try{
							Thread.sleep(2000);							
							synchronized(clients){
								for(String key : clients.keySet()){
									HttpClientEntry entry = clients.get(key);
									
									if(entry != null){
										entry.httpClient.getConnectionManager().closeExpiredConnections();
										
										entry.httpClient.getConnectionManager().closeIdleConnections(30, TimeUnit.SECONDS);
									}
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}, "IdleConnectionMonitorThread");
		 th.setDaemon(true);
		 th.start();
	}
	
	public static synchronized HttpClient getDefaultHttpClient(){
		if(defaultHttpClient == null){
			PoolingClientConnectionManager cxMgr = new PoolingClientConnectionManager( SchemeRegistryFactory.createDefault());
			cxMgr.setMaxTotal(100);
			cxMgr.setDefaultMaxPerRoute(20);
			defaultHttpClient = new DefaultHttpClient(cxMgr);
		}
		
		return defaultHttpClient;
	}
	
	/**
	 * 获取给定事物对应的 HttpClient。
	 * 
	 * @param thing
	 * @return
	 */
	public static HttpClient getHttpClient(Thing thing){
	    //先从缓存中取 
		HttpClientEntry entry = clients.get(thing.getMetadata().getPath());
		if(entry != null && entry.lastModified == thing.getMetadata().getLastModified()){				
			return entry.httpClient;
		}
		
		if(entry != null){
			//事物已变更，重新初始化一个httpClient，先关闭原来的
			HttpClientUtils.closeQuietly(entry.httpClient);
		}
		
		DefaultHttpClient httpClient = null;
		synchronized(clients){
			//创建一个httpClient
			PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
			// Increase max total connection to 200
			cm.setMaxTotal(thing.getInt("maxConnection", 200));
			// Increase default max connection per route to 20
			cm.setDefaultMaxPerRoute(thing.getInt("maxPerRoute", 20));
			 
			httpClient = new DefaultHttpClient(cm);
			httpClient.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
	
			    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
			        // Honor 'keep-alive' header
			        HeaderElementIterator it = new BasicHeaderElementIterator(
			                response.headerIterator(HTTP.CONN_KEEP_ALIVE));
			        while (it.hasNext()) {
			            HeaderElement he = it.nextElement();
			            String param = he.getName(); 
			            String value = he.getValue();
			            if (value != null && param.equalsIgnoreCase("timeout")) {
			                try {
			                    return Long.parseLong(value) * 1000;
			                } catch(NumberFormatException ignore) {
			                }
			            }
			        }
			        
			        return 5 * 1000;
			    }
			    
			});
			
		    //代理
		    if(thing.getBoolean("proxy")){
		        String proxyUrl = thing.getString("proxyUrl");
		        int proxyPort = thing.getInt("proxyPort");
		        String userName = thing.getString("proxyUserName");
		        String password = thing.getString("proxyPassword");
		        
		        if(userName != null && userName != ""){
		            httpClient.getCredentialsProvider().setCredentials(
		                    new AuthScope(proxyUrl, proxyPort),
		                    new UsernamePasswordCredentials(userName, password));
		        }
		        
		        HttpHost proxy = new HttpHost(proxyUrl, proxyPort);
		        httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		    }
		    
		    //放入缓存
		    entry = new HttpClientEntry();
		    entry.lastModified = thing.getMetadata().getLastModified();
		    entry.httpClient = httpClient;
		    clients.put(thing.getMetadata().getPath(), entry);
		}
		
	    return httpClient;
	}
	
	public static void shutdown(Thing thing){
	   //先从缓存中取 
		HttpClientEntry entry = clients.get(thing.getMetadata().getPath());
		if(entry != null){
			HttpClientUtils.closeQuietly(entry.httpClient);
		}
	}
}