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
package xworker.http.controls;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.security.SecurityManager;

public class SimpleControlCreator {
	public static String httpDo(ActionContext actionContext) throws Exception{
		//获取SimpleControl框架配置本身
        Thing self = (Thing) actionContext.get("self");
        
        //权限检查
        if(!checkPermission(self, actionContext)){
        	return null;
        }
        
        //登录校验
        if(self.getBoolean("checkLogin")){
        	Object obj = self.doAction("doCheckLogin", actionContext);
        	if(obj instanceof Boolean && (Boolean) obj == false){
        		return null;
        	}
        }
        
        String result = "success";
        //执行业务逻辑，并返回结果
        Object r = self.doAction("doAction", actionContext);
        if(r instanceof String){
            result = (String) r;
        }
        
        //寻找并处理结果
        List<Thing> results = self.getChilds("result");
		Thing resultObject = null;
		for(int i=0; i<results.size(); i++){
			Thing rObject = results.get(i);
			if(rObject.getMetadata().getName().equals(result)){	
				resultObject = rObject;
                break;
			}
		}
		
		//执行结果的方法，相当于输出界面
		if(resultObject != null){
			resultObject.doAction("doResult", actionContext);
		}
		
		return result;
       
    }
	
	public static boolean checkPermission(Thing self, ActionContext actionContext){
		if(self.getBoolean("checkPermission")){
			return SecurityManager.doCheck("WEB", self.getString("permission"), self.getString("action"), self.getMetadata().getPath(), actionContext);
		}else{
			return true;
		}
	}

	public static boolean doCheckLogin(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		HttpSession session = (HttpSession) actionContext.get("session");
		HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
		String name = self.getStringBlankAsNull("loginAttribute");
		if(name == null){
			return true;
		}else{
			if(session.getAttribute(name) != null){
				return true;
			}else{
				response.sendRedirect(self.getString("loginUrl"));
				return false;
			}
		}
	}
}