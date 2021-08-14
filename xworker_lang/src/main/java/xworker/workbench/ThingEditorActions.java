package xworker.workbench;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.lang.executor.Executor;
import xworker.thingeditor.ThingEditorUtils;
import xworker.util.Callback;

import java.util.Map;

public class ThingEditorActions {
    private static final String TAG = ThingEditorActions.class.getName();

    public static void selectNode(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing.getRoot());
        ThingEditorUtils.selectNode(editor, thing, new ThingEdiorCallback(self, actionContext));
    }

    public static void save(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.save(editor, new ThingEdiorCallback(self, actionContext));
    }

    public static void refreshOutline(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.refreshOutline(editor, new ThingEdiorCallback(self, actionContext));
    }

    public static void showAddChild(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.showAddChild(editor, new ThingEdiorCallback(self, actionContext));
    }

    public static void setAddChildDescriptor(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);
        Thing descriptor = self.doAction("getDescriptor", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.setAddChildDescriptor(editor, descriptor, new ThingEdiorCallback(self, actionContext));
    }

    public static void setAddChildValues(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);
        Map<String, Object> values = self.doAction("getValues", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.setAddChildValues(editor, values, new ThingEdiorCallback(self, actionContext));
    }

    public static void doAddChild(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.doAddChild(editor, new ThingEdiorCallback(self, actionContext));
    }

    public static void focusAttributeInput(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);
        String attributeName = self.doAction("getAttributeName", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.focusAttributeInput(editor, attributeName, new ThingEdiorCallback(self, actionContext));
    }

    public static void setValues(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);
        Map<String, Object> values = self.doAction("getValues", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.setValues(editor, values, new ThingEdiorCallback(self, actionContext));
    }

    public static void showFormEditor(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.showFormEditor(editor, new ThingEdiorCallback(self, actionContext));
    }

    public static void selectChildTreeNode(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);
        Thing descriptor = self.doAction("getDescriptor", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.setAddChildDescriptor(editor, descriptor, new ThingEdiorCallback(self, actionContext));
    }

    public static void isXmlEditor(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.isXmlEditor(editor, new ThingEdiorCallback(self, actionContext));
    }

    public static void nodeMoveUp(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.nodeMoveUp(editor, new ThingEdiorCallback(self, actionContext));
    }

    public static void nodeMoveDown(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.nodeMoveDown(editor, new ThingEdiorCallback(self, actionContext));
    }

    public static void getCurrentThing(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.getCurrentThing(editor, new ThingEdiorCallback(self, actionContext));
    }

    public static void refreshRoot(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.refreshRoot(editor, new ThingEdiorCallback(self, actionContext));
    }

    public static void showXmlEditor(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.showXmlEditor(editor, new ThingEdiorCallback(self, actionContext));
    }

    public static void showGuideEditor(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.showGuideEditor(editor, new ThingEdiorCallback(self, actionContext));
    }

    public static void nodeDelete(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.nodeDelete(editor, new ThingEdiorCallback(self, actionContext));
    }

    public static void selectDescriptor(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);
        Thing descriptor = self.doAction("getDescriptor", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.selectDescriptor(editor, descriptor, new ThingEdiorCallback(self, actionContext));
    }

    public static void isFormEditor(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.isFormEditor(editor, new ThingEdiorCallback(self, actionContext));
    }

    public static void isAddChild(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.isAddChild(editor, new ThingEdiorCallback(self, actionContext));
    }

    public static void isGuideEditor(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.isGuideEditor(editor, new ThingEdiorCallback(self, actionContext));
    }

    public static void getAddChildSelectedDescriptor(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.getAddChildSelectedDescriptor(editor, new ThingEdiorCallback(self, actionContext));
    }

    public static void getValues(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing thing = self.doAction("getThing", actionContext);

        IEditor<?,?,?> editor = ThingEditorUtils.getThingEditor(thing);
        ThingEditorUtils.getValues(editor, new ThingEdiorCallback(self, actionContext));
    }

    public static void onSuccess(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Executor.info(TAG, "action: " + self.getMetadata().getName()  + ", result=" + actionContext.getObject("result"));
    }

    static class ThingEdiorCallback implements Callback<Object, Void>{
        Thing self;
        ActionContext actionContext;

        public ThingEdiorCallback(Thing self, ActionContext actionContext){
            this.self = self;
            this.actionContext = actionContext;
        }

        @Override
        public Void call(Object o) {
            try {
                self.doAction("onSuccess", actionContext, "result", o);
            }catch(Exception e){
                Executor.warn(TAG, "ThingEditor callback error, thing=" + self.getMetadata().getPath(), e);
            }

            return null;
        }
    }
}
