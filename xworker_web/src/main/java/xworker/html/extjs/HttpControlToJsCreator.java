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
package xworker.html.extjs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class HttpControlToJsCreator {
    public static void doAction(ActionContext actionContext) throws IOException{
    	//Thing self = (Thing) actionContext.get("self");
    	World world = World.getInstance();
    	
    	HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
    	HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
        response.setContentType("text/plain; charset=utf-8");
        
        //获取ExtThing
        String path = request.getParameter("request");
        if(path != null){
            Thing extThing = world.getThing(path);
            if(extThing != null){
                //文件名
                String fileName = extThing.getRoot().getMetadata().getPath().replace(':','/').replace('.','/');
                fileName = world.getPath() + "/work/extjs/" + fileName + "/p_" + extThing.getRoot().getMetadata().getPath().hashCode() + "_js.js";
                File file = new File(fileName);
                
                //是否要重新生成
                if(!file.exists() || file.lastModified() < extThing.getMetadata().getLastModified()){
                     if(!file.exists()){
                          file.getParentFile().mkdirs();
                     }
                     
                     String jsCode = (String) extThing.doAction("toJsCode", actionContext);
                     FileOutputStream fout = new FileOutputStream(file);
                     try{
                          fout.write(jsCode.getBytes("utf-8"));
                     }finally{
                         fout.close();
                     }
                }
                
                FileInputStream fin = new FileInputStream(file);
                try{
                    byte[] bytes = new byte[fin.available()];
                    fin.read(bytes);
                    response.getOutputStream().write(bytes);
                    return;
                }finally{ 
                    fin.close();
                }
            }else{
                response.getWriter().println("//thing not found ：" + path);
            }
        }else{
            response.getWriter().println("//path parameter not fount");
        }
    }

}