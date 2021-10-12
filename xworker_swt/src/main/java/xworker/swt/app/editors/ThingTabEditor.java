package xworker.swt.app.editors;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Control;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;

import xworker.swt.app.IEditor;

@ActionClass(creator="createInstance")
public class ThingTabEditor {
    public static Map<String, Object> createDataParams(ActionContext actionContext) throws IOException {
        Object data = actionContext.get("data");
        if (data instanceof Thing) {
            Thing thing = (Thing) data;
            Map<String, Object> params = new HashMap<>();
            params.put(IEditor.EDITOR_THING, World.getInstance().getThing("xworker.swt.app.editors.ThingTabEditor"));
            params.put("thing", data);
            params.put(IEditor.EDITOR_ID, "Thing:" + thing.getMetadata().getPath());
            return params;
        }

        return null;
    }

    public void modify() {
        //xworker.swt.app.editors.ThingEditor/@EditorComposite/@thingEditor/@actions/@modify
        if (actionContext.get("actions") == null) {
            //println "actions is null";
            return;
        }

        Thing editorThing = actionContext.getObject("editorThing");
        IEditor editor = actionContext.getObject("editor");

        editorThing.doAction("editorChanged", actionContext, "editorActions", actions);

        editor.fireStateChanged();
    }

    public void onOutlineCreated(){
        Thing thing = actionContext.getObject("thing");
        if(thing != null) {
            actionContext.peek().put("parent", outlineComposite);
            thing.doAction("createThingTabOutline", actionContext);
        }
    }


    public void setContent() {
        //xworker.swt.app.editors.ThingEditor/@ActionContainer/@setContent
        Map<String, Object> params = actionContext.getObject("params");
        Action noParams = actionContext.getObject("noParams");

        //判断参数是否存在
        if (params == null) {
            noParams.run(actionContext);
            return;
        }

        Object thingObj = params.get("thing");
        Thing thing = null;
        if (thingObj instanceof String) {
            thing = world.getThing((String) thingObj);
        } else {
            thing = (Thing) thingObj;
        }

        for(Control child : editorComposite.getChildren()){
            child.dispose();
        }
        for(Control child : outlineComposite.getChildren()){
            child.dispose();
        }

        actionContext.g().put("thing", thing);
        actionContext.peek().put("parent", outlineComposite);
        thing.doAction("createThingTabOutline", actionContext);

        actionContext.peek().put("parent", editorComposite);
        thing.doAction("createThingTab", actionContext);

    }

    public boolean isSame(Thing oldThing, Thing newThing) {
        if (oldThing == null || newThing == null) {
            return false;
        }

        String oldPath = oldThing.getRoot().getMetadata().getPath();
        String newPath = newThing.getRoot().getMetadata().getPath();

        return oldPath.equals(newPath);
    }

    public Object isSameContent() {
        //xworker.swt.app.editors.ThingEditor/@ActionContainer/@isSameContent
        return false;
        /*
        if(actionContext.get("thing") == null){
            return false;
        }

        if(actionContext.get("params") == null){
            return false;
        }

        def t = params.thing;
        if(t instanceof String){
            t = world.getThing(t);
        }
        if(t != null && thing.getRoot() == t.getRoot()){
            return true;
        }else{
            return false;
        }*/
    }

    public Object getSimpleTitle() {
        //xworker.swt.app.editors.ThingEditor/@ActionContainer/@getSimpleTitle
        Thing thing = actionContext.getObject("thing");

        if (actionContext.get("thing") != null) {
            return thing.getRoot().getMetadata().getLabel();
        } else {
            return "ThingEditor";
        }
    }

    public Object getTitle() {
        //xworker.swt.app.editors.ThingEditor/@ActionContainer/@getTitle
        Thing thing = actionContext.getObject("thing");
        if (actionContext.get("thing") != null) {
            ThingManager thingManager = thing.getMetadata().getThingManager();
            if (thingManager != null) {
                return thingManager.getName() + "/" + thing.getRoot().getMetadata().getPath();
            } else {
                return thing.getRoot().getMetadata().getPath();
            }
        } else {
            return "null/ThingEditor";
        }
    }

    public void doDispose() {
        //xworker.swt.app.editors.ThingEditor/@ActionContainer/@doDispose
        if (editorComposite.isDisposed() == false) {
            editorComposite.dispose();
        }
    }

    public void doAction(ActionContext actionContext) {
        /*
        Map<String, Object> params = new HashMap<>();
        params.put("actionName", actionContext.getObject("actionName"));
        params.put("params", actionContext.getObject("params"));
        params.put("callback", actionContext.getObject("callback"));

        thingEditor.doAction("doAction", actionContext, params);
        */
    }

    public static ThingTabEditor createInstance(ActionContext actionContext) {
        //return new MyClass();
        String key = ThingEditor.class.getName();
        ThingTabEditor obj = actionContext.getObject(key);
        if (obj == null) {
            obj = new ThingTabEditor();
            actionContext.g().put(key, obj);
        }

        return obj;
    }

    @ActionField
    public ActionContext actionContext;

    public World world = World.getInstance();

    @ActionField
    public org.xmeta.util.ActionContainer actions;

    @ActionField
    public org.eclipse.swt.widgets.Composite editorComposite;

    @ActionField
    public org.eclipse.swt.widgets.Composite outlineComposite;
}