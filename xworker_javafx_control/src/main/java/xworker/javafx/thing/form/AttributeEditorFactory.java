package xworker.javafx.thing.form;

import javafx.beans.property.Property;
import org.xmeta.Thing;
import xworker.javafx.thing.form.attributeeditors.*;

public class AttributeEditorFactory {
    public static AttributeEditor createAttributeEditor(ThingForm thingForm, Thing attribute, Property<Object> property) {
        String inputType = attribute.getString("inputtype");
        if("text".equals(inputType)) {
            return new TextFieldEditor(thingForm, attribute, property);
        }else if("textarea".equals(inputType)) {
            return new TextAreaEditor(thingForm, attribute, property);
        }else if("codeEditor".equals(inputType)) {
            return new TextAreaEditor(thingForm, attribute, property);
        }else if("html".equals(inputType)) {
            return new HtmlEditor(thingForm, attribute, property);
        }else if("htmlDesc".equals(inputType)) {
            return new HtmlDescEditor(thingForm, attribute, property);
        }else if("select".equals(inputType)) {
            return new ChoiceEditor(thingForm, attribute, property);
        }else if("truefalse".equals(inputType)) {
            return new TrueFalseEditor(thingForm, attribute, property);
        }else if("truefalseselect".equals(inputType)) {
            return new TrueFalseSelectorEditor(thingForm, attribute, property);
        }else if("inputSelect".equals(inputType)) {
            return new InputSelectEditor(thingForm, attribute, property);
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
            return new TextFieldEditor(thingForm, attribute, property);
        }
    }
}
