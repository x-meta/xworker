package xworker.javafx.dataobject.datastore;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.javafx.dataobject.DataStore;
import xworker.javafx.dataobject.DataStoreListener;
import xworker.javafx.util.DataObjectStringConverter;

public class DataStoreChoiceBox implements DataStoreListener {
    DataStore dataStore;
    ChoiceBox<DataObject> choiceBox;

    public DataStoreChoiceBox(DataStore dataStore, ChoiceBox<DataObject> choiceBox){
        this.dataStore = dataStore;
        this.choiceBox = choiceBox;

        choiceBox.itemsProperty().bind(dataStore.datasProperty());
        dataStore.addListener(this);
    }

    @Override
    public void onReconfig(DataStore dataStore, Thing dataObject) {
        //清空原有的列
        this.choiceBox.setConverter(new DataObjectStringConverter(dataObject));
    }

    @Override
    public void onLoaded(DataStore dataStore) {
    }
}
