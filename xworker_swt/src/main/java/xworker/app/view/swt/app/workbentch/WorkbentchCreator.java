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
package xworker.app.view.swt.app.workbentch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.swt.ActionContainer;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.UtilSwt;

public class WorkbentchCreator {
    @SuppressWarnings("unchecked")
	public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	World world = World.getInstance();
    	
        //工作带事物
        actionContext.getScope(0).put("workbentch", self);
        actionContext.getScope(0).put("actions", self);
        
        //创建工作台
        Thing prototype = world.getThing("xworker.app.view.swt.app.workbentch.WorkbentchPrototype/@shell");
        Shell shell = (Shell) prototype.doAction("create", actionContext);
        shell.setData("thing", self);
        shell.setText(self.getMetadata().getLabel());
        
        //初始化界面布局
        if(self.getBoolean("MENU") == false){
            ((Menu) actionContext.get("mainMenu")).setVisible(false);
            shell.setMenuBar(null);
        }
        if(self.getBoolean("TOOLBAR") == false){
            ((Composite) actionContext.get("mainCoolbarComposite")).dispose();    
            //mainCoolbar.setVisible(false);
            //mainCoolbar.layoutData.heightHint = 0;
        }
        if(self.getBoolean("STATUSBAR") == false){
            ((Composite) actionContext.get("mainStatusbarComposite")).dispose();
            //mainStatusBar.setVisible(false);
            //mainStatusBar.layoutData.heightHint = 0;
        }
        boolean left = self.getBoolean("LEFT");
        boolean right = self.getBoolean("RIGHT");
        //boolean bottom = self.getBoolean("BOTTOM");
        SashForm topSashForm = (SashForm) actionContext.get("topSashForm");
        SashForm mainSashForm = (SashForm) actionContext.get("mainSashForm");
        CTabFolder rightTabFolder = (CTabFolder) actionContext.get("rightTabFolder");
        CTabFolder leftTabFolder = (CTabFolder) actionContext.get("leftTabFolder");
        CTabFolder bottomTabFolder = (CTabFolder) actionContext.get("bottomTabFolder");
        
        if(left && !right){
            int[] weights = topSashForm.getWeights();
            weights[1] = weights[1] + weights[2];
            weights[2] = 0;
            rightTabFolder.setVisible(false);
            topSashForm.setWeights(weights);
        }else if(!left && right){
            int[] weights = topSashForm.getWeights();
            weights[1] = weights[1] + weights[0];
            weights[0] = 0;
            leftTabFolder.setVisible(false);
            topSashForm.setWeights(weights);
        }else if(!left && !right){
            int[] weights = topSashForm.getWeights();
            weights[1] = 100;
            weights[0] = 0;
            weights[2] = 0;
            leftTabFolder.setVisible(false);
            rightTabFolder.setVisible(false);
            topSashForm.setWeights(weights);
        }
        
        if(self.getBoolean("BOTTOM") == false){
            int[] weights = mainSashForm.getWeights();
            weights[0] = 100;
            weights[1] = 0;
            bottomTabFolder.setVisible(false);
            mainSashForm.setWeights(weights);
        }
        
        ActionContainer workbentchActions = (ActionContainer) actionContext.get("workbentchActions");
        //初始化主菜单
        workbentchActions.doAction("initMainMenu", actionContext);
        //初始化主工具栏
        workbentchActions.doAction("initToolbar", actionContext);
        //初始化状态栏
        workbentchActions.doAction("initStatusbar", actionContext);
        
        //创建view
        Map<String, Object> views = new HashMap<String, Object>();
        actionContext.getScope(0).put("views", views);
        List<Thing> viewsThingList = (List<Thing>) self.get("views@");
        for(Thing viewThings : viewsThingList){
            for(Thing viewThing : viewThings.getChilds()){
                Map<String, Object> view = new HashMap<String, Object>();
                view.put("viewThing", viewThing);
                views.put(viewThing.getString("name"), view);
                
                if(viewThing.getBoolean("defaultOpen")){
                    workbentchActions.doAction("openView", actionContext, UtilMap.toMap(new Object[]{"view", viewThing}));
                }
            }
        }
        
        //创建编辑器索引
        Map<String, Object> editors = new HashMap<String, Object>();
        actionContext.getScope(0).put("editors", editors);
        List<Thing> editorThingsList = (List<Thing>) self.get("editors@");
        for(Thing editorThings : editorThingsList){
            for(Thing editorThing : editorThings.getChilds()){
                editors.put(editorThing.getString("name"), UtilMap.toMap(new Object[]{"editorThing", editorThing, "instances", new HashMap<String, Object>()}));
            }
        }
        
        ((Composite) actionContext.get("mainComposite")).layout();
        
        int width = UtilSwt.getInt(self.getInt("width", 1024));
        int height = UtilSwt.getInt(self.getInt("height", 768));
        shell.setSize(width, height);
        SwtUtils.centerShell(shell);
        
        //执行初始化
        self.doAction("init", actionContext);
        
        return shell;
    }

    public static void run(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
        
        Thing runner = new Thing("xworker.swt.xworker.SwtRunner");
        runner.put("shellThingPath", self.getMetadata().getPath());
        runner.put("shellName", self.getMetadata().getPath());
        runner.doAction("run", actionContext);
    }

}