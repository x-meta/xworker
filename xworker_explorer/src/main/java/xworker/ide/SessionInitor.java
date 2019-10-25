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
package xworker.ide;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Category;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;

import xworker.ui.UIManager;
import xworker.ui.UIRequest;

/**
 * 初始化会话窗口中的会话树。
 * 
 * @author Administrator
 *
 */
public class SessionInitor {
	private static Logger logger = LoggerFactory.getLogger(SessionInitor.class);
	
	public static void initSessionTree(final ActionContext actionContext){
		//开始作为第一个会话
		Thing start = World.getInstance().getThing("xworker.ui.session.manager.SessionManager");
		UIRequest startRequest = new UIRequest(start, "", 0, actionContext);		
		UIManager.requestUI("xworker_session_swt_sessionTree", startRequest, actionContext);
		
		//遍历事物管理器下的其他会话
		new Thread(){
			public void run(){
				World world = World.getInstance();
		        //去掉project后新版本的初始化方法
		        try{
		            for(int i=0; i<world.getThingManagers().size(); i++){
		            	ThingManager manager = world.getThingManagers().get(i);
		            	
		                Category rootCategory = manager.getCategory(null);
		                if(rootCategory != null){
		                    for(Category lv1Category : rootCategory.getCategorys()){
		                        for(Category lv2Category : lv1Category.getCategorys()){
		                            //初始化其他项目
		                            String projectPath = lv1Category.getSimpleName() + "." + lv2Category.getSimpleName();
		                            Thing session = world.getThing(projectPath + ".config.Session");
		                            if(session != null){
		                            	session.doAction("run", actionContext);
		                            }
		                        }
		                    }
		                }
		            }
		        }catch(Exception e){
		            logger.error("init new project config error", e);
		        }
			}
		}.start();
	}
}