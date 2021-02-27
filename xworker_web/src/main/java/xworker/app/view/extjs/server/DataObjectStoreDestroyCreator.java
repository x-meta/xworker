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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ExceptionUtil;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;
import xworker.dataObject.utils.JsonFormator;
import xworker.lang.executor.Executor;
import xworker.security.PermissionConstants;
import xworker.security.SecurityManager;

public class DataObjectStoreDestroyCreator {
	private static final String TAG = DataObjectStoreDestroyCreator.class.getName();
	
    @SuppressWarnings("unchecked")
	public static void doAction(ActionContext actionContext) throws IOException{
    	World world = World.getInstance();
        
        //数据对象
    	HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
    	String dataObjectPath = request.getParameter("dataObjectPath");
    	if(!SecurityManager.doCheck("WEB", PermissionConstants.XWORKER_DATAOBJECT, "delete", dataObjectPath, actionContext)){
        	return;
        }
    	
    	Thing dataObjectCfg = world.getThing(dataObjectPath);
    	
    	 String jsonRecord = "{}";
         Map<String, Object> result = UtilMap.toMap(new Object[]{"success","false", "msg",""});
         actionContext.peek().put("result", result);
         if(dataObjectCfg == null){
             result.put("success", "false");
             result.put("msg",  "数据对象定义不存在，dataObject=" + request.getParameter("dataObjectPath"));
         }else{
        	 String value = request.getParameter("rows");
             Executor.info(TAG, value);
             if( value != null && !"".equals(value)){        
                 try{
                	 if(value.startsWith("[") || value.startsWith("{")){
                    	 //JSON数据
	                	 Object dataObject = dataObjectCfg.doAction("parseJsonData", actionContext, UtilMap.toMap(new Object[]{"jsonText", value}));
	                	 if(dataObject == null){
	                		 result.put("msg", "没有数据需要删除");
	                	 }else{
		                     if(dataObject instanceof DataObject){
		                    	 DataObject dobj = (DataObject) dataObject;
		                         dobj.doAction("delete", actionContext);
		                     }else{
		                    	 for(DataObject dobj : ((Iterable<DataObject>) dataObject)){
		                    		 dobj.doAction("delete", actionContext);
		                    	 }
		                     }
		                     Thing jsonFactory = world.getThing("xworker.text.JsonDataFormat");
		                     jsonRecord = (String) jsonFactory.doAction("format", actionContext, UtilMap.toMap(new Object[]{"data", dataObject}));
		                     
		                     result.put("success", "true");
		                     if(result.get("msg") == null){
		                         result.put("msg", "删除数据成功");
		                     }
	                     }
                	 }else{
                		 //只是ID的字符串，？应该是Json数据，为什么编程了id的值了呢？
                		 String[] ids = value.split("[,]");
                		 if(ids.length == 1){
                			 DataObject obj = new DataObject(dataObjectCfg);
                			 obj.put(obj.getMetadata().getKeys()[0].getString("name"), ids[0]);
                			 obj.doAction("delete", actionContext);
                		 }else{
                			 List<DataObject> objs = new ArrayList<DataObject>();
                			 for(int i=0; i<ids.length; i++){
                				 DataObject obj = new DataObject(dataObjectCfg);
                    			 obj.put(obj.getMetadata().getKeys()[0].getString("name"), ids[0]);
                    			 objs.add(obj);
                			 }
                			 
                			 dataObjectCfg.doAction("deleteBatch", actionContext, UtilMap.toMap(new Object[]{"datas", objs}));
                		 }
                		 
                		 result.put("success", "true");
                         result.put("msg", "删除数据成功");
                	 }
                 }catch(Exception e){
                     result.put("success", "false");
                     result.put("msg", ExceptionUtil.getRootMessage(e));
                     Executor.error(TAG, "Read and update dataobject error", e);
                 }
             }else{
                 result.put("msg", "没有数据需要删除");
             }
         }
        
        if(jsonRecord == null || jsonRecord == ""){
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