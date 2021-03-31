package xworker.javafx.thing.form.attributeeditors;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import org.xmeta.Thing;
import xworker.javafx.thing.form.AttributeEditor;
import xworker.javafx.thing.form.ThingForm;
import xworker.util.UtilData;

public class TrueFalseEditor extends AttributeEditor {
    CheckBox checkBox;

    public TrueFalseEditor(ThingForm thingForm, Thing attribute) {
        super(thingForm, attribute);
    }

    @Override
    public Node createEditor() {
        checkBox = new CheckBox();
        checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                thingForm.modified(TrueFalseEditor.this);
            }
        });
        return checkBox;
    }

    @Override
    public void setValue(Object value) {
        if(UtilData.getBoolean(value, false)){
            checkBox.setSelected(true);
        }else{
            checkBox.setSelected(false);
        }
    }

    @Override
    public Object getValue() {
        return checkBox.isSelected();
    }
}
