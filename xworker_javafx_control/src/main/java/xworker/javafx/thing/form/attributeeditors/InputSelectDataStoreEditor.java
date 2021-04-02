package xworker.javafx.thing.form.attributeeditors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.util.StringConverter;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataStore;
import xworker.javafx.dataobject.datastore.DataStoreComboBox;
import xworker.javafx.thing.form.AttributeEditor;
import xworker.javafx.thing.form.ThingForm;
import xworker.javafx.util.ThingValueStringConverter;

import java.util.List;

public class InputSelectDataStoreEditor extends AttributeEditor {
    ComboBox<DataObject> comboBox = null;
    DataStore dataStore;
    DataStoreComboBox dataStoreComboBox;

    public InputSelectDataStoreEditor(ThingForm thingForm, Thing attribute, DataStore dataStore) {
        super(thingForm, attribute);

        this.dataStore = dataStore;

    }

    @Override
    public Node createEditor() {
        comboBox = new ComboBox<>();
        comboBox.setEditable(true);

        comboBox.selectionModelProperty().addListener(new ChangeListener<SingleSelectionModel<DataObject>>() {
            @Override
            public void changed(ObservableValue<? extends SingleSelectionModel<DataObject>> observable, SingleSelectionModel<DataObject> oldValue, SingleSelectionModel<DataObject> newValue) {
                thingForm.modified(InputSelectDataStoreEditor.this);
            }
        });

        dataStoreComboBox = new DataStoreComboBox(dataStore, comboBox);

        if(dataStore.getDatas().size() == 0){
            dataStore.reload();
        }
        return comboBox;
    }

    @Override
    public void setValue(Object value) {
        dataStoreComboBox.setValue(value);
    }

    @Override
    public Object getValue() {
        DataObject dataObject = comboBox.getSelectionModel().getSelectedItem();
        if(dataObject != null){
            return dataObject.getKeyValue();
        }else {
            return comboBox.getEditor().getText().trim();
        }
    }
}
