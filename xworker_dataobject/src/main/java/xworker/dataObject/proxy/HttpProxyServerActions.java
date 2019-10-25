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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;
import xworker.dataObject.utils.JacksonFormator;

public class HttpProxyServerActions {
private static Logger logger = LoggerFactory.getLogger(HttpProxyServerActions.class);

	public static void doAction(ActionContext actionContext){
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
		
		String actionName = request.getParameter("actionName");
		if("load".equals(actionName) || "create".equals(actionName) || "update".equals(actionName) || "delete".equals(actionName)){
			doActionByName(actionName, request, response, actionContext);
		}else if("query".equals(actionName)){
			doQuery(request, response, actionContext);
		}
	}

		
	@SuppressWarnings("unchecked")
	public static void doQuery(HttpServletRequest request, HttpServletResponse response, ActionContext actionContext){
		try{
			//参数
			Map<String, Object> pageInfo = (Map<String, Object>) JacksonFormator.parseObject(request, "pageInfo");
			Map<String, Object> conditionData = (Map<String, Object>) JacksonFormator.parseObject(request, "conditionData");
			Map<String, Object> conditionConfigMap = (Map<String, Object>) JacksonFormator.parseObject(request, "conditionConfig");
			Thing conditionConfig = null;
			if(conditionConfigMap != null){
				conditionConfig = DataObjectUtil.mapToConditionThing(conditionConfigMap);
			}
			
			if(pageInfo == null){
				pageInfo = new HashMap<String, Object>();
				pageInfo.put("start", 0);
				pageInfo.put("limit", 0);
			}
			
			String dataObjectPath = request.getParameter("dataObject");
			Thing dataObject = World.getInstance().getThing(dataObjectPath);
			
			Map<String, Object> result = new HashMap<String, Object>();
			if(dataObject == null){
				result.put("success", false);
				result.put("msg", "dataObject is null, dataObject path=" + dataObjectPath);			
			}else{
				Object datas = dataObject.doAction("query", actionContext, UtilMap.toMap(new Object[]{"conditionData",conditionData, "conditionConfig",conditionConfig, "pageInfo",pageInfo}));
				if(datas != pageInfo){
					pageInfo.put("datas", datas);
				}
				
				result.put("success", true);
				result.put("data", pageInfo);
			}
			
			response.setContentType("text/plain; charset=utf-8");
			
			String json = JacksonFormator.formatObject(result);
			response.getWriter().println(json);
		}catch(Exception e){
			logger.error("DataObject remote query error", e);
			response.setContentType("text/plain; charset=utf-8");
			try {
				response.getWriter().println("{\"success\":false, \"msg\":\"" + e.getMessage() + "\"}");
			} catch (IOException e1) {
				logger.error("DataObject remote error", e1);
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static void doActionByName(String actionName, HttpServletRequest request, HttpServletResponse response, ActionContext actionContext){
		//返回结果
		Map<String, Object> result = new HashMap<String, Object>();
		
		//参数
		String theData = request.getParameter("theData");
		String dataObjectPath = request.getParameter("dataObject");
		Thing dataObject = World.getInstance().getThing(dataObjectPath);
		if(theData == null){
			result.put("success", false);
			result.put("msg", "parameter theData is null");
		}else if(dataObject == null){
			result.put("success", false);
			result.put("msg", "dataObject is null, dataObject path=" + dataObjectPath);			
		}else{
			try{
				//转化theData为数据对象
				
				Map<String, Object> theDataMap = (Map<String, Object>) JacksonFormator.parseObject(theData);
				DataObject dobject = new DataObject(dataObject);
				dobject.putAll(theDataMap);
				
				//执行数据对象的load方法
				Object obj = dobject.doAction(actionName, actionContext);
				if(obj == null){
					result.put("success", false);
					result.put("msg", "dataobject load method not return a value, dataObject=" + dataObject);
				}else{
					result.put("success", true);
					result.put("data", obj);
				}
			}catch(Exception e){
				result.put("success", false);
				result.put("msg", e.getMessage());
				logger.error("DataObject remote load error", e);
			}
		}
		
		try{
			response.setContentType("text/plain; charset=utf-8");
			String json = JacksonFormator.formatObject(result);
			response.getWriter().println(json);
		}catch(Exception e){
			logger.error("DataObject remote: format json string error", e);
			response.setContentType("text/plain; charset=utf-8");
			try {
				response.getWriter().println("{\"success\":false, \"msg\":\"format json string error\"}");
			} catch (IOException e1) {
				logger.error("DataObject remote: format json string error", e1);
			}
		}		
	}
}