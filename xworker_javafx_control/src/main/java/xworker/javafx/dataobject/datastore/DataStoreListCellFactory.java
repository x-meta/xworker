package xworker.javafx.dataobject.datastore;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.Callback;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataStore;

public class DataStoreListCellFactory implements Callback<ListView<DataObject>, ListCell<DataObject>>{
    DataStore dataStore;

    public DataStoreListCellFactory(DataStore dataStore){
        this.dataStore = dataStore;
    }

    @Override
    public ListCell<DataObject> call(ListView<DataObject> param) {
        return new TextFieldListCell<DataObject>();
    }
}
