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
package xworker.app.view.swt.app;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.swt.ActionContainer;
import xworker.swt.design.Designer;
import xworker.swt.util.UtilBrowser;

public class ApplicationCreator {
	private static Logger log = LoggerFactory.getLogger(ApplicationCreator.class);
	
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
        
    	ActionContainer explorerActions = (ActionContainer) actionContext.get("explorerActions");
    	ActionContext explorerContext = (ActionContext) actionContext.get("explorerContext");
    	Shell exshell = (Shell) explorerContext.get("shell");
        if(actionContext.get("explorerActions") != null){
            //设置设计器的事物浏览器
            Designer.setExplorerActions(exshell.getDisplay(), explorerActions);
            
            //浏览器和编辑器的工具
            UtilBrowser utilBrowser = new UtilBrowser(exshell.getDisplay(), explorerActions);
            actionContext.getScope(0).put("utilBrowser", utilBrowser);
        }
        
        
        //创建shell
        World world = World.getInstance();
        Thing shellThing = world.getThing("xworker.app.view.swt.app.tpls.ApplicationTmpl/@shell");
        Shell shell = (Shell) shellThing.doAction("create", actionContext);
        actionContext.getScope(0).put(self.getString("name"), shell);
        actionContext.getScope(0).put("appThing", self);
        
        shell.setText(self.getMetadata().getLabel());
        if(self.getBoolean("haveMenu") == false){
            ((Menu) actionContext.get("mainMenu")).dispose();
        }
        if(self.getBoolean("haveToolBar") == false){
            ((CoolBar) actionContext.get("mainCoolbar")).dispose();
        }
        if(self.getBoolean("haveLeftTabFolder") == false){
            ((CTabFolder) actionContext.get("leftTabFolder")).dispose();
        }
        if(self.getBoolean("haveCenterTabFolder") == false){
            ((CTabFolder) actionContext.get("contentTabFolder")).dispose();
        }
        if(self.getBoolean("haveRightTabFolder") == false){
            ((CTabFolder) actionContext.get("rightTabFolder")).dispose();
        }
        if(self.getBoolean("haveBottomTabFodler") == false){
            ((CTabFolder) actionContext.get("bottomTabFolder")).dispose();
        }
        
        Map<String, Object> views = new HashMap<String, Object>();
        Map<String, Object> menus = new HashMap<String, Object>();
        Map<String, Object> tools = new HashMap<String, Object>();
        Map<String, Object> contexts = new HashMap<String, Object>();
        Bindings scope = actionContext.getScope(0);
        scope.put("view", views);
        scope.put("menus", menus);
        scope.put("tools", tools);
        scope.put("contexts", contexts);
        
        //创建View
        self.doAction("createMenu", actionContext, UtilMap.toMap(new Object[]{"workSetThing", self}));
        self.doAction("createToolBar", actionContext, UtilMap.toMap(new Object[]{"workSetThing", self}));
        self.doAction("createView", actionContext, UtilMap.toMap(new Object[]{"workSetThing", self}));
        return shell;
    }

    @SuppressWarnings("unchecked")
	public static void createMenu(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	Thing appThing = (Thing) actionContext.get("appThing");
        if(appThing.getBoolean("haveMenu")){
            //先释放原有的菜单，然后重新创建菜单
            //释放原有菜单
            MenuItem[] items = ((Menu) actionContext.get("mainMenu")).getItems();    
            if(items != null){        
                for(MenuItem item : items){
                    item.dispose();
                }
            }
            
            ((Map<String, Object>) actionContext.get("menus")).clear();
            //workSet的使用workSet的动作上下文
            ActionContext acContext = (ActionContext) self.doAction("getActionContext", actionContext);
            
            //新建菜单
            Thing menuSet = appThing.getThing("MenuSet@0");
            if(menuSet != null){
                for(Thing child : menuSet.getAllChilds()){
                    child.doAction("createMenu", acContext);
                }
            }
            
            if(actionContext.get("workSetThing") != null){
                menuSet = (Thing) ((Thing) actionContext.get("workSetThing")).get("MenuSet@0");
                if(menuSet != null){
                    for(Thing child : menuSet.getAllChilds()){
                        child.doAction("createMenu", acContext);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
	public static void createToolBar(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Thing appThing = (Thing) actionContext.get("appThing");
    	
        if(appThing.getBoolean("haveToolBar")){
            //先释放原有的菜单，然后重新创建菜单
            //释放原有菜单
            CoolItem[] items = ((CoolBar) actionContext.get("mainCoolbar")).getItems();    
            if(items != null){        
                for(CoolItem item : items){
                    item.dispose();
                }
            }
            
            ((Map<String, Object>) actionContext.get("tools")).clear();
            ActionContext acContext = (ActionContext) self.doAction("getActionContext", actionContext);
            //新建菜单
            Thing toolBarSet = appThing.getThing("ToolBarSet@0");
            if(toolBarSet != null){
                for(Thing child : toolBarSet.getAllChilds()){
                    child.doAction("createToolBar", acContext);
                }
            }
            
            if(actionContext.get("workSetThing") != null){
                toolBarSet = (Thing) ((Thing) actionContext.get("workSetThing")).get("ToolBarSet@0");
                if(toolBarSet != null){
                    for(Thing child : toolBarSet.getAllChilds()){
                        child.doAction("createToolBar", acContext);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
	public static void createView(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
        
        Thing workSet = (Thing) actionContext.get("workSetThing");
        if(workSet == null){
            return;
        }
        
        Thing viewSet = workSet.getThing("ViewSet@0");
        if(viewSet == null){
            return;
        }
        
        //workSet的使用workSet的动作上下文
        ActionContext acContext = (ActionContext) self.doAction("getActionContext", actionContext);
        
        for(Thing child : viewSet.getAllChilds()){
            TabItem tabItem = (TabItem) ((Map<String, Object>) actionContext.get("views")).get(child.getString("id"));
            if(tabItem != null){
                log.info("tabItem already exists, id is same , id=" + child.get("id"));
            }else{        
                tabItem = (TabItem) child.doAction("createView", acContext);
                tabItem.setData("viewThing", child);
                ((Map<String, Object>) actionContext.get("views")).put(child.getString("id"), acContext);
            }
        }
    }

    @SuppressWarnings("unchecked")
	public static Object getActionContext(ActionContext actionContext){
        Thing workSet = (Thing) actionContext.get("workSetThing");
        
        if(workSet == null){
            return null;
        }
        
        ActionContext acContext = (ActionContext) ((Map<String, Object>) actionContext.get("contexts")).get(workSet);
        if(acContext == null){
            acContext = new ActionContext();
            acContext.put("appContext", actionContext);
            acContext.put("workSetThing", workSet);
            ((Map<String, Object>) actionContext.get("contexts")).put(workSet.toString(), acContext);
        }
        
        return acContext;
    }

}