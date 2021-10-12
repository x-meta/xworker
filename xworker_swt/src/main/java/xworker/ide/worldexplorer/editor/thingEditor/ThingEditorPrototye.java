package xworker.ide.worldexplorer.editor.thingEditor;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionField;
import xworker.lang.executor.Executor;
import xworker.swt.ActionContainer;
import xworker.swt.events.SwtListener;
import xworker.util.Callback;
import xworker.util.UtilData;

import java.util.Map;

public class ThingEditorPrototye {
    @ActionField
    public org.eclipse.swt.widgets.Composite blankComposite;
    @ActionField
    public org.eclipse.swt.widgets.Composite contentComposite;
    @ActionField
    public org.xmeta.util.ActionContainer editorActions;
    @ActionField
    public org.eclipse.swt.widgets.Composite mainComposite;
    @ActionField
    public org.eclipse.swt.custom.StackLayout mainStackLayout;
    @ActionField
    public ActionContext thingContext;

    public void setThing(ActionContext actionContext){
        //输入参数是thing，如果没有提示错误并返回
        if(actionContext.get("thing") == null){
            mainStackLayout.topControl = blankComposite;
            mainComposite.layout();
            return;
            //thing = world.getThing("xworker.ide.worldexplorer.ThingEditor");
        }

        //如果有编辑状态改为没有编辑
        ActionContainer actions = thingContext.getObject("actions");
        actions.doAction("modify", actionContext, "setModified", false);

        Thing thing = actionContext.getObject("thing");
        if(UtilData.isTrue(actionContext.get("editorThingUseRootThing"))){
            thingContext.put("thing", thing.getRoot());
            thingContext.put("rootThing", thing.getRoot());
            actions.doAction("setThing", actionContext,"thing", thing.getRoot());
            mainComposite.getDisplay().asyncExec(() -> {
                    actions.doAction("selectThing", actionContext ,"thing", thing);
            });
        }else{
            thingContext.put("thing", thing);
            thingContext.put("rootThing", thing);
            actions.doAction("setThing", actionContext, "thing", thing);
        }

        mainStackLayout.topControl = contentComposite;
        mainComposite.layout();
    }

    public Object getThing(){
        //先保存
        SwtListener okButtonSelection = thingContext.getObject("okButtonSelection");
        okButtonSelection.handleEvent(null);
        return thingContext.get("thing");
    }

    public Object save(){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("save");
    }

    public Object selectThing(ActionContext actionContext){
        Object th = actionContext.getObject("thing");
        Thing thing = null;
        if(th instanceof String){
            thing = World.getInstance().getThing((String) th);
        }else{
            thing = (Thing) th;
        }

        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("selectThing", actionContext, "thing", thing);
    }

    public Object refresh(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("refreshOutline", actionContext, "refreshThing", actionContext.get("refreshThing"));
    }

    public Object openThing(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("openThing", actionContext,  "thing", actionContext.get("thing"));
    }

    public Object getRootThing(ActionContext actionContext){
        //先保存
        SwtListener okButtonSelection = thingContext.getObject("okButtonSelection");
        okButtonSelection.handleEvent(null);

        return thingContext.get("rootThing");
    }

    public void setVariablesActionContext(ActionContext actionContext){
        actionContext.g().put("variablesActionContext", actionContext.get("variablesActionContext"));
    }

    public boolean isModified(){
        ActionContainer actions = thingContext.getObject("actions");
        ActionContext ac = actions.getActionContext();
        return ac.getBoolean("modified");
    }

    public Object nodeMoveUp(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("nodeMoveUp", actionContext);
    }

    public Object nodeMoveDown(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("nodeMoveDown", actionContext);
    }

    public Object showAddChild(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("showAddChild", actionContext);
    }

    public Object getCurrentAttribute(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("getCurrentAttribute", actionContext);
    }

    public Object setAddChildDescriptor(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("setAddChildDescriptor", actionContext, "descriptor", actionContext.get("descriptor"));
    }

    public Object setAddChildValues(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("setAddChildValues", actionContext, "values", actionContext.getObject("values"));
    }

    public Object doAddChild(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("doAddChild", actionContext);
    }

    public Object setValues(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("setValues", actionContext, "values", actionContext.getObject("values"));
    }

    public Object selectChildTreeNode(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("selectChildTreeNode", actionContext, "thingPath", actionContext.getObject("thingPath"));
    }

    public Object getCurrentThing(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("getCurrentThing", actionContext);
    }

    public Object showXmlEditor(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("showXmlEditor", actionContext);
    }

    public Object showGuideEditor(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("showGuideEditor", actionContext);
    }

    public Object showFormEditor(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("showFormEditor", actionContext);
    }

    public Object isAddChild(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("isAddChild", actionContext);
    }

    public Object isFormEditor(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("isFormEditor", actionContext);
    }

    public Object isXMLEditor(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("isXMLEditor", actionContext);
    }

    public Object isGuideEditor(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("isGuideEditor", actionContext);
    }

    public Object getAddChildSelectedDescriptor(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("getAddChildSelectedDescriptor", actionContext);
    }

    public Object selectDescriptor(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("selectDescriptor", actionContext, "descriptor", actionContext.getObject("descriptor"));
    }

    public Object selectNode(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("selectNode", actionContext);
    }

    public void doAction(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        final String actionName = actionContext.getObject("actionName");
        final Map<String, Object> params = actionContext.getObject("params");
        final Callback<Object, Void> callback = actionContext.getObject("callback");

        if(mainComposite != null && !mainComposite.isDisposed()){
            mainComposite.getDisplay().asyncExec(()->{
                try {
                    Object result = actions.doAction(actionName, params);
                    if(callback != null){
                        callback.call(result);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        }
    }

    public Object getValues(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("getValues", actionContext);
    }

    public void initOutlineBrowser(){
    }
}
