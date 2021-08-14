package xworker.swt.app;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.workbench.ICoolBarContainer;
import xworker.workbench.IMenuContainer;

import java.util.Map;

public class WorkbenchConfig {
    //动作：xworker.swt.SwtThingEditorConfig/@actions/@create
    public static void create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Workbench workbench = actionContext.getObject("workbench");

        //菜单
        IMenuContainer menuContainer = workbench.getEditorContainer().getMenuContainer();
        if(menuContainer != null) {
            for (Thing menuConfig : self.getChilds("MenuConfig")) {
                menuContainer.setEditorMenu(menuConfig, actionContext);
            }
        }

        //工具栏
        ICoolBarContainer coolBarContainer = workbench.getEditorContainer().getCoolBarContainer();
        if(coolBarContainer != null){
            for(Thing coolbarConfig : self.getChilds("CoolBarConfig")){
                coolBarContainer.setEditorCoolBar(coolbarConfig, actionContext);
            }
        }

        //视图
        for(Thing views : self.getChilds("Views")){
            for(Thing view : views.getChilds()){
                String id = view.doAction("getId", actionContext);
                String type = view.doAction("getType", actionContext);
                Thing viewThing = view.doAction("getComposite", actionContext);

                Map<String, Object> params = view.doAction("getParams", actionContext);
                workbench.openView(id, viewThing, type, view.getBoolean("closeable"), params);
            }
        }

        //编辑器
        for(Thing editors : self.getChilds("Editors")){
            for(Thing editor : editors.getChilds()){
                //编辑器是OpenEditor动作
                editor.getAction().run(actionContext, "editorContainer", workbench.getEditorContainer());
            }
        }
    }
}
