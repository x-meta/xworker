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

    public InputSelectEditor(ThingForm thingForm, Thing attribute) {
        super(thingForm, attribute);
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
        Thing thing = null;
        if(value instanceof Thing){
            thing = (Thing) value;
        }else if(value instanceof  String) {
            thing = converter.fromString((String) value);
        }
        if(thing == null){
            thing = converter.fromString(null);

        }

        comboBox.setValue(thing);
        if(thing.getStringBlankAsNull("value") == null){
            comboBox.getEditor().setText(" ");
        }else{
            comboBox.getEditor().setText(thing.getMetadata().getLabel());
        }
    }

    @Override
    public Object getValue() {
        Thing thing = comboBox.getSelectionModel().getSelectedItem();
        if(thing != null){
            return thing.get("value");
        }else {
            return comboBox.getEditor().getText().trim();
        }
    }
}
