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

    public void save(){
        ActionContainer actions = thingContext.getObject("actions");
        actions.doAction("save");
    }

    public void selectThing(ActionContext actionContext){
        Object th = actionContext.getObject("thing");
        Thing thing = null;
        if(th instanceof String){
            thing = World.getInstance().getThing((String) th);
        }else{
            thing = (Thing) th;
        }

        ActionContainer actions = thingContext.getObject("actions");
        actions.doAction("selectThing", actionContext, "thing", thing);
    }

    public void refresh(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        actions.doAction("refreshOutline", actionContext, "refreshThing", actionContext.get("refreshThing"));
    }

    public void openThing(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        actions.doAction("openThing", actionContext,  "thing", actionContext.get("thing"));
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

    public void nodeMoveUp(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        actions.doAction("nodeMoveUp", actionContext);
    }

    public void nodeMoveDown(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        actions.doAction("nodeMoveDown", actionContext);
    }

    public void showAddChild(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        actions.doAction("showAddChild", actionContext);
    }

    public Object getCurrentAttribute(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        return actions.doAction("getCurrentAttribute", actionContext);
    }

    public void setAddChildDescriptor(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        actions.doAction("setAddChildDescriptor", actionContext, "descriptor", actionContext.get("descriptor"));
    }

    public void setAddChildValues(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        actions.doAction("setAddChildValues", actionContext, "values", actionContext.getObject("values"));
    }

    public void doAddChild(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        actions.doAction("doAddChild", actionContext);
    }

    public void setValues(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        actions.doAction("setValues", actionContext, "values", actionContext.getObject("values"));
    }

    public void selectChildTreeNode(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        actions.doAction("selectChildTreeNode", actionContext, "thingPath", actionContext.getObject("thingPath"));
    }

    public void getCurrentThing(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        actions.doAction("getCurrentThing", actionContext);
    }

    public void showXmlEditor(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        actions.doAction("showXmlEditor", actionContext);
    }

    public void showGuideEditor(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        actions.doAction("showGuideEditor", actionContext);
    }

    public void showFormEditor(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        actions.doAction("showFormEditor", actionContext);
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

    public void selectDescriptor(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        actions.doAction("selectDescriptor", actionContext, "descriptor", actionContext.getObject("descriptor"));
    }

    public void selectNode(ActionContext actionContext){
        ActionContainer actions = thingContext.getObject("actions");
        actions.doAction("selectNode", actionContext);
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

}
