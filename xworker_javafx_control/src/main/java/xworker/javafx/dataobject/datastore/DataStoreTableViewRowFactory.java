package xworker.javafx.dataobject.datastore;

import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataStore;
import xworker.lang.executor.Executor;

public class DataStoreTableViewRowFactory implements Callback<TableView<DataObject>, TableRow<DataObject>> {
    private static final String TAG = DataStoreTableViewRowFactory.class.getName();
    private static final DataStoreTableViewRowFactory instance = new DataStoreTableViewRowFactory();

    public static final DataStoreTableViewRowFactory getInstance(){
        return instance;
    }

    @Override
    public TableRow<DataObject> call(TableView<DataObject> param) {
        return new TableRow<DataObject>(){
            @Override
            protected void updateItem(DataObject item, boolean empty) {
                super.updateItem(item, empty);

                DataStore dataStore = DataStoreTableView.getDataStore(getTableView());
                if(dataStore != null){
                    try{
                        dataStore.getThing().doAction("updateTableItem", dataStore.getActionContext(), "item", this, "data", item, "empty", empty);
                    }catch(Exception e){
                        Executor.warn(TAG, "Update table row error", e);
                    }
                }

            }
        };
    }
}
