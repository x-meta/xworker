package xworker.swt.app.editors;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import xworker.lang.executor.Executor;
import xworker.swt.app.IEditor;
import xworker.swt.util.SwtUtils;
import xworker.swt.xworker.content.SwtQuickContentHandler;
import xworker.util.ThingUtils;
import xworker.util.XWorkerUtils;
import xworker.workbench.EditorParams;

@ActionClass(creator="createInstance")
public class RegistThingEditor {
	private static final String TAG = RegistThingEditor.class.getName();

    public static EditorParams<Object> createParams(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Object content = actionContext.getObject("content");
        Thing thing = null;
        if (content instanceof String) {
            thing = World.getInstance().getThing((String) content);
        }else  if(content instanceof Thing){
            thing = (Thing) content;
        }
        if(thing != null && (thing.isThing("xworker.swt.xworker.ThingRegistThing") || thing.isThing("xworker.lang.util.ThingIndex"))){
            return new EditorParams<Object>(self, "thingregistorresgistor:" + thing.getMetadata().getPath(), thing) {
                @Override
                public Map<String, Object> getParams() {
                    Map<String, Object> params = new HashMap<>();
                    params.put("thing", this.getContent());

                    return params;
                }
            };
        }

        return null;
    }

    public Object createDataParams(){
        //xworker.swt.app.editors.RegistThingEditor/@actions1/@createDataParams
        Object data = actionContext.getObject("data");
        
        if(data instanceof Thing){
            if(((Thing) data).isThing("xworker.swt.xworker.ThingRegistThing")){
                Map<String, Object> params = UtilMap.toMap("thing", data);
                params.put(IEditor.EDITOR_THING, world.getThing("xworker.swt.app.editors.RegistThingEditor"));
                params.put(IEditor.EDITOR_ID, "ThingRegist:" + ((Thing) data).getMetadata().getPath());
                return params;
            }
        }
        
        return null;
    }
    
    public void chageRegistTypeButtonSelection(){
        //xworker.swt.app.editors.RegistThingEditor/@EditorComposite/@contentComposie/@homeComposite/@chageRegistTypeButton/@Listeners/@chageRegistTypeButtonSelection/@GroovyAction
        String newType = registTypeCombo.getText();
        if(newType != null && newType != ""){
            parentContext.g().put("type", newType);
            ActionContainer actions = parentContext.getObject("actions");
            actions.doAction("refresh", parentContext);
        }
    }
    
    
    public void initCode() throws SQLException{
        //xworker.swt.app.editors.RegistThingEditor/@EditorComposite/@contentComposie/@homeComposite/@init
        //显示事物的文档
        if(actionContext.get("thing") == null){
            return;
        }
        
        Thing thing = actionContext.getObject("thing");
        Browser homeDescBrowser = actionContext.getObject("homeDescBrowser");
        
        homeDescBrowser.setUrl(XWorkerUtils.getThingDescUrl(thing));
        
        //显示可用的注册方式
        registTypeCombo.removeAll();
        
        List<String> types = ThingUtils.getRegistTypes(thing, Collections.emptyList(), false, actionContext);
        for(String rtype : types){
            registTypeCombo.add(rtype);
        }
        if(actionContext.get("type") != null){
            registTypeCombo.setText((String) actionContext.getObject("type"));
        }
    }

    public void onOutlineCreated(){
        Thing thing = actionContext.getObject("thing");
        Browser browser = actionContext.getObject("topicBrowser");
        if(browser != null && !browser.isDisposed()){
            if(thing != null){
                SwtUtils.setThingDesc(thing, browser);
            }

            SwtQuickContentHandler contentHandler = actions.doAction("getContentHandler", actionContext);
            if(contentHandler != null){
                contentHandler.setBrowser(browser);
            }
        }
    }

    public void setContent(){
        //xworker.swt.app.editors.RegistThingEditor/@actions/@setContent
        //事物参数
    	Map<String, Object> params = actionContext.getObject("params");
        Object thing = params.get("thing");
        if(thing instanceof String){
            thing = world.getThing((String) thing);
        }
        if(thing == null){
            Executor.warn(TAG, "RegistThingEdtior: thing is null");
            return;
        }
        actionContext.g().put("thing", thing);
        
        //编辑器
        if("ReferenceIndex".equals(((Thing) thing).getThingName())){
            Thing data = world.getThing(((Thing) thing).getString("thingPath"));
            if(thing != null){
                thing = data;
            }
        }
        //log.info("thing=" + data);  
        Thing viewer = (Thing) thing;
        if(!UtilData.isTrue(actionContext.get("forceUseDefaultOpenMethod"))){
            viewer = getViewer((Thing) thing);
        }
        //log.info("viewer = " + viewer);
        String contentDefaultOpenMethod = null;
        if(viewer != null || (contentDefaultOpenMethod != null 
            && !"".equals(contentDefaultOpenMethod)
            && !"null".equals(contentDefaultOpenMethod) 
            && !"thingDescBrowser".equals(contentDefaultOpenMethod))){
            //显示Composite
            actions.doAction("openCompositeTab", actionContext, "thing", thing, "viewer", viewer,  "acContext", actionContext);   
        }else{            
            //显示事物的文档
            actions.doAction("openBrowserTab", actionContext, "thing", thing, "acContext", actionContext);            
        }

        onOutlineCreated();
        
    }
    
    public Thing getViewer(Thing thing){
        //首先取自己的
        //println thing;
        Thing viewer = getViewer_(thing, thing.getStringBlankAsNull("th_thingRegistViewer"));
        
        if(viewer == null){
            for(Thing desc : thing.getAllDescriptors()){
                //println desc;
                viewer = getViewer_(thing, desc.getStringBlankAsNull("th_thingRegistViewer"));
                if(viewer != null){
                    return viewer;
                }
            }
        }
    
        if(viewer == null){
            for(Thing extd : thing.getAllExtends()){
                // println "exted:" extd;
                viewer = getViewer_(thing, extd.getStringBlankAsNull("th_thingRegistViewer"));
                if(viewer != null){
                    return viewer;
                }
            }
        }
        
        return viewer;
    }
    
    public Thing getViewer_(Thing thing, String path){
        if(path == null){
            return null;
        }else if(path == "self"){
            return thing;
        }else{
            return world.getThing(path);
        }
    }
    
    public void openBrowserTab(){
        //xworker.swt.app.editors.RegistThingEditor/@actions/@openBrowserTab
        //TabFolder的显示方式
        //println(actionContext.get("contentComposite"));
    	Thing thing = actionContext.getObject("thing");
    	CTabFolder contentTabFolder = actionContext.getObject("contentTabFolder");
    	
        if(actionContext.get("contentTabFolder") != null){    
            //tabItem的key，一个事物只打开一个tabItem
            String key = thing.getMetadata().getPath() + "_TabItem";
            
            //选中TabItem，如果已经存在
            for(CTabItem tabItem : contentTabFolder.getItems()){
                if(key.equals(tabItem.getData("tabKey"))){
                    contentTabFolder.setSelection(tabItem);
                    return;
                }
            }
            
            //创建新的TabItem
            ActionContext ac = new ActionContext();
            ac.put("parent", contentTabFolder);
            ac.put("parentContext", actionContext);
            
            Thing tabItemThing = world.getThing("xworker.swt.xworker.prototype.ThingRegistSelector/@mainTabFolder/@CTabItem");
            CTabItem tabItem = tabItemThing.doAction("create", ac);
            tabItem.setText(thing.getMetadata().getLabel());
            
            Thing compositeThing = world.getThing("xworker.swt.xworker.prototype.ThingRegistSelector/@mainTabFolder/@searchDescRightCompositeITem/@mainSashForm/@contentComposie/@descComposite");
            ac.peek().put("parent", contentTabFolder);
            Composite composite = compositeThing.doAction("create", ac);
            tabItem.setControl(composite);
            tabItem.setData("tabKey", key);
            
            //设置属性                        
            descBrowser.setUrl(XWorkerUtils.getThingDescUrl(thing));
            contentTabFolder.setSelection(tabItem);
        }else if(actionContext.get("contentComposite") != null){
            //Composite的显示方式
            descBrowser.setUrl(XWorkerUtils.getThingDescUrl(thing));
            StackLayout contentCompositeStackLayout = actionContext.getObject("contentCompositeStackLayout");
            Composite descComposite = actionContext.getObject("descComposite");
            
            contentCompositeStackLayout.topControl = descComposite;
            contentComposite.layout();
            //contentComposite.getParent().layout();
        }
    }
    
    public void openCompositeTab(){
        //xworker.swt.app.editors.RegistThingEditor/@actions/@openCompositeTab
        Thing swtThing = actionContext.getObject("viewer");
        Thing thing = actionContext.getObject("thing");
        CTabFolder contentTabFolder = actionContext.getObject("contentTabFolder");
        
        String contentDefaultOpenMethod = actionContext.getObject("contentDefaultOpenMethod");
        if(swtThing == null){
            //使用传入的默认打开方式
            swtThing = new Thing("xworker.swt.xworker.ThingRegistThing");
            swtThing.set("type", contentDefaultOpenMethod);
            String path = thing.getMetadata().getPath();
            if(contentDefaultOpenMethod == "thingRegist"){
                path = "child|" + path;
            }
            swtThing.set("path", path);
        }
        
        if(actionContext.get("contentTabFolder") != null){    
            //TabFolder的显示方式
            //tabItem的key，一个事物只打开一个tabItem
            String key = thing.getMetadata().getPath() + "_TabItem";
            
            //选中TabItem，如果已经存在
            for(CTabItem tabItem : contentTabFolder.getItems()){
                if(tabItem.getData("tabKey") == key){
                    contentTabFolder.setSelection(tabItem);
                    return;
                }
            }
            
            //创建新的TabItem
            ActionContext ac = new ActionContext();
            ac.put("parent", contentTabFolder);
            ac.put("parentContext", actionContext.get("acContext"));
            ac.put("thing", thing);
            Composite composite = swtThing.doAction("create", ac);
            //log.info("" + swtThing);
            
            ac.peek().put("parent", contentTabFolder);
            Thing tabItemThing = world.getThing("xworker.swt.xworker.prototype.ThingRegistSelector/@mainTabFolder/@CTabItem");
            CTabItem tabItem = tabItemThing.doAction("create", ac);
            tabItem.setText(thing.getMetadata().getLabel());
            
            //println thing.getMetadata().getPath();
            
            tabItem.setControl(composite);
            tabItem.setData("tabKey", key);
            tabItem.setData("actionContext", ac);
            
            contentTabFolder.setSelection(tabItem);
        }else if(actionContext.get("contentComposite") != null){
            //Composite的显示方式
            //清空原有节点
            for(Control child : conComposite.getChildren()){
                child.dispose();
            }
            
            ActionContext ac = new ActionContext();
            ac.put("parent", conComposite);
            ac.put("parentContext", actionContext.get("acContext"));
            ac.put("thing", thing);
            swtThing.doAction("create", ac);
            conComposite.setData("actionContext", ac);
            conComposite.layout();
            
            contentCompositeStackLayout.topControl = conComposite;
            contentComposite.layout();
            //contentComposite.getParent().layout();
            //println "xxxxxx";
        }
        
        
    }

    public boolean isCompoisteThing(Thing data){
        if((data.isThing("xworker.swt.widgets.Composite") && !data.isThing("xworker.swt.widgets.Shell"))
            || data.isThing("xworker.swt.xworker.ThingRegistComposite")
            || data.isThing("xworker.swt.xworker.ThingRegistThing")){
             return true;
        }else{
            return false;
        }
    }
    
    public Object isSameContent(){
        //xworker.swt.app.editors.RegistThingEditor/@actions/@isSameContent
    	Map<String, Object> params = actionContext.getObject("params");
    	
        Object thing = params.get("thing");
        if(thing instanceof String){
            thing = world.getThing((String) thing);
        }
        
        return thing == actionContext.get("thing");
    }
    
    public Object getTitle(){
        //xworker.swt.app.editors.RegistThingEditor/@actions/@getTitle
    	Thing thing = actionContext.getObject("thing");
        if(actionContext.get("thing") != null){
            return thing.getMetadata().getPath();
        }else{
            return "No Regit Thing";
        }
    }
    
    public Object getSimpleTitle(){
        //xworker.swt.app.editors.RegistThingEditor/@actions/@getSimpleTitle
    	Thing thing = actionContext.getObject("thing");
        if(actionContext.get("thing") != null){
            return thing.getMetadata().getLabel();
        }else{
            return "No Regit Thing";
        }
    }
    

    public static RegistThingEditor createInstance(ActionContext actionContext){
        //return new MyClass();    
        String key = RegistThingEditor.class.getName();
        RegistThingEditor obj = actionContext.getObject(key);
        if(obj == null){
            obj = new RegistThingEditor();
            actionContext.g().put(key, obj);
        }
        
        return obj;
    }    
        
    @ActionField
    public ActionContext actionContext;
    
    public World world = World.getInstance();
    
    @ActionField
    public org.eclipse.swt.custom.CCombo registTypeCombo;
    
    @ActionField
    public ActionContext parentContext;
    
    @ActionField
    public ActionContainer actions;
    
    @ActionField
    public org.eclipse.swt.widgets.Composite conComposite;
        
    @ActionField
    public org.eclipse.swt.widgets.Composite contentComposite;
        
    @ActionField
    public org.eclipse.swt.custom.StackLayout contentCompositeStackLayout;
        
    @ActionField
    public org.eclipse.swt.browser.Browser descBrowser;
        
    @ActionField
    public org.eclipse.swt.widgets.Composite descComposite;
}
