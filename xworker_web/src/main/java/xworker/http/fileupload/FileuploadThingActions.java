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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.http.MultiPartRequest;

public class FileuploadThingActions {
	@SuppressWarnings("unchecked")
	public static String doFileuploadAction(ActionContext actionContext) throws ClassNotFoundException, InstantiationException, IllegalAccessException, FileUploadException, ServletException{
		Thing self = (Thing) actionContext.get("self");
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
		
		String key = "__fileuploadAction__";
		FileuploadAction action = (FileuploadAction) self.getData(key);
		Long lastModified = (Long) self.getData(key + "LastModified");
		if(action == null ||lastModified == null || lastModified != self.getMetadata().getLastModified()){
			Class<FileuploadAction> cls = (Class<FileuploadAction>) Class.forName(self.getString("classPath"));
			action = cls.newInstance();			
			self.setData(key, action);
			self.setData(key + "LastModified", self.getMetadata().getLastModified());
		}
		
		//创建MultiPartRequest
        ServletRequestContext context = new ServletRequestContext(request);
        FileItemFactory factory = DiskFileItemFactoryManager.getDiskFileItemFactory(self);
        MultiPartRequest multiPartRequest = new MultiPartRequest(request, factory, context);
        
        //执行
		return action.doService(multiPartRequest, request, response, actionContext);
	}
}