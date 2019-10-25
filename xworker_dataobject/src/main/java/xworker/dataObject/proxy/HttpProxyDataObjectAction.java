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
package xworker.dataObject.proxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectConstants;
import xworker.dataObject.DataObjectException;
import xworker.dataObject.PageInfo;
import xworker.dataObject.utils.JacksonFormator;

public class HttpProxyDataObjectAction { 
	/**
	 * 装载数据对象。
	 * 
	 * @param actionContext
	 * @throws OgnlException 
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static Object doLoad(ActionContext actionContext) throws Exception{
		return doAction("load", actionContext);
	}
	
	public static Object doCreate(ActionContext actionContext) throws Exception{
		return doAction("create", actionContext);
	}
	
	public static Object doUpdate(ActionContext actionContext) throws Exception{
		return doAction("update", actionContext);
	}
	
	public static Object doDelete(ActionContext actionContext) throws Exception{
		return doAction("delete", actionContext);
	}
	
	@SuppressWarnings("unchecked")
	public static Object doQuery(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		
		//使用self获取url和httpClient，这样可以使用重载过的相应的方法
		String url = (String) self.doAction("getUrl", actionContext);
		HttpClient client = (HttpClient) self.doAction("getHttpClient", actionContext);
		if(url == null){
			throw new DataObjectException("Url is null, thing=" + self);
		}
		if(client == null){
			throw new DataObjectException("HttpClient is null, thing=" + self);
		}
				
		//把数据提交到服务器
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("dataObject", self.getString("dataObject")));
		formparams.add(new BasicNameValuePair("actionName", "query"));
		
		Object pageInfo = actionContext.get(DataObjectConstants.PAGEINFO_PAGEINFO);
		if(pageInfo != null){
			PageInfo tpageInfo = PageInfo.getPageInfo(actionContext);			
			if(tpageInfo != null){
				//清楚datas如果有，否则会提交到服务器，没有必要
				if(tpageInfo.getDatas() != null){
					tpageInfo.setDatas(null);
				}
				if(tpageInfo.getLimit() == 0){
					tpageInfo.setLimit(self.getInt(DataObjectConstants.PAGEINFO_PAGESIZE));
				}
				if(tpageInfo.getDir() == null){
					tpageInfo.setDir(self.getString(DataObjectConstants.SORT_DIR));
				}
				if(tpageInfo.getSort() == null || DataObjectConstants.PAGEINFO_SORT_EXTEND.equals(tpageInfo.getSort())){
					tpageInfo.setSort(self.getString(DataObjectConstants.SORT_FIELD));
				}
			}
			formparams.add(new BasicNameValuePair(DataObjectConstants.PAGEINFO_PAGEINFO, JacksonFormator.formatObject(pageInfo)));
		}
		Object conditionData = actionContext.get(DataObjectConstants.CONDITION_DATA);
		if(conditionData != null){
			formparams.add(new BasicNameValuePair(DataObjectConstants.CONDITION_DATA,  JacksonFormator.formatObject(conditionData)));
		}
		Object conditionConfig = actionContext.get(DataObjectConstants.CONDITION_CONFIG);
		if(conditionConfig != null){
			formparams.add(new BasicNameValuePair(DataObjectConstants.CONDITION_CONFIG, JacksonFormator.formatObject(conditionConfig)));		
		}
		
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		httpPost.setEntity(entity);
		HttpResponse response = client.execute(httpPost);
		try{
			if(response.getStatusLine().getStatusCode() == 200){
				String body = EntityUtils.toString(response.getEntity());
				Map<String, Object> result = (Map<String, Object>) JacksonFormator.parseObject(body);			
				if((Boolean) result.get("success") == true){
					Object data = result.get("data");
					if(data == null){
						data = result.get("rows");
					}
					if(data instanceof Map){
						//复制远程返回的pageInfo到本地的pageInfo
						Map<String, Object> page = (Map<String, Object>) data;
						PageInfo srcPageInfo = PageInfo.getPageInfo(actionContext);
						PageInfo rpageInfo = PageInfo.getPageInfo(data);
						srcPageInfo.setDatas(listToDataObjectList((List<Map<String, Object>>) page.get("datas"), self.getMetadata().getPath()));
						srcPageInfo.setDir(rpageInfo.getDir());
						srcPageInfo.setLimit(rpageInfo.getLimit());
						srcPageInfo.setMsg(rpageInfo.getMsg());
						srcPageInfo.setSort(rpageInfo.getSort());
						srcPageInfo.setStart(rpageInfo.getStart());
						srcPageInfo.setSuccess(rpageInfo.isSuccess());
						srcPageInfo.setTotalCount(rpageInfo.getTotalCount());
						return srcPageInfo.getDatas();
					}else{
						return listToDataObjectList((List<Map<String, Object>>) data, self.getMetadata().getPath());
					}
				}else{
					throw new DataObjectException((String) result.get("msg"));
				}
			}
		}finally{
			EntityUtils.consume(response.getEntity());
		}
		
		return null;
	}
	
	public static List<DataObject> listToDataObjectList(List<Map<String, Object>> datas, String dataObject){
		if(datas == null){
			return Collections.emptyList();
		}else{
			List<DataObject> dataObjectList = new ArrayList<DataObject>();
			for(Map<String, Object> data : datas){
				DataObject obj = new DataObject(dataObject);
				obj.setInited(false);
				obj.putAll(data);
				obj.setInited(true);
				dataObjectList.add(obj);
			}
			
			return dataObjectList;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static Object doAction(String actionName, ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		
		//使用self获取url和httpClient，这样可以使用重载过的相应的方法
		String url = (String) self.doAction("getUrl", actionContext);
		HttpClient client = (HttpClient) self.doAction("getHttpClient", actionContext);
		if(url == null){
			throw new DataObjectException("Url is null, thing=" + self);
		}
		if(client == null){
			throw new DataObjectException("HttpClient is null, thing=" + self);
		}
		
		//把数据对象转化为json数据
		DataObject theData = (DataObject) actionContext.get("theData");
		if("update".equals(actionName)){
			//如果是更新，则只需要脏数据和id
			DataObject newData = new DataObject(theData.getMetadata().getDescriptor());
			for(Object[] key : theData.getKeyAndDatas()){
				Thing attr = (Thing) key[0];
				newData.put(attr.getString("name"), key[1]);
			}
			for(String dirty : theData.getMetadata().getDirtyFields()){
				newData.put(dirty, theData.get(dirty));
			}
			
			theData = newData;
		}
		
		String json = JacksonFormator.formatObject(theData);
		
		//把数据提交到服务器
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("dataObject", self.getString("dataObject")));
		formparams.add(new BasicNameValuePair("actionName", actionName));
		formparams.add(new BasicNameValuePair(DataObjectConstants.THEDATA, json));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		httpPost.setEntity(entity);
		
		HttpResponse response = client.execute(httpPost);
		try{
			if(response.getStatusLine().getStatusCode() == 200){
				String body = EntityUtils.toString(response.getEntity());
				Map<String, Object> result = (Map<String, Object>) JacksonFormator.parseObject(body);		
				if((Boolean) result.get("success") == true){
					if("update".equals(actionName) || "delete".equals(actionName)){
						Boolean r = (Boolean) result.get("data");
						if("update".equals(actionName) && r){
							theData.getMetadata().cleanDirty();
						}
						
						return r;
					}
					
					Map<String, Object> data = (Map<String, Object>) result.get("data"); 
					theData.putAll(data);
					return theData;
				}else{
					throw new DataObjectException((String) result.get("msg"));
				}
			}
		}finally{
			EntityUtils.consume(response.getEntity());
		}
		
		return null;
	}
	
	/**
	 * 获取访问服务器的URL。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String getUrl(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Thing httpProxyConfiger = World.getInstance().getThing(self.getString("httpProxyConfiger"));
		if(httpProxyConfiger == null){
			throw new DataObjectException("Please set up httpProxyConfiger, thing=" + self);
		}
		
		return httpProxyConfiger.doAction("getBaseUrl", actionContext) + self.getString("url");
	}
	
	/**
	 * 获取HttpClient。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static HttpClient getHttpClient(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing httpProxyConfiger = World.getInstance().getThing(self.getString("httpProxyConfiger"));
		if(httpProxyConfiger == null){
			throw new DataObjectException("Please set up httpProxyConfiger, thing=" + self);
		}
		
		return (HttpClient) httpProxyConfiger.doAction("getHttpClient", actionContext);
	}
}