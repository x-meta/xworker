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
package xworker.lang.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Category;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.util.ThingRegistor;

import xworker.lang.executor.Executor;
import xworker.lang.executor.NotificationExecutorService;

public class ProjectCreator {
	private static Logger log = LoggerFactory.getLogger(ProjectCreator.class);
	
    public static void init(ActionContext actionContext){
    	World world = World.getInstance();
        
        //重新设置元事物，Java代码中定义的元事物是最简单的，有更多功能的更好一些
        Thing metaThing = world.getThing("xworker.lang.MetaThing").detach();
        
        //保留元事物的路径
        metaThing.getMetadata().setPath("xworker.lang.MetaThing");
        metaThing.initChildPath();
        world.metaThing = metaThing;
                
        //注册事物        
        ThingRegistor.regist("_xworker_thing_attribute_editor_openDataListener", "xworker.ide.worldexplorer.swt.shareScript.ThingEditor/@scripts/@openDataListener");
        
        //derby system path,Derby已经不再使用，而改名sqlite了
        //java.lang.System.setProperty("derby.system.home", world.getPath() + "/databases/derby");

        //执行其他项目的初始化
        //World.getInstance().runActionAsync("xworker.lang.config.Project/@actions/@initBackground", null);
        
        //设置默认的执行服务
        Executor.setDefaultExecutorService(new NotificationExecutorService());
    }

    public static void initBackground(ActionContext actionContext){
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
                            Thing p = world.getThing(projectPath + ".config.Project");
                            if(p != null){
                                if("xworker.lang".equals(projectPath)){
                                     continue;
                                }
                            	try{
                            		p.doAction("init", actionContext);
                            		log.info("初始化项目：" + p.getMetadata().getPath());
                            	}catch(Exception e){
                            		log.error("初始化项目" + projectPath + ".config.Project" + "失败", e);
                            	}catch(Error ee){
                            		log.error("初始化项目" + projectPath + ".config.Project" + "失败", ee);
                            	}
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
            log.error("init new project config error", e);
        }
    }

}