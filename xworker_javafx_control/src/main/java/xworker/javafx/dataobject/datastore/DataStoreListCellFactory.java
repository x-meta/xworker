package xworker.javafx.dataobject.datastore;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import xworker.dataObject.DataObject;
import xworker.javafx.dataobject.DataStore;

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
