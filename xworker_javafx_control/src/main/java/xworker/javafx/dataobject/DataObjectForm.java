package xworker.javafx.dataobject;

import javafx.scene.Node;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.dataObject.DataObject;
import xworker.javafx.thing.form.ThingForm;

import java.util.Map;

public class DataObjectForm {
    ThingForm thingForm = new ThingForm();
    DataObject dataObject = null;

    public DataObjectForm(){

    }

    public DataObjectForm(DataObject dataObject){
        setDataObject(dataObject);
    }

    public DataObjectForm(Thing dataObjectDescriptor){
        setDataObject(dataObjectDescriptor);
    }

    /**
     * 设置表单模型，表单模型更可以监听modify事件。
     *
     * @param formThing
     * @param actionContext
     */
    public void setFormThing(Thing formThing, ActionContext actionContext){
        thingForm.setFormThing(formThing, actionContext);
    }

    /**
     * 从表单设置值到数据对象（如果存在），返回该数据对象。
     *
     * @return
     */
    public DataObject getDataObject(){
        if(dataObject != null){
            dataObject.putAll(getValues());
        }

        return dataObject;
    }

    public void setDataObject(String descriptor){
        setDataObject(World.getInstance().getThing(descriptor));
    }

    public void setDataObject(Thing descriptor){
        DataObject dataObject = new DataObject(descriptor);
        setDataObject(dataObject);
    }

    public void setDataObject(DataObject dataObject){
        this.dataObject = dataObject;
        if(dataObject == null){
            thingForm.setDescriptor(null);
        }else {
            Thing descriptor = dataObject.getMetadata().getDescriptor();
            thingForm.setDescriptor(descriptor);

            thingForm.setValues(dataObject);
        }
    }

    public Map<String, Object> getValues(){
        return thingForm.getValues();
    }

    public void setValues(Map<String, Object> values){
        thingForm.setValues(values);
    }

    public void setPartialValues(Map<String, Object> values){
        thingForm.setPartialValues(values);
    }

    public boolean validate(){
        return thingForm.validate();
    }

    public Node getFormNode(){
        return thingForm.getFormNode();
    }

    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        DataObjectForm form = new DataObjectForm();
        form.setFormThing(self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), form);

        DataObject dataObject = self.doAction("getDataObject", actionContext);
        if(dataObject != null){
            form.setDataObject(dataObject);
        }else{
            Thing dataObjectDescriptor = self.doAction("getDataObjectDescriptor", actionContext);
            if(dataObjectDescriptor != null){
                form.setDataObject(dataObjectDescriptor);
            }
        }

        actionContext.peek().put("parent", form.getFormNode());
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
        return form.getFormNode();
    }
}
