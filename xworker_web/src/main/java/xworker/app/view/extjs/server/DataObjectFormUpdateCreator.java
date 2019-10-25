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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
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

public class DataObjectFormUpdateCreator {
	private static Logger log = LoggerFactory.getLogger(DataObjectFormUpdateCreator.class);
	
    @SuppressWarnings("deprecation")
	public static void doAction(ActionContext actionContext) throws IOException{
    	World world = World.getInstance();
        
        //数据对象定义
    	HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
    	String dataObjectPath = request.getParameter("dataObjectPath");
    	if(!SecurityManager.doCheck("WEB", PermissionConstants.XWORKER_DATAOBJECT, "update", dataObjectPath, actionContext)){
        	return;
        }
    	
        Thing dataObjectConfig = world.getThing(dataObjectPath);
        
        String record = "{}";
        Map<String, Object> result = UtilMap.toMap(new Object[]{"success","true", "msg",""});
        actionContext.peek().put("result", result);
        if(dataObjectConfig == null){
            result.put("success", "false");
            result.put("msg",  "数据对象定义不存在，dataObject=" + request.getParameter("dataObjectPath"));
        }else{
            try{
                DataObject theData = new DataObject(dataObjectConfig);
                theData.doAction("parseHttpRequestData", actionContext);
                theData.doAction("update", actionContext);
                
                Thing jsonFactory = world.getThing("xworker.text.JsonDataFormat");
                record = (String) jsonFactory.doAction("format", actionContext, UtilMap.toMap(new Object[]{"data",theData}));
                if(result.get("msg") == null || "".equals(result.get("msg"))){
                    result.put("msg", "数据更新成功!");
                }
            }catch(Exception e){
                log.error("数据更新失败", e);
                result.put("success", "false");
                result.put("msg", JsonFormator.formatString(ExceptionUtil.getRootMessage(e)));
            }
        }
        HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
        if(ServletFileUpload.isMultipartContent(request)){
        	//extjs提交文件是使用iframe，contentType必须是text/html
        	response.setContentType("text/html; charset=utf-8");
        }else{
        	response.setContentType("text/plain; charset=utf-8");
        }
        String code = "{\n" + 
        	"\"success\":" + result.get("success") + ",\n" + 
        	"\"msg\":\"" + JsonFormator.formatString((String) result.get("msg")) + "\",\n" + 
        	"\"data\":" + record + "\n" + 
        	"}";
        response.getWriter().println(code);
    }

}