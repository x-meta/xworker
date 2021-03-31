package xworker.javafx.dataobject.datastore;

import javafx.scene.control.ComboBox;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.javafx.dataobject.DataStore;
import xworker.javafx.dataobject.DataStoreListener;
import xworker.javafx.util.DataObjectStringConverter;

public class DataStoreComboBox implements DataStoreListener {
    DataStore dataStore;
    ComboBox<DataObject> comboBox;

    public DataStoreComboBox(DataStore dataStore, ComboBox<DataObject> comboBox){
        this.dataStore = dataStore;
        this.comboBox = comboBox;

        //comboBox.itemsProperty().bind(dataStore.datasProperty());
        dataStore.addListener(this);
    }

    @Override
    public void onReconfig(DataStore dataStore, Thing dataObject) {
        //清空原有的列
        //this.comboBox.setCellFactory(new DataStoreListCellFactory(dataStore));
        this.comboBox.setConverter(new DataObjectStringConverter(dataObject));
    }

    @Override
    public void onLoaded(DataStore dataStore) {

        comboBox.setItems(dataStore.getDatas());
    }
}
