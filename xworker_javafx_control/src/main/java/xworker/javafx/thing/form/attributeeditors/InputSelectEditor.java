package xworker.javafx.thing.form.attributeeditors;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.util.StringConverter;
import org.xmeta.Thing;
import xworker.javafx.thing.form.AttributeEditor;
import xworker.javafx.thing.form.ThingForm;
import xworker.javafx.util.ThingValueStringConverter;

import java.util.List;

public class InputSelectEditor extends AttributeEditor {
    ComboBox<Thing> comboBox = null;
    List<Thing> values;
    StringConverter<Thing> converter = null;

    public InputSelectEditor(ThingForm thingForm, Thing attribute, Property<Object> valueProperty) {
        super(thingForm, attribute, valueProperty);
        values = attribute.getAllChilds("value");
        converter = new ThingValueStringConverter(values);
    }

    @Override
    public Node createEditor() {
        comboBox = new ComboBox<>();
        comboBox.setEditable(true);
        comboBox.setConverter(converter);
        comboBox.getItems().addAll(values);

        comboBox.selectionModelProperty().addListener(new ChangeListener<SingleSelectionModel<Thing>>() {
            @Override
            public void changed(ObservableValue<? extends SingleSelectionModel<Thing>> observable, SingleSelectionModel<Thing> oldValue, SingleSelectionModel<Thing> newValue) {
                thingForm.modified(InputSelectEditor.this);
            }
        });

        return comboBox;
    }

    @Override
    public void setValue(Object value) {
        if(value instanceof Thing){
            comboBox.setValue((Thing) value);
        }else if(value instanceof  String) {
            comboBox.setValue(converter.fromString((String) value));
        }else{
            comboBox.setValue(null);
        }
    }

    @Override
    public Object getValue() {
        Thing thing = comboBox.getSelectionModel().getSelectedItem();
        if(thing != null){
            return thing.get("value");
        }else {
            return comboBox.getEditor().getText();
        }
    }
}
