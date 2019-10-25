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
package xworker.html.extjs.xw.remote;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class RemoteSupportCreator {
    public static Object toJavaScriptCode(ActionContext actionContext){
    	//Thing self = (Thing) actionContext.get("self");
    	World  world = World.getInstance();
    	
        Thing codeThing = world.getThing("xworker.html.extjs.xw.remote.RemoteSupport/@Code");
        return codeThing.getString("code");
    }

    public static void httpDo(ActionContext actionContext) throws IOException{
    	//Thing self = (Thing) actionContext.get("self");
    	
    	HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
        response.setContentType("text/plain; charset=utf-8");
        
        World  world = World.getInstance();
        Thing codeThing = world.getThing("xworker.html.extjs.xw.remote.RemoteSupport/@Code");
        //log.info("code=" + codeThing.code);
        response.getWriter().println(codeThing.getString("code"));
    }

}