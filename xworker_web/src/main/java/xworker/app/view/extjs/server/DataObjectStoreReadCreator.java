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
package xworker.app.view.extjs.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ExceptionUtil;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.dataObject.PageInfo;
import xworker.dataObject.utils.JsonFormator;
import xworker.lang.executor.Executor;
import xworker.security.PermissionConstants;
import xworker.security.SecurityManager;
import xworker.util.UtilAction;

public class DataObjectStoreReadCreator {
	private static final String TAG = DataObjectStoreReadCreator.class.getName();
	
	public static int getInt(String value, int defaultValue){
        try{
            return Integer.parseInt(value);
        }catch(Exception e){
            return defaultValue;
        }
    }
	
    @SuppressWarnings("unchecked")
	public static void doAction(ActionContext actionContext) throws OgnlException, IOException{
        World world = World.getInstance();
        
        HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
        HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
        
        String dataObjectPath = request.getParameter("dataObjectPath");
        String conditionPath = request.getParameter("conditionPath");
        
        if(!SecurityManager.doCheck("WEB", PermissionConstants.XWORKER_DATAOBJECT, "read", dataObjectPath, actionContext)){
        	return;
        }
        
        //数据对象
        Thing dataObject = world.getThing(dataObjectPath);
        
        //查询定义
        Thing condition = world.getThing(conditionPath);
        
        //分页信息
        Map<String, Object> pageInfo = new HashMap<String, Object>();
		PageInfo pageInfo1 = new PageInfo(pageInfo);
        pageInfo.put("start", getInt(request.getParameter("start"), 0));
        pageInfo.put("limit", getInt(request.getParameter("limit"), 0));
        if(pageInfo1.getLimit() == 0){
            pageInfo.put("limit", getInt(request.getParameter("pageSize"), 0));
        }
        pageInfo.put("datas", new ArrayList<Object>());
        pageInfo.put("success", true);
        pageInfo.put("msg",  "");
        pageInfo.put("totalCount", 0);
        pageInfo.put("sort",  request.getParameter("sort"));
        pageInfo.put("dir", request.getParameter("dir"));
        
        //查询参数
        Object conditionData = null;
        if(condition != null){
            conditionData = condition.doAction("parseHttpRequest", actionContext);
        }
        
        actionContext.put("aggregateColumns", request.getParameter("aggregateColumns"));
        actionContext.put("groupColumns", request.getParameter("groupColumns"));
        
        //查询
        try{
	        Object datas = dataObject.doAction("query", actionContext, UtilMap.toMap(new Object[]{"conditionData",conditionData, "conditionConfig",condition, "pageInfo",pageInfo}));
	        
	        //输出json格式的数据
	        Thing jsonFactory = world.getThing("xworker.text.JsonDataFormat");
	        String code = "[]";
	        if(datas == pageInfo){
	            code = (String) jsonFactory.doAction("format", actionContext, UtilMap.toMap(new Object[]{"data", pageInfo.get("datas")}));
	        }else{
	            code = (String) jsonFactory.doAction("format", actionContext, UtilMap.toMap(new Object[]{"data", datas}));
	            if(datas instanceof DataObject){
	                code = code + "[" + code + "]";
	                pageInfo.put("totalCount", 1);	                
	            }else{
	                if(pageInfo1.getTotalCount() == 0){
	                	if(datas instanceof List){
	                        pageInfo.put("totalCount", ((List) datas).size());
	                    }else if(datas != null){
	                        pageInfo.put("totalCount",  OgnlUtil.getValue("length",datas));
	                    }else{
	                    	pageInfo.put("totalCount",  0);
	                    }
	                }
	            }    
	            pageInfo.put("limit", pageInfo.get("totalCount"));
	        }
	        
	        //log.info(code);
	        //输出到httpResponse
	        if(code.startsWith("{")){
	            code = "[" + code + "]";
	        }
	        
	        //是否动态生成store和gridColumn定义，如果pageInfo.dynamicDataObject存在
	        //println "store";
	        String storeCode = "";
	        String columnCode = "";
	        if(pageInfo.get("dynamicDataObject") != null){
	            Thing grid = new Thing("xworker.app.view.extjs.widgets.AppItems/@DataObjectGridPanel");
	            Thing dynamicDataObject = (Thing) pageInfo.get("dynamicDataObject");
	            grid.put("dataObject", dynamicDataObject.getMetadata().getPath());
	            
	            Thing store = (Thing) grid.doAction("createExtStore", actionContext, UtilMap.toMap(new Object[]{"dataObject", dynamicDataObject, "cmpType","grid"}));
	            //store作为生产reader的metadata用，不需要的子节点删除
	            Thing stores = store.getChilds().get(0);
	            for(int i=0; i<stores.getChilds().size(); i++){
	                Thing child = stores.getChilds().get(i);
	                if(!"fields".equals(child.getThingName())){
	                    stores.removeChild(child);
	                    i--;
	                }
	            }
	            storeCode = (String) store.doAction("toJavaScriptCode", actionContext);
	            storeCode = "\n    metaData: " + storeCode.substring(storeCode.indexOf("(") + 1, storeCode.lastIndexOf(")")) + ",";
	            
	            Thing columnModel = (Thing) grid.doAction("createExtGridColumns", actionContext, UtilMap.toMap(new Object[]{"grid",grid, "dataObject",dynamicDataObject}));
	            columnCode = "\n   columnModel: " + columnModel.doAction("toJavaScriptCode", actionContext) + ",";
	            //println columnCode;
	        }
	        
	        response.setContentType("text/plain; charset=utf-8");
	        String result = "{\n" +
	        		"success:" + pageInfo.get("success") + ",\n" +
	        		"totalCount:" + pageInfo.get("totalCount") + ",\n" +
	        		"msg:'" + JsonFormator.formatString((String) pageInfo.get("message")) + "',\n" +
	        		"pageSize:" + pageInfo.get("limit") + ",\n" +
	        		"limit:" + pageInfo.get("limit") + ",\n" + storeCode + columnCode + 
	        		"rows:" + code + "\n" +
	        		"}";
	        //println result;
	        //println request.getParameterMap();
	        //log.info("read " + result);
	        
	        response.getWriter().println(result);
        }catch(Exception e){
        	Throwable t = UtilAction.getCause(e);
        	Executor.error(TAG, "DataObjectStore read error， dataObjectPath=" + dataObjectPath, t);
        	t.printStackTrace();
        	
        	response.setContentType("text/plain; charset=utf-8");
	        String result = "{\n" +
	        		"success:false,\n" +
	        		"totalCount:0,\n" +
	        		"msg:'" + JsonFormator.formatString(ExceptionUtil.getRootMessage(e)) + "',\n" +
	        		"pageSize:0\n" +
	        		"}";
	        //println result;
	        //println request.getParameterMap();
	        response.getWriter().println(result);
        }
    }
}