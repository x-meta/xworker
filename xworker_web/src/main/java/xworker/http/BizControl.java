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
package xworker.http;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

/**
 * 业务Web Control。
 * 
 * @author zyx
 *
 */
public class BizControl {
	private static Logger log = LoggerFactory.getLogger(HttpServletDo.class);
	private static World world = World.getInstance();
	
	/**
	 * 执行Web Control。
	 * 
	 * @param actionContext
	 * @return
	 * @throws IOException 
	 */
	public static String doControl(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");		
		Thing webApp = (Thing) actionContext.get("webApp");
		HttpSession session = (HttpSession) actionContext.get("session");
		HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
		
		//信息提示页面
		String messageUrl = self.getString("messageUrl");
		if((messageUrl == null || "".equals(messageUrl)) && webApp != null){
			messageUrl = webApp.getString("messageUrl");
		}
		
		//校验是否需要登录
		if(self.getBoolean("isNeedLogin") || (webApp != null && webApp.getBoolean("isNeedLogin"))){
			String userLoginSession = self.getString("userLoginSession");
			if((userLoginSession == null || "".equals(userLoginSession)) && webApp != null){
				userLoginSession = webApp.getString("userLoginSession");
			}
			if(userLoginSession == null || "".equals(userLoginSession)){
				//默认userLgoin
				userLoginSession = "userLogin";
			}
			
			if(session.getAttribute(userLoginSession) == null){
				//没有登录
				String loginUrl = self.getString("loginUrl");
				if((loginUrl == null || "".equals(loginUrl)) && webApp != null){
					loginUrl = webApp.getString("loginPage");
				}
				if(loginUrl != null && !"".equals(loginUrl)){
					response.sendRedirect(loginUrl);
					return "success";
				}else if(messageUrl != null && !"".equals(messageUrl)){
					actionContext.put("message", "需要登录，但系统没有设定登录页面！");
					Thing messageThing = world.getThing("messageUrl");
					messageThing.doAction("httpDo", actionContext);
					return "success"; 
				}else{
					//什么也不做
					log.error("需要登录，但系统没有设定登录页面！");
					return "success";
				}
			}
		}
		
		//校验权限，只有在设定了权限和权限脚本时才需要校验
		String permission = self.getString("permission");
		if(permission != null && !"".equals(permission)){
			String permissionCheckScript = self.getString("permissionCheckScript");
			if((permissionCheckScript == null || "".equals(permissionCheckScript)) && webApp != null){
				permissionCheckScript = webApp.getString("permissionCheckScript");
			}
			
			if(permissionCheckScript != null){
				Action action = world.getAction(permissionCheckScript);
				if(action != null){
					Object result = action.run(actionContext);
					if(result instanceof Boolean && ((Boolean) result).booleanValue() == false){
						//权限校验失败
						String message = self.getString("noPermissionInfo");
						if(message == null || "".equals(message)){
							actionContext.put("message", "没有指定的权限：" + permission);
						}else{
							actionContext.put("message", message);
						}
						
						if(messageUrl != null && !"".equals(messageUrl)){
							actionContext.put("message", "需要登录，但系统没有设定登录页面！");
							Thing messageThing = world.getThing("messageUrl");
							messageThing.doAction("httpDo", actionContext);
							return "success"; 
						}else{
							log.error("没有权限，但系统没有设定提示页面！");
							return "success";
						}
					}
				}
			}			
		}
		
		//执行业务程序
	    String result = "success";
        Object r = self.doAction("doAction", actionContext);
        if(r instanceof String){
            result = (String) r;
        }
    
        //寻找并处理结果
        List<Thing> results = self.getChilds("result");
		Thing resultObject = null;
		for(int i=0; i<results.size(); i++){
			Thing rObject = (Thing) results.get(i);
			if(rObject.getMetadata().getName().equals(result)){	
				resultObject = rObject;
                break;
			}
		}
		
		//执行结果的方法
		if(resultObject != null){
			resultObject.doAction("doResult", actionContext);
		}
		
		return result;
	}
	
	/**
	 * 初始化上下文。
	 * 
	 * @param context
	 * @return
	 */
	public static Object init(ActionContext context){
		//取原始脚本的动作上下文
		ActionContext acContext = (ActionContext) context.get("acContext");
		Thing self = (Thing) acContext.get("self");
		
		//初始化WebApp，首先从自身寻找webApp的定义
		String webAppPath = self.getString("webAppPath");
		
		HttpServletRequest request = (HttpServletRequest) acContext.get("request");
		//其次从参数_webApp寻找
		if(webAppPath == null || "".equals(webAppPath)){			
			webAppPath = request.getParameter("_webApp");
		}
		
		//再次从dataSet参数中指定的dataSet寻找
		if(webAppPath == null || "".equals(webAppPath)){
			String dataSetStr = request.getParameter("dataSet");
			Thing dataSet = world.getThing(dataSetStr);
			if(dataSet != null){
				webAppPath = dataSet.getString("webApp");
			}
		}
		
		//最后从全局定义中寻找
		if(webAppPath == null || "".equals(webAppPath)){
			Thing globalCfg = world.getThing("core:config:GlobalConfig");
			webAppPath = globalCfg.getString("defaultWebApp");
		}
		
		Thing webApp = world.getThing(webAppPath);
		acContext.getScope(0).put("webApp", webApp);
		
		//初始化数据库的名称
		String dbSession = self.getString("database");
		if(dbSession == null || "".equals(dbSession)){
			//从webApp中取
			if(webApp != null){
				dbSession = webApp.getString("databaseName");
			}
		}		
		acContext.getScope(0).put("dbSession", dbSession);		
		
		return null;
	}
}