package xworker.javafx.thing.form;

import javafx.beans.property.Property;
import org.xmeta.Thing;
import xworker.dataObject.DataStore;
import xworker.javafx.thing.form.attributeeditors.*;

public class AttributeEditorFactory {
    public static AttributeEditor createAttributeEditor(ThingForm thingForm, Thing attribute) {
        String inputType = attribute.getString("inputtype");
        if("text".equals(inputType)) {
            return new TextFieldEditor(thingForm, attribute);
        }else if("textarea".equals(inputType)) {
            return new TextAreaEditor(thingForm, attribute);
        }else if("codeEditor".equals(inputType)) {
            return new TextAreaEditor(thingForm, attribute);
        }else if("html".equals(inputType)) {
            return new HtmlEditor(thingForm, attribute);
        }else if("htmlDesc".equals(inputType)) {
            return new HtmlDescEditor(thingForm, attribute);
        }else if("select".equals(inputType) || "radio".equals(inputType)) {
            DataStore dataStore = AttributeDataStore.getDataStore(attribute, thingForm.getActionContext());
            if(dataStore == null) {
                return new ChoiceEditor(thingForm, attribute);
            }else{
                return new ChoiceDataStoreEditor(thingForm, attribute, dataStore);
            }
        }else if("multSelect".equals(inputType) || "checkBox".equals(inputType)){
            DataStore dataStore = AttributeDataStore.getDataStore(attribute, thingForm.getActionContext());
            if(dataStore == null) {
                return new MultiComboEditor(thingForm, attribute);
            }else{
                return new MultiComboDataStoreEditor(thingForm, attribute, dataStore);
            }
        }else if("truefalse".equals(inputType)) {
            return new TrueFalseEditor(thingForm, attribute);
        }else if("truefalseselect".equals(inputType)) {
            return new TrueFalseSelectorEditor(thingForm, attribute);
        }else if("inputSelect".equals(inputType)) {
            DataStore dataStore = AttributeDataStore.getDataStore(attribute, thingForm.getActionContext());
            if(dataStore == null) {
                return new InputSelectEditor(thingForm, attribute);
            }else{
                return new InputSelectDataStoreEditor(thingForm, attribute, dataStore);
            }
        }else if("datePick".equals(inputType)){
            return new DatePickEditor(thingForm, attribute);
        }else if("colorpicker".equals(inputType)) {
            return new ColorEditor(thingForm, attribute);
        }/*else if("radio".equals(inputType)) {
            return new RadioAttributeEditor(formThing, attribute, gridData, actionContext);
        }else if("checkBox".equals(inputType)) {
            return new CheckBoxAttributeEditor(formThing, attribute, gridData, actionContext);
        }else if("dataSelector".equals(inputType)) {
            return new DataSelectorAttributeEditor(formThing, attribute, gridData, actionContext);
        }else if("pathSelector".equals(inputType)) {
            return new PathSelectorAttributeEditor(formThing, attribute, gridData, actionContext);
        }else if("colorpicker".equals(inputType)) {
            return new ColorpickerAttributeEditor(formThing, attribute, gridData, actionContext);
        }else if("fontSelect".equals(inputType)) {
            return new FontSelectAttributeEditor(formThing, attribute, gridData, actionContext);
        }else if("image".equals(inputType)){
            return new ImageAttributeEditor(formThing, attribute, gridData, actionContext);
        }else if("__subGroup__editor__".equals(inputType)){
            return new SubGroupEditor(formThing, attribute, gridData, actionContext);
        }*/else {
            return new TextFieldEditor(thingForm, attribute);
        }
    }
}
