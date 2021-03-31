package xworker.javafx.thing.form.attributeeditors;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SingleSelectionModel;
import org.xmeta.Thing;
import xworker.javafx.thing.form.AttributeEditor;
import xworker.javafx.thing.form.ThingForm;
import xworker.util.UtilData;

public class TrueFalseSelectorEditor extends AttributeEditor {
    ChoiceBox<String> choiceBox;

    public TrueFalseSelectorEditor(ThingForm thingForm, Thing attribute) {
        super(thingForm, attribute);
    }

    @Override
    public Node createEditor() {
        choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll("", "true(是)", "false(否）");
        choiceBox.selectionModelProperty().addListener(new ChangeListener<SingleSelectionModel<String>>() {
            @Override
            public void changed(ObservableValue<? extends SingleSelectionModel<String>> observable, SingleSelectionModel<String> oldValue, SingleSelectionModel<String> newValue) {
                thingForm.modified(TrueFalseSelectorEditor.this);
            }
        });
        return choiceBox;
    }

    @Override
    public void setValue(Object value) {
        if(value == null){
            choiceBox.getSelectionModel().select(0);
        }else if(UtilData.getBoolean(value, false)){
            choiceBox.getSelectionModel().select(1);
        }else{
            choiceBox.getSelectionModel().select(2);
        }
    }

    @Override
    public Object getValue() {
        int index = choiceBox.getSelectionModel().getSelectedIndex();
        if(index == 1){
            return true;
        }else if(index == 2){
            return false;
        }else{
            return null;
        }
    }
}
