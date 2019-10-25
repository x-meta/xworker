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

import xworker.dataObject.DataObject;
import xworker.security.PermissionConstants;
import xworker.security.SecurityManager;

public class DataProviderLoadDataCreator {
    public static void doAction(ActionContext actionContext) throws IOException{
    	World world = World.getInstance();
        
    	HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
    	HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
    	
    	//构造和装载数据对象
        String dataObjectPath = request.getParameter("objPath");
        if(!SecurityManager.doCheck("WEB", PermissionConstants.XWORKER_DATAPROVIDER, "read", dataObjectPath, actionContext)){
        	return;
        }
        
        Thing dataObject = world.getThing(dataObjectPath);
        DataObject obj = new DataObject(dataObject);
        Thing[] keys = obj.getMetadata().getKeys();
        for(Thing key : keys){
        	String name = key.getString("name");
            obj.put(name, request.getParameter(name));
        }
        obj.doAction("load", actionContext);
        
        //转换成Json数据
        response.setContentType("text/plain; charset=utf-8");
        Thing jsonFactory = world.getThing("xworker.text.JsonDataFormat");
        String code = (String) jsonFactory.doAction("getData", actionContext, UtilMap.toMap(new Object[]{"data", obj}));
        response.getWriter().println(code);
    }

}