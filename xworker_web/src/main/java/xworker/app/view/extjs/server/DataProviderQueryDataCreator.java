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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.security.PermissionConstants;
import xworker.security.SecurityManager;

public class DataProviderQueryDataCreator {
    public static void doAction(ActionContext actionContext) throws IOException{
    	World world = World.getInstance();
    	
    	HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
    	HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
    	
        //数据对象
        Thing dataObject = world.getThing(request.getParameter("objPath"));
        if(!SecurityManager.doCheck("WEB", PermissionConstants.XWORKER_DATAPROVIDER, "read", request.getParameter("objPath"), actionContext)){
        	return;
        }
        
        //查询定义
        Thing condition = world.getThing(request.getParameter("conditionPath"));
        
        //查询参数
        Object datas = dataObject.doAction("query", actionContext, UtilMap.toMap(new Object[]{"datas", actionContext.get("requestBean"), "condition",condition}));
        //输出json格式的数据
        Thing jsonFactory = world.getThing("xworker.text.JsonDataFormat");
        String code = (String) jsonFactory.doAction("getData", actionContext, UtilMap.toMap(new Object[]{"data", datas}));
        //输出到httpResponse
        response.setContentType("text/plain; charset=utf-8");
        response.getWriter().println(code);
    }

}