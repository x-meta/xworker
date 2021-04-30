package xworker.javafx.dataobject.datastore;

import javafx.scene.control.cell.ChoiceBoxTableCell;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataStore;
import xworker.dataObject.DataStoreListener;

public class DataStoreChoiceBoxTableCell implements DataStoreListener {
    DataStore dataStore;
    ChoiceBoxTableCell<DataObject, Object> choiceBox;

    public DataStoreChoiceBoxTableCell(DataStore dataStore, ChoiceBoxTableCell<DataObject, Object> choiceBox){
        this.dataStore = dataStore;
        this.choiceBox = choiceBox;

        //hoiceBox.itemsProperty().bind(dataStore.datasProperty());
        dataStore.addListener(this);
    }

    @Override
    public void onReconfig(DataStore dataStore, Thing dataObject) {
    }

    @Override
    public void onLoaded(DataStore dataStore) {
        choiceBox.getItems().clear();
        choiceBox.getItems().addAll(dataStore.getDatas());
    }

    @Override
    public void onChanged(DataStore dataStore) {
        choiceBox.getItems().clear();
        choiceBox.getItems().addAll(dataStore.getDatas());
    }

}
