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
package xworker.app.model.tree.http;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import xworker.dataObject.utils.JsonFormator;

public class HttpExtjsJsonTreeModelActions {
	private static Logger logger = LoggerFactory.getLogger(HttpExtjsJsonTreeModelActions.class);
	
	/**
	 * 向服务器抓取数据。
	 * 
	 * @param url
	 * @param self
	 * @param actionContext
	 * @return
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public static Object getTreeDatas(String prefixUrl, String url, Thing self, ActionContext actionContext) throws IOException{
		HttpClient httpClient = getHttpClient(self, actionContext);
		if(httpClient != null){
			HttpGet httpGet = new HttpGet(url);
			HttpEntity entity = null;
			try{
				 HttpResponse response = httpClient.execute(httpGet);
				 entity = response.getEntity();
				 String content = EntityUtils.toString(entity);
				 Object datas = JsonFormator.parse(content, actionContext);
				 URI uri = null;
				 if(prefixUrl != null && !"".equals(prefixUrl)){
					 //基础根首先选用prefixUrl
					 uri = new URI(prefixUrl);
				 }else{
					 uri = new URI(url);
				 }
				 
				 if(datas instanceof List){
					 checkImageUrl((List<Map<String, Object>>) datas, uri);
				 }else{
					 checkImageUrl((Map<String, Object>) datas, uri);
				 }
				 
				 return datas;
			}catch(Exception e){
				logger.error("Get tree content error", e);
			}finally{
				if(entity != null){
					EntityUtils.consume(entity);
				}
			}
		}
		
		return null;
	}
	
	public static void checkImageUrl(List<Map<String, Object>> nodes, URI root){
		for(Map<String, Object> node : nodes){
			checkImageUrl(node, root);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void checkImageUrl(Map<String, Object> node, URI root){
		String iconPath = (String) node.get("icon");
		if(iconPath != null && !"".equals(iconPath)){
			URI iconUri = root.resolve(iconPath);
			node.put("icon", iconUri.toString());
		}
		
		List<Map<String, Object>> childs = (List<Map<String, Object>>) node.get("childs");
		if(childs != null){
			checkImageUrl(childs, root);
		}
	}
	
	public static HttpClient getHttpClient(Thing self, ActionContext actionContext){
		HttpClient client = null;
		String httpClientPath = self.getString("httpClient");
		if(httpClientPath != null && !"".equals(httpClientPath)){
			Thing httpClientThing = World.getInstance().getThing(httpClientPath);
			if(httpClientThing != null){
				client = (HttpClient) httpClientThing.doAction("getHttpClient", actionContext);
			}
			if(client == null){
				logger.info("Cann't get httpclient, path=" + httpClientPath + ",thing=" + self);
			}
		}else{
			Thing httpClientThing = self.getThing("HttpClient@0");
			if(httpClientThing != null){
				client = (HttpClient) httpClientThing.doAction("getHttpClient", actionContext);
			}else{
				World world = World.getInstance();
				client = (HttpClient) world.getData(self.getMetadata().getPath());
				if(client == null){
					client = new DefaultHttpClient();
					world.setData(self.getMetadata().getPath(), client);
				}
			}
		}
		
		return client;
	}
	
	public static String getUrl(Thing self, ActionContext actionContext){
		String httpUrlPrefix = self.getString("httpUrlPrefix");
		String httpUrl = self.getString("httpUrl");
		String url = null;
		if(httpUrlPrefix != null && !"".equals(httpUrlPrefix)){
			url = UtilString.getString(httpUrlPrefix, actionContext);			
		}
		
		if(url != null){
			return url + httpUrl;
		}else{
			return httpUrl;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Object getRoot(ActionContext actionContext) throws IOException{
    	Thing self = (Thing) actionContext.get("self");
    	
    	String url = getUrl(self, actionContext);
    	String rootId = self.getString("rootNodeId");
    	Object roots = getTreeDatas(self.getString("httpUrlPrefix"), getAddParamUrl(url, "id=" + rootId), self, actionContext);
    	if(roots instanceof List){
    		Map<String, Object> root = new HashMap<String, Object>();
    		root.put("childs", roots);
    		return root;
    	}else{
    		return roots;
    	}
    }

	public static String getAddParamUrl(String url, String param){
		if(url.indexOf("?") == -1){
			url = url + "?" + param;
		}else{
			url = url + "&" + param;
		}
		
		return url;
	}
	
    public static Object getChilds(ActionContext actionContext) throws IOException{
    	Thing self = (Thing) actionContext.get("self");
    	
    	String url = getUrl(self, actionContext);
    	String id = (String) actionContext.get("id");
        if(id == null || "".equals(id)){            
        }else{
        	url = getAddParamUrl(url, "id=" + URLEncoder.encode(id, "utf-8"));
        }        
        return getTreeDatas(self.getString("httpUrlPrefix"), url, self, actionContext);
    }
}