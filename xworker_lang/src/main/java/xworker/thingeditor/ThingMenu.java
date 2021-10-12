package xworker.thingeditor;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;
import xworker.util.XWorkerUtils;

import java.util.*;

/**
 * 模型编辑器中的模型的菜单。
 */
public class ThingMenu implements Comparable<ThingMenu>{
    /** 当前正在编辑器的模型 */
    Thing currentThing;
    Thing thing;
    int sortWeight;
    ActionContext parentContext;
    ActionContext actionContext;
    List<ThingMenu> childs = new ArrayList<>();
    Object thingEditor;
    ThingMenu parent;
    String editorPlatform;

    private ThingMenu(){

    }
    public ThingMenu(Thing currentThing, Thing thing, ActionContext parentContext){
        this.currentThing = currentThing;
        this.thing = thing;
        this.parentContext = parentContext;

        actionContext = new ActionContext();
        actionContext.put("parentContext", parentContext);
        actionContext.put("currentThing", currentThing);
        actionContext.put("thing", currentThing);
        actionContext.put("rootThing", currentThing.getRoot());
        actionContext.put("menu", this);
    }

    public String getEditorPlatform() {
        return editorPlatform;
    }

    public void setEditorPlatform(String editorPlatform) {
        this.editorPlatform = editorPlatform;
    }

    public void setThingEditor(Object thingEditor){
        this.thingEditor = thingEditor;

        actionContext.g().put("thingEditor", thingEditor);
    }

    public Object getThingEditor() {
        return thingEditor;
    }

    public String getLabel(){
        return thing.getMetadata().getLabel();
    }

    public String getName(){
        return thing.getMetadata().getName();
    }

    public String getPlatform(){
        return thing.getStringBlankAsNull("platform");
    }

    public Thing getThing(){
        return thing;
    }

    public ActionContext getActionContext(){
        return actionContext;
    }

    public ActionContext getParentContext() {
        return parentContext;
    }

    public List<ThingMenu> getChilds(){
        return childs;
    }

    public boolean isSeparator(){
        return thing.getBoolean("separator");
    }

    public String getAccelerator(){
        return null;
    }

    /**
     * 执行该菜单，分别执行引用的动作、窗口和doAction行为。
     * @return
     */
    public Object execute(){
        Object result = thing.doAction("doAction", actionContext);

        //执行动作，如果存在
        String action= thing.getStringBlankAsNull("action");
        if(action != null){
            if(action.startsWith("action:")){
                //执行当前模型的动作
                String actionName = action.substring(7, action.length());
                result = currentThing.doAction(actionName, actionContext);
            }else{
                Action action1 = World.getInstance().getAction(action);
                if(action1 != null){
                    result = action1.run(actionContext);
                }
            }
        }

        //打开窗口，如果存在，执行create方法
        String windowPath = thing.getStringBlankAsNull("window");
        if(windowPath != null){
            Map<String, String> params = null;
            int index = windowPath.indexOf("?");
            if(index != -1){
                String paramStr = windowPath.substring(index + 1, windowPath.length());
                windowPath = windowPath.substring(0, index);
                params = UtilString.getParams(paramStr);
            }else{
                params = new HashMap<>();
            }
            Thing window = World.getInstance().getThing(windowPath);
            if(window != null){
                actionContext.peek().put("result", result);
                actionContext.peek().putAll(params);
                result = window.doAction("run", actionContext);
            }
        }

        return result;
    }

    @Override
    public int compareTo(ThingMenu o) {
        if(sortWeight < o.sortWeight){
            return -1;
        }else if(sortWeight == o.sortWeight){
            return 0;
        }else {
            return 1;
        }
    }

    private void initParnets(ThingMenu parent){
        this.parent = parent;
        for(ThingMenu menu : childs){
            menu.initParnets(this);
        }
    }

    public ThingMenu getRoot(){
        ThingMenu parent = this;
        while(parent.parent != null){
            parent = parent.parent;
        }

        return parent;
    }

    public ThingMenu getParent() {
        return parent;
    }

    public void sort(){
        Collections.sort(childs);

        for(ThingMenu child : childs){
            child.sort();
        }
    }
    /**
     * 根据描述者获取定义的菜单。
     */
    public static List<ThingMenu> getThingMenus(Thing currentThing, Thing descriptor, String platform, ActionContext actionContext){
        List<ThingMenu> menus = new ArrayList<>();
        Map<String, String> context = new HashMap<>();
        List<Thing> menuBars = descriptor.getChilds("MenuBar");
        for(Thing menuBar : menuBars){
            merge(currentThing, platform, menus, context, menuBar, actionContext);
        }

        for(Thing ext : descriptor.getAllExtends()){
            menuBars = ext.getChilds("MenuBar");
            for(Thing menuBar : menuBars){
                merge(currentThing, platform, menus, context, menuBar, actionContext);
            }
        }

        menuBars = XWorkerUtils.searchRegistThings(descriptor, "menu", Collections.emptyList(), actionContext);
        if(menuBars != null){
            for(Thing menuBar : menuBars){
                merge(currentThing, platform, menus, context, menuBar, actionContext);
            }
        }

        //应该只对Menubar的部分排序
        Collections.sort(menus);

        ThingMenu root = new ThingMenu();
        root.childs.addAll(menus);
        root.initParnets(null);

        //对子节点进行排序
        for(ThingMenu thingMenu : menus){
            thingMenu.sort();
        }

        return menus;
    }

    private static void merge(Thing currentThing, String platform, List<ThingMenu> menus, Map<String, String> context, Thing menuBar, ActionContext actionContext){
        String path = menuBar.getMetadata().getPath();
        if(context.get(path) == null) {
            context.put(path, path);
        }else{
            //已经初始化了，可能是通过注册索引等
            return;
        }

        for(Thing menu : menuBar.getAllChilds("Menu")){
            if(platform != null && !"".equals(platform)){
                String tp = menu.getStringBlankAsNull("platform");
                if(tp != null && !"".equals(tp) && !platform.equals(tp)){
                    //不符合指定的平台
                    return;
                }
            }

            String name = menu.getMetadata().getName();
            ThingMenu thingMenu = null;
            for(ThingMenu tm : menus){
                if(name.equals(tm.getName())){
                    thingMenu = tm;
                    break;
                }
            }

            if(thingMenu == null){
                thingMenu = new ThingMenu(currentThing, menu, actionContext);
                thingMenu.sortWeight = menu.getInt("sortWeight", 0);
                thingMenu.setEditorPlatform(platform);
                menus.add(thingMenu);
            }else{
                thingMenu.sortWeight = (menu.getInt("sortWeight", 0) + thingMenu.sortWeight) / 2;
            }

            //初始化子节点
            merge(currentThing, platform, thingMenu.childs, context, menu, actionContext);
        }
    }
}
