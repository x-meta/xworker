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
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import xworker.lang.executor.Executor;
import xworker.swt.ActionContainer;
import xworker.swt.events.SwtListener;
import xworker.swt.util.ResourceManager;

public class WorkbentchPrototypeMainCompositeCreator {
	private static final String TAG = WorkbentchPrototypeMainCompositeCreator.class.getName();
	
    public static void tabItemSelected(ActionContext actionContext){
        Executor.info(TAG, "table selection");
    }

    public static void tabFolderMouseDoubleClicked(ActionContext actionContext){
    	Event event = (Event) actionContext.get("event");
        
        //如果是鼠标中键什么也不做
        if(event.button == 2) return;
        
        CTabFolder tabFolder = (CTabFolder) event.widget;
        if("max".equals(tabFolder.getData("maxStatus"))){
            restoreSashFormWeights(actionContext);
            tabFolder.setData("maxStatus", "nomax");
        }else{
            tabFolder.setData("maxStatus", "max");
            if(tabFolder == actionContext.get("leftTabFolder")){
                setSashFormWeights(new int[]{100,0}, new int[]{100,0,0}, actionContext);
            }else if(tabFolder == actionContext.get("contentTabFolder")){
                setSashFormWeights(new int[]{100,0}, new int[]{0,100,0}, actionContext);
            }else if(tabFolder == actionContext.get("rightTabFolder")){
                setSashFormWeights(new int[]{100,0}, new int[]{0,0,100}, actionContext);
            }else if(tabFolder == actionContext.get("bottomTabFolder")){
                setSashFormWeights(new int[]{0,100}, new int[]{0,0,100}, actionContext);             
            }
        }
        
        
       
    }

    //设置sashForm的权值
    public static void setSashFormWeights(int[] mainSashWeights, int[] topSashWeights, ActionContext actionContext){
        SashForm mainSashForm = (SashForm) actionContext.get("mainSashForm");
        if(mainSashForm != null){
            mainSashForm.setData("oldWeights", mainSashForm.getWeights());
            mainSashForm.setWeights(mainSashWeights);
        }
        
        SashForm topSashForm = (SashForm) actionContext.get("topSashForm");
        if(topSashForm != null){
            topSashForm.setData("oldWeights", topSashForm.getWeights());
            topSashForm.setWeights(topSashWeights);
        }
    }
    
    //还原sashform的权值
    public static void restoreSashFormWeights(ActionContext actionContext){
    	SashForm mainSashForm = (SashForm) actionContext.get("mainSashForm");
        if(mainSashForm != null){
            mainSashForm.setWeights((int[]) mainSashForm.getData("oldWeights"));
        }
        
        SashForm topSashForm = (SashForm) actionContext.get("topSashForm");
        if(topSashForm != null){
            topSashForm.setWeights((int[]) topSashForm.getData("oldWeights"));
        }
    }
    
    @SuppressWarnings("unchecked")
	public static void initMainMenu(ActionContext actionContext){
        Shell shell = (Shell) actionContext.get("shell");
        
        //是否有菜单
        Thing workbentchThing = (Thing) shell.getData("thing");
        if(workbentchThing == null || workbentchThing.getBoolean("MENU") == false){
            return;
        }
        
        //获取菜单的定义
        Map<String, Thing> editorMenus = new HashMap<String, Thing>();
        ActionContext editorActionContext = null;
        Thing workbentchMenus = (Thing) workbentchThing.get("menus@0");
        Map<String, Object> currentEditor = (Map<String, Object>) actionContext.get("currentEditor");
        if(currentEditor != null){
            editorActionContext = (ActionContext) currentEditor.get("actionContext");
            Thing editorThing = (Thing) currentEditor.get("editorThing");
            Thing editorMenu = (Thing) editorThing.doAction("getMenus", actionContext);
            if(editorMenu != null){
                for(Thing menu : editorMenu.getChilds()){
                    editorMenus.put(menu.getString("name"), menu);
                }
            }
        }
        
        //清除原有菜单
        Menu mainMenu = (Menu) actionContext.get("mainMenu");
        for(MenuItem item : mainMenu.getItems()){
            item.dispose();
        }
        
        //创建菜单
        createMenu(mainMenu, workbentchMenus, editorMenus, actionContext, editorActionContext);
        
        //初始化菜单的设置，如果需要
        Thing actions = (Thing) actionContext.get("actions"); 
        if(actions != null){
            actions.doAction("initMenu", actionContext);
        }
        if(currentEditor != null){
            editorActionContext = (ActionContext) currentEditor.get("actionContext");
            if(editorActionContext != null && editorActionContext.get("actions") != null){
                actions.doAction("initMenu", editorActionContext);
            }
        }    
    }
    
    //创建菜单的递归函数
    public static void  createMenu(Menu parent, Thing workbentchMenus, Map<String, Thing> editorMenus, ActionContext actionContext, ActionContext editorActionContext){   
        Map<String, Object> handledEditorMenus = new HashMap<String, Object>();
        if(workbentchMenus != null){
            for(Thing workbentchMenu : workbentchMenus.getChilds()){
                createMenuItem(parent, workbentchMenu, editorMenus, actionContext, true, handledEditorMenus, editorActionContext);
            }
        }
        
        if(editorMenus != null){
        	for(String key : editorMenus.keySet()){
        		Thing editorMenu = editorMenus.get(key);
                Thing eMenu = (Thing) editorMenu.get("value");
                if(handledEditorMenus.get(eMenu.getString("name")) == null){
                    createMenuItem(parent, eMenu, editorMenus, actionContext, false, handledEditorMenus, editorActionContext);
                }
            }
        }
    }
    
    //创建一个菜单节点
    public static void createMenuItem(Menu parent, Thing menu, Map<String,Thing> editorMenus, ActionContext actionContext, boolean isWorkbentchMenu, Map<String, Object> handledEditorMenus, ActionContext editorActionContext){
        Thing editorMenu = editorMenus.get(menu.getString("name"));
        if(isWorkbentchMenu && editorMenu != null && "BEFORE".equals(editorMenu.getString("insertType"))){
             //插入创建编辑器的菜单
             for(Thing editM : editorMenu.getChilds()){
                 createMenuItem(parent, editM, editorMenus, actionContext, false, handledEditorMenus, editorActionContext);
             }
             handledEditorMenus.put(menu.getString("name"), editorMenu);
        }
        
        //菜单的类型
        String type = menu.getString("type");
        if(type == null || "".equals(type)){
            type = menu.getChilds().size() > 0 ? "CASCADE" : "PUSH";
        }
        
        int style = SWT.NONE;
        if("CHECK".equals(type)){
                style = style | SWT.CHECK;
        }else if("CASCADE".equals(type)){
                style = style | SWT.CASCADE;
        }else if("PUSH".equals(type)){
                style = style | SWT.PUSH;
        }else if("RADIO".equals(type)){
                style = style | SWT.RADIO;
        }else if("SEPARATOR".equals(type)){
                style = style | SWT.SEPARATOR;
                
        }
        
        //菜单条目
        MenuItem menuItem = new MenuItem(parent, style);
        menuItem.setText(menu.getMetadata().getLabel());
        menuItem.setData("thing", menu);
        menuItem.addListener(SWT.Selection, (SwtListener) actionContext.get("mainMenuSelection"));
        if(isWorkbentchMenu){
            menuItem.setData("actionContext", actionContext);
            actionContext.getScope(0).put(menu.getString("name") + "MenuItem", menuItem);
        }else{
            menuItem.setData("actionContext", editorActionContext);
            editorActionContext.getScope(0).put(menu.getString("name") + "MenuItem", menuItem);
        }
        
        //创建子节点
        if("CASCADE".equals(type)){
            Menu childmenu = new Menu(menuItem);
            menuItem.setMenu(childmenu);
            actionContext.getScope(0).put(menu.getString("name") + "Menu", childmenu);
            childmenu.setData("thing", menu);
            for(Thing child : menu.getChilds()){
                createMenuItem(childmenu, child, editorMenus, actionContext, isWorkbentchMenu, handledEditorMenus, editorActionContext);
            }
        }
        
        if(isWorkbentchMenu && editorMenu != null && "AFTER".equals(editorMenu.getString("insertType"))){
             //插入创建编辑器的菜单
             for(Thing editM : editorMenu.getChilds()){
                 createMenuItem(parent, editM, editorMenus, actionContext, false, handledEditorMenus, editorActionContext);
             }
             handledEditorMenus.put(editorMenu.getString("name"), editorMenu);
        }
    }

    @SuppressWarnings("unchecked")
	public static void initToolbar(ActionContext actionContext){
        Shell shell = (Shell) actionContext.get("shell");
        
        //是否有工具栏
        Thing workbentchThing = (Thing) shell.getData("thing");
        if(workbentchThing == null || workbentchThing.getBoolean("TOOLBAR") == false){
            return;
        }
        
        //清除原有工具栏
        Composite mainCoolbarComposite= (Composite) actionContext.get("mainCoolbarComposite");
        for(Control child : mainCoolbarComposite.getChildren()){
            child.dispose();
        }
        
        CoolBar mainCoolbar = new CoolBar(mainCoolbarComposite, SWT.HORIZONTAL | SWT.FLAT);
        actionContext.getScope(0).put("mainCoolbar", mainCoolbar);
        mainCoolbar.addListener(SWT.Resize, (SwtListener) actionContext.get("mainCoolbarResize"));
        
        //获取工具栏的定义
        Map<String, Object> editorToolbars = new HashMap<String, Object>();
        ActionContext editorActionContext = null;
        Thing workbentchToolbars = (Thing) workbentchThing.get("toolbars@0");
        Map<String, Object> currentEditor = (Map<String, Object>) actionContext.get("currentEditor");
        if(workbentchToolbars == null){
            workbentchToolbars = new Thing();
        }
        if(currentEditor != null){
            editorActionContext = (ActionContext) currentEditor.get("actionContext");
            Thing editorThing = (Thing) currentEditor.get("editorThing");
            Thing editorToolbar = (Thing) editorThing.doAction("getToolbars", actionContext);
            if(editorToolbar != null){
                for(Thing toolbar : editorToolbar.getChilds()){
                    editorToolbars.put(toolbar.getString("name"), toolbar);
                }
            }
        }
        
        //创建工具栏
        createToolbar(mainCoolbar, workbentchToolbars, editorToolbars, actionContext, editorActionContext);
        
        //mainCoolbar.pack();
        
        for (CoolItem coolItem : mainCoolbar.getItems()) {
            //Executor.info(TAG, "init coolitem, item=" + coolItem);
            Control control = coolItem.getControl();
            //Executor.info(TAG, "coolitem control=" + control);
            if(control == null) continue;
            Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            Point coolSize = coolItem.computeSize(size.x, size.y);
            //Executor.info(TAG, "size=" + coolSize + ",min=" + size);
            coolItem.setMinimumSize(size);
            coolItem.setPreferredSize(coolSize);
            coolItem.setSize(coolSize);
        }
        //mainCoolbar.pack();
        if(mainCoolbar.getItemCount() == 0){
            mainCoolbar.dispose();
            ((GridData) actionContext.get("mainCoolbarCompositeGridLayout")).heightHint = 0;
            mainCoolbarComposite.setVisible(false);
        }else{
            ((GridData) actionContext.get("mainCoolbarCompositeGridLayout")).heightHint = -1;
            mainCoolbarComposite.setVisible(true);    
            mainCoolbarComposite.pack();
            mainCoolbarComposite.layout();    
        }
            
        //初始化toolbar的设置
        Thing actions = (Thing) actionContext.get("actions");
        if(actionContext.get("actions") != null){
            actions.doAction("initToolbar", actionContext);
        }
        if(currentEditor != null){
            editorActionContext = (ActionContext) currentEditor.get("actionContext");
            if(editorActionContext != null && editorActionContext.get("actions") != null){
                actions.doAction("initToolbar", editorActionContext);
            }
        } 
        ((Composite) actionContext.get("mainComposite")).layout();
    }
    
    //创建工具栏的递归函数
    public static void  createToolbar(CoolBar parent, Thing  workbentchToolbars, Map<String, Object> editorToolbars, ActionContext actionContext, ActionContext editorActionContext){
        Map<String, Object> handledEditorToolbars = new HashMap<String, Object>();
        for(Thing workbentchToolbar : workbentchToolbars.getChilds()){
            //Executor.info(TAG, "workbentch toolbar, name=" + workbentchToolbar.name);
            Thing editorToolbar = (Thing) editorToolbars.get(workbentchToolbar.getString("name"));
            if(editorToolbar != null && "BEFORE".equals(editorToolbar.getString("insertType"))){
                 //插入创建编辑器的工具栏
            	CoolItem coolItem = new CoolItem(parent, SWT.NONE);
            	ToolBar toolbar = new ToolBar(parent, SWT.HORIZONTAL | SWT.FLAT);
                 coolItem.setControl(toolbar);
                 for(Thing editM : editorToolbar.getChilds()){
                     createToolbartem(toolbar, editM, editorActionContext);
                 }
                 handledEditorToolbars.put(editorToolbar.getString("name"), editorToolbar);
            }
            
            CoolItem coolItem = new CoolItem(parent, SWT.NONE);
            ToolBar toolbar = new ToolBar(parent, SWT.HORIZONTAL | SWT.FLAT);
            coolItem.setControl(toolbar);
            if(editorToolbar != null && "INSERT_BEFORE".equals(editorToolbar.getString("insertType"))){
                 //插入创建编辑器的工具栏
                 for(Thing editM : editorToolbar.getChilds()){
                     createToolbartem(toolbar, editM, editorActionContext);
                 }
                 handledEditorToolbars.put(editorToolbar.getString("name"), editorToolbar);
            }
            for(Thing workbentchToolItem : workbentchToolbar.getChilds()){
                //Executor.info(TAG, "workbentch Tool Item ,name =" + workbentchToolItem.name);
                createToolbartem(toolbar, workbentchToolItem, actionContext);
            }
            
            if(editorToolbar != null && "INSERT_AFTER".equals(editorToolbar.getString("insertType"))){
                 //插入创建编辑器的工具栏
                 for(Thing editM : editorToolbar.getChilds()){
                     createToolbartem(toolbar, editM, editorActionContext);
                 }
                 handledEditorToolbars.put(editorToolbar.getString("name"), editorToolbar);
            }
            
            if(editorToolbar != null && "AFTER".equals(editorToolbar.getString("insertType"))){
                 //插入创建编辑器的工具栏
            	CoolItem coolItem1 = new CoolItem(parent, SWT.NONE);
            	ToolBar toolbar1 = new ToolBar(parent, SWT.HORIZONTAL | SWT.FLAT);
                 coolItem1.setControl(toolbar1);
                 for(Thing editM : editorToolbar.getChilds()){
                     createToolbartem(toolbar, editM, editorActionContext);
                 }
                 handledEditorToolbars.put(editorToolbar.getString("name"), editorToolbar);
            }
        }
        
        //插入没有插入的编辑器工具栏        
        for(Map.Entry<String, Object> editorToolbar : editorToolbars.entrySet()){
            if(handledEditorToolbars.get(editorToolbar.getKey()) == null){
            	CoolItem coolItem1 = new CoolItem(parent, SWT.NONE);
            	ToolBar toolbar1 = new ToolBar(parent, SWT.HORIZONTAL | SWT.FLAT);
                 coolItem1.setControl(toolbar1);
                 for(Thing editM : ((Thing) editorToolbar.getValue()).getChilds()){
                     createToolbartem(toolbar1, editM, editorActionContext);
                 }
            }
        }
    }
    
    //创建一个工具栏节点
    public static void  createToolbartem(ToolBar parent, Thing toolbarItem, ActionContext actionContext){
        int style = SWT.NONE;
        String type = toolbarItem.getString("type");
        if("CHECK".equals(type)){
            style = SWT.CHECK;
        }else if("DROP_DOWN".equals(type)){
            style = SWT.DROP_DOWN;
        }else if("PUSH".equals(type)){
            style = SWT.PUSH;
        }else if("RADIO".equals(type)){
            style = SWT.RADIO;
        }else if("SEPARATOR".equals(type)){
            style = SWT.SEPARATOR;
        }
   
        //工具栏条目
        ToolItem toolItem = new ToolItem(parent, style);
        //Executor.info(TAG, "create tool item, name=" + toolbarItem.name + ",text=" + toolbarItem.text);
        if(toolbarItem.getString("text") != null){
            toolItem.setText(toolbarItem.getString("text"));
        }
        if(toolbarItem.getString("tooltip") != null){
            toolItem.setToolTipText(toolbarItem.getString("tooltip"));
        }
        boolean enabled = "false".equals(toolbarItem.getString("enabled")) ? false : true;
        toolItem.setEnabled(enabled);	
        
        if("SEPARATOR".equals(type)){
            Thing control = (Thing) toolbarItem.get("control@0");
            if(control != null){
                Bindings bindings = actionContext.push(null);
                try{
                    bindings.put("parent", toolItem);
                    control.doAction("create", actionContext);
                }finally{
                    actionContext.pop();
                }                        
            }
            
            String width= toolbarItem.getString("width");
            if(width != null && !"".equals(width)){
                toolItem.setWidth(toolbarItem.getInt("width"));
            }
        }
        
        toolItem.setData("thing", toolbarItem);
        toolItem.setData("actionContext", actionContext);
        toolItem.addListener(SWT.Selection, (SwtListener) actionContext.get("mainMenuSelection"));
        String icon = toolbarItem.getString("icon");
        if(icon != null && !"".equals(icon)){
            Image image = (Image) ResourceManager.createIamge(icon, actionContext);
            if(image != null){
                toolItem.setImage(image);
            }
        }
        actionContext.getScope(0).put(toolbarItem.getString("name"), toolItem);   
        //Executor.info(TAG, "toolitem created");
    }

    @SuppressWarnings("unchecked")
	public static void initStatusbar(ActionContext actionContext){
    	Shell shell = (Shell) actionContext.get("shell");
    	
        //是否有工具栏
        Thing workbentchThing = (Thing) shell.getData("thing");
        if(workbentchThing == null || workbentchThing.getBoolean("STATUSBAR") == false){
            return;
        }
        
        Composite mainStatusbarComposite = (Composite) actionContext.get("mainStatusbarComposite");
        
        //清除原有工具栏
        CoolBar mainStatusBar = (CoolBar) actionContext.get("mainStatusBar");
        mainStatusBar.dispose();
        
        mainStatusBar = new CoolBar(mainStatusbarComposite, SWT.HORIZONTAL | SWT.FLAT);
        actionContext.getScope(0).put("mainStatusBar", mainStatusBar);
        
        //获取工具栏的定义
        Map<String, Object> editorToolbars = new HashMap<String, Object>();
        ActionContext editorActionContext = null;
        Thing workbentchToolbars = (Thing) workbentchThing.get("statusbars@0");
        Map<String, Object> currentEditor = (Map<String, Object>) actionContext.get("currentEditor");
        //Executor.info(TAG, "currentEditor=" + currentEditor);
        if(currentEditor != null){
            editorActionContext = (ActionContext) currentEditor.get("actionContext");
            Thing editorThing = (Thing) currentEditor.get("editorThing");
            Thing editorToolbar = (Thing) editorThing.doAction("getStatusbars", actionContext);
            if(editorToolbar != null){
                for(Thing toolbar : editorToolbar.getChilds()){
                    editorToolbars.put(toolbar.getString("name"), toolbar);
                }
            }
        }
        
        //创建工具栏
        createToolbar(mainStatusBar, workbentchToolbars, editorToolbars, actionContext, editorActionContext);
        
        //mainCoolbar.pack();
        
        for (CoolItem coolItem : mainStatusBar.getItems()) {
            //Executor.info(TAG, "init coolitem, item=" + coolItem);
            Control control = coolItem.getControl();
            //Executor.info(TAG, "coolitem control=" + control);
            if(control == null) continue;
            Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            Point coolSize = coolItem.computeSize(size.x, size.y);
            //Executor.info(TAG, "size=" + coolSize + ",min=" + size);
            coolItem.setMinimumSize(size);
            coolItem.setPreferredSize(coolSize);
            coolItem.setSize(coolSize);
        }
        //mainCoolbar.pack();
        mainStatusbarComposite.pack();
        mainStatusbarComposite.layout();
        
        //初始化toolbar的设置
        ActionContainer actions = (ActionContainer) actionContext.get("actions");
        if(actionContext.get("actions") != null){
            actions.doAction("initStatusbar", actionContext);
        }
        if(currentEditor != null){
            editorActionContext = (ActionContext) currentEditor.get("actionContext");
            if(editorActionContext.get("actions") != null){
                actions.doAction("initStatusbar", editorActionContext);
            }
        } 
        
        /*
        //创建工具栏的递归函数
        def createToolbar(parent, workbentchToolbars, editorToolbars, actionContext, editorActionContext){
            def handledEditorToolbars = [:];
            for(workbentchToolbar in workbentchToolbars.childs){
                //Executor.info(TAG, "workbentch toolbar, name=" + workbentchToolbar.name);
                def editorToolbar = editorToolbars.get(workbentchToolbar.name);
                if(editorToolbar != null && editorToolbar.insertType == "BEFORE"){
                     //插入创建编辑器的工具栏
                     def coolItem = new CoolItem(parent, SWT.NONE);
                     def toolbar = new ToolBar(parent, SWT.HORIZONTAL | SWT.FLAT);
                     coolItem.setControl(toolbar);
                     for(editM in editorToolbar.childs){
                         createToolbartem(toolbar, editM, editorActionContext);
                     }
                     handledEditorToolbars.put(editorToolbar.name, editorToolbar);
                }
                
                def coolItem = new CoolItem(parent, SWT.NONE);
                def toolbar = new ToolBar(parent, SWT.HORIZONTAL | SWT.FLAT);
                coolItem.setControl(toolbar);
                if(editorToolbar != null && editorToolbar.insertType == "INSERT_BEFORE"){
                     //插入创建编辑器的工具栏
                     for(editM in editorToolbar.childs){
                         createToolbartem(toolbar, editM, editorActionContext);
                     }
                     handledEditorToolbars.put(editorToolbar.name, editorToolbar);
                }
                for(workbentchToolItem in workbentchToolbar.childs){
                    //Executor.info(TAG, "workbentch Tool Item ,name =" + workbentchToolItem.name);
                    createToolbartem(toolbar, workbentchToolItem, actionContext);
                }
                
                if(editorToolbar != null && editorToolbar.insertType == "INSERT_AFTER"){
                     //插入创建编辑器的工具栏
                     for(editM in editorToolbar.childs){
                         createToolbartem(toolbar, editM, editorActionContext);
                     }
                     handledEditorToolbars.put(editorToolbar.name, editorToolbar);
                }
                
                if(editorToolbar != null && editorToolbar.insertType == "AFTER"){
                     //插入创建编辑器的工具栏
                     def coolItem1 = new CoolItem(parent, SWT.NONE);
                     def toolbar1 = new ToolBar(parent, SWT.HORIZONTAL | SWT.FLAT);
                     coolItem1.setControl(toolbar1);
                     for(editM in editorToolbar.childs){
                         createToolbartem(toolbar, editM, editorActionContext);
                     }
                     handledEditorToolbars.put(editorToolbar.name, editorToolbar);
                }
            }
            
            //插入没有插入的编辑器工具栏
            for(editorToolbar in editorToolbars){
                if(handledEditorToolbars.get(editorToolbar.key) == null){
                     def coolItem1 = new CoolItem(parent, SWT.NONE);
                     def toolbar1 = new ToolBar(parent, SWT.HORIZONTAL | SWT.FLAT);
                     coolItem1.setControl(toolbar1);
                     for(editM in editorToolbar.value.childs){
                         createToolbartem(toolbar1, editM, editorActionContext);
                     }
                }
            }
        }
        
        //创建一个工具栏节点
        def createToolbartem(parent, toolbarItem, actionContext){
            int style = SWT.NONE;
            switch(toolbarItem.type){
                case "CHECK":
                    style = SWT.CHECK;
                    break;
                case "DROP_DOWN":
                    style = SWT.DROP_DOWN;
                    break;
                case "PUSH":
                    style = SWT.PUSH;
                    break;
                case "RADIO":
                    style = SWT.RADIO;
                    break;
                case "SEPARATOR":
                    style = SWT.SEPARATOR;
                    break;
            }    
            //工具栏条目
            ToolItem toolItem = new ToolItem(parent, style);
            //Executor.info(TAG, "create tool item, name=" + toolbarItem.name + ",text=" + toolbarItem.text);
            if(toolbarItem.text != null){
                toolItem.setText(toolbarItem.text);
            }
            if(toolbarItem.tooltip != null){
                toolItem.setToolTipText(toolbarItem.tooltip);
            }
            toolItem.setData("thing", toolbarItem);
            toolItem.setData("actionContext", actionContext);
            toolItem.addListener(SWT.Selection, actionContext.mainMenuSelection);
            if(toolbarItem.icon != null && toolbarItem.icon != ""){
                def image = ResourceManager.createIamge(toolbarItem.icon, actionContext);
                if(iamge != null){
                    image.setImage(image);
                }
            }
            actionContext.getScope(0).put(toolbarItem.name + "ToolItem", toolItem);   
            //Executor.info(TAG, "toolitem created");
        }*/
    }

    @SuppressWarnings("unchecked")
	public static void openView(ActionContext actionContext){
        Map<String, Object> views = (Map<String, Object>) actionContext.get("views");
        Thing view = (Thing) actionContext.get("view");
        
        Map<String, Object> v = (Map<String, Object>) views.get(view.getString("name"));
        if(v != null && v.get("opened") != null && (Boolean) v.get("opened")){
            return;
        }
        
        //新的动作上下文
        ActionContext ac = new ActionContext();
        ac.put("workbentchContext", actionContext);
        ac.put("workbentchActions", actionContext.get("workbentchActions"));
        
        CTabFolder tabFolder = null;
        String site = view.getString("site");
        if("LEFT".equals(site)){
            tabFolder = (CTabFolder) actionContext.get("leftTabFolder");
        }else if("RIGHT".equals(site)){
            tabFolder = (CTabFolder) actionContext.get("rightTabFolder");
        }else if("BOTTOM".equals(site)){
            tabFolder = (CTabFolder) actionContext.get("bottomTabFolder");
        }else{
            Executor.warn(TAG, "error site,site=" + site + "，view not init, view=" + view.getMetadata().getPath());
        }
        
        int style = SWT.NONE;
        if(view.getBoolean("closeable")){
            style = style | SWT.CLOSE;
        }
        CTabItem tabItem = new CTabItem(tabFolder, style);
        tabItem.setText(view.getMetadata().getLabel());
        ac.put("parent", tabFolder);
        
        Control control = (Control) view.doAction("createControl", ac);
        tabItem.setControl(control);
        view.doAction("init", ac);
        
        String name = view.getString("name");
        v = (Map<String, Object>) views.get(name);
        if(v == null){
            v = new HashMap<String, Object>();
            v.put("viewThing", view);
            views.put(name, v);
        }
        v.put("actionContext", ac);
        v.put("opened", true);
    }

    @SuppressWarnings("unchecked")
	public static void openEditor(ActionContext actionContext){
        Map<String, Object> editors = (Map<String, Object>) actionContext.get("editors");
        String name = (String) actionContext.get("name");
        
        try{
            Map<String, Object> editor = (Map<String, Object>) editors.get(name);
            if(editor == null){
                Executor.info(TAG, "editor not exists, name=" + name);
                return;
            }
            
            Object data = actionContext.get("data");
            Thing editorThing = (Thing) editor.get("editorThing");
            //查看是否要建立编辑器实例
            String instanceId = String.valueOf(OgnlUtil.getValue("id", data));
            if(editorThing.getBoolean("singleInstance") == true){
                instanceId = ""; //单一实例的ID号唯一
            }
            Map<String, Object> instance = (Map<String, Object>) ((Map<String, Object>) editor.get("instances")).get(instanceId);
            if(instance == null){
                //新的动作上下文
            	ActionContext ac = new ActionContext();
                ac.put("workbentchContext", actionContext);
                ac.put("workbentchActions", actionContext.get("workbentchActions"));
                ac.put("params", actionContext.get("params"));
                
                //打开前的校验
                editorThing.doAction("beforeOpen", ac, UtilMap.toMap(new Object[]{"data",data, "dataId", instanceId}));
        
                //编辑器的菜单和工具栏的事件监听
                Thing listenerThing = World.getInstance().getThing("xworker.app.view.swt.app.workbentch.WorkbentchPrototype/@shell/@mainComposite/@ListenersPrepared");
                listenerThing.doAction("create", ac);
                
                //编辑器的actions
                Thing actionsThing = (Thing) editorThing.doAction("getActions", actionContext);
                if(actionsThing != null){
                     actionsThing.doAction("create", ac);
                }
                
                CTabFolder tabFolder = (CTabFolder) actionContext.get("contentTabFolder");    
                int style = SWT.NONE;
                if(editorThing.getBoolean("closeable")){
                    style = style | SWT.CLOSE;
                }
                CTabItem tabItem = new CTabItem(tabFolder, style);
                tabItem.setText(editorThing.getMetadata().getLabel());
                ac.put("parent", tabFolder);
                ac.put("tabItem", tabItem);
                
                Control control = (Control) editorThing.doAction("createControl", ac);
                tabItem.setControl(control);
                editorThing.doAction("init", ac, UtilMap.toMap(new Object[]{"data",data, "dataId", instanceId}));
                
                tabFolder.setSelection(tabItem);
                tabFolder.showItem(tabItem);
                
                instance = new HashMap<String, Object>();
                instance.put("id", instanceId);
                instance.put("actionContext", ac);
                ac.put("tabItem", tabItem);
                instance.put("tabItem", tabItem);
                instance.put("editorThing", editorThing);
                instance.put("editor", editor);
                tabItem.setData(instance);
                ((Map<String, Object>) editor.get("instances")).put(instanceId, instance);        
                actionContext.getScope(0).put("currentEditor", instance);
                
                ActionContainer workbentchActions = (ActionContainer) actionContext.get("workbentchActions");
                workbentchActions.doAction("initMainMenu");
                workbentchActions.doAction("initToolbar");
            }else{
                //已有实例
                //打开前的校验
                editorThing.doAction("beforeOpen", (ActionContext) instance.get("actionContext"), UtilMap.toMap(new Object[]{"data",data, "dataId", instanceId}));
                
                //初始化
                CTabFolder contentTabFolder = (CTabFolder) actionContext.get("contentTabFolder");    
                ((Thing) editor.get("editorThing")).doAction("init", (ActionContext) instance.get("actionContext"), UtilMap.toMap(new Object[]{"data",data, "dataId",instanceId}));
                contentTabFolder.setSelection((CTabItem) instance.get("tabItem"));
                contentTabFolder.showItem((CTabItem) instance.get("tabItem"));
                actionContext.getScope(0).put("currentEditor", editor);
        
                ActionContainer workbentchActions = (ActionContainer) actionContext.get("workbentchActions");
                workbentchActions.doAction("initMainMenu");
                workbentchActions.doAction("initToolbar");
            }
            
            ((Shell) actionContext.get("shell")).layout();
        }catch(Exception e){
            Executor.info(TAG, "Open editor error", e);
            
            CTabFolder contentTabFolder = (CTabFolder) actionContext.get("contentTabFolder");
            MessageBox box = new MessageBox(contentTabFolder.getShell(), SWT.ICON_WARNING | SWT.OK);
            box.setText("提示");
            box.setMessage("" + e.getMessage());
            box.open();
        }
    }

    public static void initAfterEditoChanged(ActionContext actionContext){
    	ActionContainer workbentchActions = (ActionContainer) actionContext.get("workbentchActions");
    	
        //重新初始化菜单和工具栏
        workbentchActions.doAction("initMainMenu");
        workbentchActions.doAction("initToolbar");
        workbentchActions.doAction("initStatusbar");
        
        //Executor.info(TAG, "init menu & toolbar after close");
    }

    @SuppressWarnings("unchecked")
	public static Object getView(ActionContext actionContext){
        if(actionContext.get("views") != null){
        	Map<String, Object> views = (Map<String, Object>) actionContext.get("views");
            return views.get((String) actionContext.get("viewName"));
        }else{
            return null;
        }
    }

    @SuppressWarnings("unchecked")
	public static Object getEditor(ActionContext actionContext){
    	Map<String, Object> editors = (Map<String, Object>) actionContext.get("editors");
    	String editorName = (String) actionContext.get("editorName");
    	String instanceId = (String) actionContext.get("instanceId");
    	
        Map<String, Object> editor = (Map<String, Object>) editors.get(editorName);
        if(editor == null){
            Executor.info(TAG, "editor not exists, name=" + editorName + ",instanceId=" + instanceId);
            return null;
        }else{
            return ((Map<String, Object>) editor.get("instances")).get(instanceId);
        }
    }

}