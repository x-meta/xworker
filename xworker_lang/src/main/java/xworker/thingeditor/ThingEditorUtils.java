package xworker.thingeditor;

import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;
import xworker.util.Callback;
import xworker.util.XWorkerUtils;
import xworker.workbench.IEditor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ThingEditorUtils {
    public static IEditor<?, ?, ?> getThingEditor(Thing thing){
        return getThingEditor(thing.getRoot().getMetadata().getPath());
    }

    public static IEditor<?, ?, ?> getThingEditor(String thing){
        return Objects.requireNonNull(XWorkerUtils.getWorkbench()).getEditor("thing:" + thing);
    }

    public static void selectNode(IEditor<?,?,?> thingEditor, Thing thing, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        Map<String, Object> params = new HashMap<>();
        params.put("thing", thing);
        params.put("refresh", false);
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "selectNode", "params", params, "callback", callback);
    }

    public static void save(IEditor<?,?,?> thingEditor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "save", "params", Collections.emptyMap(), "callback", callback);
    }

    public static void refreshOutline(IEditor<?,?,?> thingEditor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        Map<String, Object> params = new HashMap<>();
        params.put("refreshThing", null);
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "refreshOutline", "params", params, "callback", callback);
    }

    public static void showAddChild(IEditor<?,?,?> thingEditor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "showAddChild", "params", Collections.emptyMap(), "callback", callback);
    }

    public static void setAddChildDescriptor(IEditor<?,?,?> thingEditor, Thing descriptor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        Map<String, Object> params = new HashMap<>();
        params.put("descriptor", descriptor);
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "setAddChildDescriptor", "params", params, "callback", callback);
    }

    public static void setAddChildValues(IEditor<?,?,?> thingEditor, Map<String, Object> values, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        Map<String, Object> params = new HashMap<>();
        params.put("values", values);
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "setAddChildValues", "params", params, "callback", callback);
    }

    public static void doAddChild(IEditor<?,?,?> thingEditor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "doAddChild", "params", Collections.emptyMap(), "callback", callback);
    }

    public static void focusAttributeInput(IEditor<?,?,?> thingEditor, String attributeName, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        Map<String, Object> params = new HashMap<>();
        params.put("name", attributeName);
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "focusAttributeInput", "params", params, "callback", callback);
    }

    public static void setValues(IEditor<?,?,?> thingEditor, Map<String, Object> values, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        Map<String, Object> params = new HashMap<>();
        params.put("values", values);
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "setValues", "params", params, "callback", callback);
    }

    public static void showFormEditor(IEditor<?,?,?> thingEditor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "showFormEditor", "params", Collections.emptyMap(), "callback", callback);
    }

    public static void selectChildTreeNode(IEditor<?,?,?> thingEditor, String descriptor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        Map<String, Object> params = new HashMap<>();
        params.put("thingPath", descriptor);
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "selectChildTreeNode", "params", params, "callback", callback);
    }

    public static void isXmlEditor(IEditor<?,?,?> thingEditor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "isXmlEditor", "params", Collections.emptyMap(), "callback", callback);
    }

    public static void nodeMoveUp(IEditor<?,?,?> thingEditor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "nodeMoveUp", "params", Collections.emptyMap(), "callback", callback);
    }

    public static void nodeMoveDown(IEditor<?,?,?> thingEditor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "nodeMoveDown", "params", Collections.emptyMap(), "callback", callback);
    }

    public static void getCurrentThing(IEditor<?,?,?> thingEditor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "getCurrentThing", "params", Collections.emptyMap(), "callback", callback);
    }

    public static void refreshRoot(IEditor<?,?,?> thingEditor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "refreshRoot", "params", Collections.emptyMap(), "callback", callback);
    }

    public static void showXmlEditor(IEditor<?,?,?> thingEditor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "showXmlEditor", "params", Collections.emptyMap(), "callback", callback);
    }

    public static void showGuideEditor(IEditor<?,?,?> thingEditor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "showGuideEditor", "params", Collections.emptyMap(), "callback", callback);
    }

    public static void nodeDelete(IEditor<?,?,?> thingEditor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "nodeDelete", "params", Collections.emptyMap(), "callback", callback);
    }

    public static void selectDescriptor(IEditor<?,?,?> thingEditor, Thing descriptor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        Map<String, Object> params = new HashMap<>();
        params.put("descriptor", descriptor);
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "selectDescriptor", "params", params, "callback", callback);
    }

    public static void isFormEditor(IEditor<?,?,?> thingEditor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "isFormEditor", "params", Collections.emptyMap(), "callback", callback);
    }

    public static void isAddChild(IEditor<?,?,?> thingEditor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "isAddChild", "params", Collections.emptyMap(), "callback", callback);
    }

    public static void isGuideEditor(IEditor<?,?,?> thingEditor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "isGuideEditor", "params", Collections.emptyMap(), "callback", callback);
    }

    public static void getAddChildSelectedDescriptor(IEditor<?,?,?> thingEditor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "getAddChildSelectedDescriptor", "params", Collections.emptyMap(), "callback", callback);
    }

    public static void getValues(IEditor<?,?,?> thingEditor, Callback<Object, Void> callback){
        ActionContainer actions = thingEditor.getActions();
        actions.doAction("doAction", actions.getActionContext(),
                "actionName", "getValues", "params", Collections.emptyMap(), "callback", callback);
    }
}
