package xworker.javafx.thing.form.attributeeditors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.util.StringConverter;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataStore;
import xworker.javafx.dataobject.datastore.DataStoreChoiceBox;
import xworker.javafx.thing.form.AttributeEditor;
import xworker.javafx.thing.form.ThingForm;
import xworker.javafx.util.ThingValueStringConverter;

import java.util.List;

public class ChoiceDataStoreEditor extends AttributeEditor {
    ChoiceBox<DataObject> choiceBox;
    DataStore dataStore;
    DataStoreChoiceBox dataStoreChoiceBox;

    public ChoiceDataStoreEditor(ThingForm thingForm, Thing attribute, DataStore dataStore) {
        super(thingForm, attribute);

        this.dataStore = dataStore;
    }

    @Override
    public Node createEditor() {
        choiceBox = new ChoiceBox<>();
        choiceBox.selectionModelProperty().addListener(new ChangeListener<SingleSelectionModel<DataObject>>() {
            @Override
            public void changed(ObservableValue<? extends SingleSelectionModel<DataObject>> observable, SingleSelectionModel<DataObject> oldValue, SingleSelectionModel<DataObject> newValue) {
                thingForm.modified(ChoiceDataStoreEditor.this);
            }
        });
        dataStoreChoiceBox = new DataStoreChoiceBox(dataStore, choiceBox);
        if(dataStore.getDatas().size() == 0){
            dataStore.reload();
        }

        return choiceBox;
    }

    @Override
    public void setValue(Object value) {
        dataStoreChoiceBox.setValue(value);
    }

    @Override
    public Object getValue() {
        DataObject dataObject = choiceBox.getSelectionModel().getSelectedItem();
        if(dataObject != null){
            return dataObject.getKeyValue();
        }else {
            return null;
        }
    }
}
