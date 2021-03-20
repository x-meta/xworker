package xworker.javafx.thing.form.attributeeditors;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.util.StringConverter;
import org.xmeta.Thing;
import xworker.javafx.thing.form.AttributeEditor;
import xworker.javafx.thing.form.ThingForm;
import xworker.javafx.util.ThingValueStringConverter;

import java.util.List;

public class ChoiceEditor extends AttributeEditor {
    ChoiceBox<Thing> choiceBox;
    List<Thing> values;
    StringConverter<Thing> converter = null;

    public ChoiceEditor(ThingForm thingForm, Thing attribute, Property<Object> valueProperty) {
        super(thingForm, attribute, valueProperty);

        values = attribute.getAllChilds("value");
        converter = new ThingValueStringConverter(values);
    }

    @Override
    public Node createEditor() {
        choiceBox = new ChoiceBox<>();
        choiceBox.setConverter(converter);
        choiceBox.getItems().addAll(values);
        choiceBox.selectionModelProperty().addListener(new ChangeListener<SingleSelectionModel<Thing>>() {
            @Override
            public void changed(ObservableValue<? extends SingleSelectionModel<Thing>> observable, SingleSelectionModel<Thing> oldValue, SingleSelectionModel<Thing> newValue) {
                thingForm.modified(ChoiceEditor.this);
            }
        });

        return choiceBox;
    }

    @Override
    public void setValue(Object value) {
        if(value instanceof Thing){
            choiceBox.setValue((Thing) value);
        }else if(value instanceof  String) {
            choiceBox.setValue(converter.fromString((String) value));
        }else{
            choiceBox.setValue(null);
        }
    }

    @Override
    public Object getValue() {
        Thing thing = choiceBox.getSelectionModel().getSelectedItem();
        if(thing != null){
            return thing.get("value");
        }else {
            return null;
        }
    }
}
