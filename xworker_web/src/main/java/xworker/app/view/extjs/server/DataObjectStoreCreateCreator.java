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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ExceptionUtil;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;
import xworker.dataObject.utils.JsonFormator;
import xworker.security.PermissionConstants;
import xworker.security.SecurityManager;

public class DataObjectStoreCreateCreator {
	private static Logger log = LoggerFactory.getLogger(DataObjectStoreCreateCreator.class);
	
    @SuppressWarnings("unchecked")
	public static void doAction(ActionContext actionContext) throws IOException{
    	World world = World.getInstance();
        
        //数据对象
    	HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
    	String dataObjectPath = request.getParameter("dataObjectPath");
    	if(!SecurityManager.doCheck("WEB", PermissionConstants.XWORKER_DATAOBJECT, "create", dataObjectPath, actionContext)){
        	return;
        }
    	
    	Thing dataObjectCfg = world.getThing(dataObjectPath);
    	
    	 String jsonRecord = "{}";
         Map<String, Object> result = UtilMap.toMap(new Object[]{"success","true", "msg",""});
         actionContext.peek().put("result", result);
         if(dataObjectCfg == null){
             result.put("success", "false");
             result.put("msg",  "数据对象定义不存在，dataObject=" + request.getParameter("dataObjectPath"));
         }else{
        	 String value = request.getParameter("rows");
             //log.info(value);
             if(value != "" && value != null){        
                 try{        
                	 String key = null;
                     List<Thing> keys = (List<Thing>) dataObjectCfg.doAction("getKeyAttributes", actionContext);
                     if(keys != null && keys.size() > 0){
                         key = keys.get(0).getString("name");
                     }
                     Object dataObject = (Object) dataObjectCfg.doAction("parseJsonData", actionContext, UtilMap.toMap(new Object[]{"jsonText", value}));
                     log.info("" + dataObject);
                     if(dataObject instanceof DataObject){
                    	 DataObject dobj = (DataObject) dataObject;
                         if(key != null){
                             dobj.put("__oldId", dobj.get(key));
                         }
                         dobj.doAction("create", actionContext);
                     }else{
                    	 for(DataObject dobj : (Iterable<DataObject>) dataObject){
	                         if(key != null){                             
	                             dobj.put("__oldId", dobj.get(key));                            
	                         }
	                         dobj.doAction("create", actionContext);
                    	 }
                     }
                     Thing jsonFactory = world.getThing("xworker.text.JsonDataFormat");
                     jsonRecord = (String) jsonFactory.doAction("format", actionContext, UtilMap.toMap(new Object[]{"data", dataObject}));
                     if(result.get("msg") == null){
                         result.put("msg", "创建数据成功");
                     }
                 }catch(Exception e){
                     result.put("success", "false");
                     result.put("msg", ExceptionUtil.getRootMessage(e));
                     log.error("Read and update dataobject error", e);
                 }
             }else{
                 result.put("msg", "没有数据需要更新");
             }
         }
        
        if(jsonRecord == null || "".equals(jsonRecord)){
            jsonRecord = "[]";
        }
        if(jsonRecord.startsWith("{")){
            jsonRecord = "[" + jsonRecord + "]";
        }
        
        HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
        response.setContentType("text/plain; charset=utf-8");
        String code = "{\n" + 
        	"success:" + result.get("success") + ",\n" + 
        	"msg:'" + JsonFormator.formatString((String) result.get("msg")) + "',\n" + 
        	"rows:" + jsonRecord + "\n" + 
        	"}";
        response.getWriter().println(code);
        
    }

}