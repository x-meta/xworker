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
package xworker.http.fileupload;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.http.HttpRequestBean;
import xworker.http.MultiPartRequest;

public class FileuploadActionsInitFileuploadRequestCreator {
    @SuppressWarnings("unchecked")
	public static Object run(ActionContext actionContext) throws FileUploadException{
    	Thing self = (Thing) actionContext.get("self");
        HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
        
        //创建MultiPartRequest
        ServletRequestContext context = new ServletRequestContext(request);
        FileItemFactory factory = DiskFileItemFactoryManager.getDiskFileItemFactory(self);
        MultiPartRequest multiPartRequest = new MultiPartRequest(request, factory, context);
        HttpRequestBean requestBean = new HttpRequestBean(multiPartRequest);
        
        try{
            Bindings bindings = actionContext.push(null);
            bindings.put("request", multiPartRequest);
            bindings.put("requestBean", requestBean);
        
            Object result = null;
            for(Thing actions : (List<Thing>) self.get("actions@")){
                for(Thing action : actions.getChilds()){
                    result = action.getAction().run(actionContext, null, true);
        
                    if(ActionContext.RETURN == actionContext.getStatus() || 
                        ActionContext.CANCEL == actionContext.getStatus() || 
                        ActionContext.BREAK == actionContext.getStatus() || 
                        ActionContext.EXCEPTION == actionContext.getStatus()){
                        break;
                    }
                }
            }
            
            return result;
        }finally{
            actionContext.pop();
        }
    }

}