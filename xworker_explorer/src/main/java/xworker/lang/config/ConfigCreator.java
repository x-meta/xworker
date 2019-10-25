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

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ThingRegistor;

public class ConfigCreator {
    public static void init(ActionContext actionContext){
    	World world = World.getInstance();
        //import xworker.util.ThingOgnlAccessor;
        
        //初始化Ognl读Thing的类
        //ThingOgnlAccessor.init();
        
        System.out.println("init world=" + world);
        
        //重新设置元事物，Java代码中定义的元事物是最简单的，有更多功能的更好一些
        Thing metaThing = world.getThing("xworker.lang.MetaThing").detach();
        //保留元事物的路径
        metaThing.getMetadata().setPath("xworker.lang.MetaThing");
        metaThing.initChildPath();
        world.metaThing = metaThing;
        
        //检查全局配置是否存在
        Thing globalConfig = world.getThing("_local.xworker.config.GlobalConfig");
        if(globalConfig == null){
            globalConfig = new Thing("xworker.ide.config.decriptors.GlobalConfig");
            globalConfig.put("name", "GlobalConfig");
            globalConfig.copyTo("_local", "_local.xworker.config");
        }
        
        //注册事物
        ThingRegistor.regist("_xworker_thing_attribute_editor_config", "xworker.lang.attributeEditors.EditorConfig");
        ThingRegistor.regist("_xworker_thing_attribute_editor_CodeEditor", "xworker.swt.xworker.CodeEditor");
        ThingRegistor.regist("_xworker_thing_attribute_editor_GridData", "xworker.swt.layout.GridData");
        ThingRegistor.regist("_xworker_thing_attribute_editor_CodeEditor", "xworker.swt.xworker.CodeEditor");
        ThingRegistor.regist("_xworker_thing_attribute_editor_HtmlEditor", "xworker.swt.xworker.HtmlEditor");
        ThingRegistor.regist("_xworker_thing_attribute_editor_openDataListener", "xworker.ide.worldExplorer.swt.shareScript.ThingEditor/@scripts/@openDataListener");
        
        ThingRegistor.regist("_xworker.swt_model", "xworker.swt.model.Model");
        ThingRegistor.regist("_xworker_globalConfig", "_local.xworker.config.GlobalConfig");
        //log.info("ThingRegister regiseted all");
        //log.info("ThingRegister class=" + ThingRegistor.getThings());
        
        //执行其他项目的初始化
        World.getInstance().runActionAsync("xworker.ide.config.Project/@actions/@initBackground", null);
    }

    public static void initBackground(ActionContext actionContext){
    	/*
        import java.io.File;
        
        import org.xmeta.Thing;
        import xworker.listeners.SwtMenuListener;
        
        //derby system path
        java.lang.System.setProperty("derby.system.home", world.getPath() + "/databases/derby");
        
        //菜单监听，监听事物的变更
        SwtMenuListener swtMenuListener = SwtMenuListener.getInstance();
        world.registThingManagerListener("*", swtMenuListener);
        
        //去掉project后新版本的初始化方法
        try{
            for(manager in world.getThingManagers()){
                def rootCategory = manager.getCategory(null);
                if(rootCategory != null){
                    for(lv1Category in rootCategory.getCategorys()){
                        for(lv2Category in lv1Category.getCategorys()){
                            //激活事物菜单
                            String projectPath = lv1Category.getSimpleName() + "." + lv2Category.getSimpleName();
                            world.getThing(projectPath + ".config.ProjectMenuSwt"); 
                            
                            Thing p = world.getThing(projectPath + ".config.Project");
                            if(p != null){
                                if(projectPath == "xworker.lang"){
                                     continue;
                                }
                            	try{
                            		p.doAction("init", actionContext);
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
        
        try{
            //初始化各个项目
            List projects = world.getProjects();
            for(project in projects){
                for(manager in project.getThingManagers()){
                    //激活事物菜单
                    world.getThing(project.getName() + ":" + manager.name + ":config.ProjectMenuSwt"); 
                    
                    Thing p = world.getThing(project.getName() + ":" + manager.name + ":config.Project");
                    if(p != null){
                        if(project.name == "xworker" && manager.name == "core"){
                             continue;
                        }
                    	try{
                    		p.doAction("init", actionContext);
                    	}catch(Exception e){
                    		log.error("初始化项目" + project.getName() + "失败", e);
                    	}catch(Error ee){
                    		log.error("初始化项目" + project.getName() + "失败", ee);
                    	}
                    }
                }
            }
        }catch(Exception e){
        }
        
        //管理备份文件，删除时间超长的文件
        manageBackUpFiles(null);
        
        def manageBackUpFiles(File file){
        	if(file == null){	    
        		file = new File(world.getPath() + "/bakup/");
        		if(!file.exists() || file.isFile()){	
        		    //log.info("not exists");	
        			return;				
        		}
        	}
        		
        	if(file.isDirectory()){
        		for(File f : file.listFiles()){
        			manageBackUpFiles(f);
        		}
        			
        		if(file.listFiles().length == 0){
        			file.delete();
        		}
        	}else{
        		//删除10天以前的备份
        		//log.info(file.getName());
        		if((System.currentTimeMillis() - file.lastModified()) > 10 * 24 * 3600000){
        		    //log.info(file.getName());
        			file.delete();
        		}
        	}
        	
        }*/
    }
}