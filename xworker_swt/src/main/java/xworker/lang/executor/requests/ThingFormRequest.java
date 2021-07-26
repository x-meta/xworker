package xworker.lang.executor.requests;

import org.xmeta.Thing;
import org.xmeta.annotation.ActionField;

import java.util.Map;

public class ThingFormRequest {
    @ActionField
    public org.xmeta.util.ActionContainer actions;

    @ActionField
    public org.xmeta.Thing thingForm;

    @ActionField
    public Boolean validate;

    @ActionField
    public String thingType;

    @ActionField
    public Thing thing;

    xworker.swt.xworker.ThingForm form;

    public void init(){
        form = xworker.swt.xworker.ThingForm.getThingForm(thingForm);

        if(thing != null){
            if("thing".equals(thingType)){
                form.setThing(thing);
            }else{
                form.setDescriptor(thing);
            }
        }
    }

    public boolean validate(){
        return form.validate();
    }

    public Map<String, Object> getValues(){
        return form.getValues();
    }

    public void setDescriptor(Thing descriptor){
        form.setDescriptor(descriptor);
    }

    public void setThing(Thing thing){
        form.setThing(thing);
    }
}
