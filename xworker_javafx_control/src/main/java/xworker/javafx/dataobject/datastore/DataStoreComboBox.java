package xworker.javafx.dataobject.datastore;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataStore;
import xworker.dataObject.DataStoreListener;
import xworker.javafx.dataobject.DataStoreActions;
import xworker.javafx.util.DataObjectStringConverter;

public class DataStoreComboBox implements DataStoreListener {
    DataStore dataStore;
    ComboBox<DataObject> comboBox;
    Object value;
    DataObjectStringConverter stringConverter;

    public DataStoreComboBox(DataStore dataStore, ComboBox<DataObject> comboBox){
        this.dataStore = dataStore;
        this.comboBox = comboBox;

        //comboBox.itemsProperty().bind(dataStore.datasProperty());
        dataStore.addListener(this);
    }

    public DataObjectStringConverter getStringConverter(){
        return stringConverter;
    }

    @Override
    public void onReconfig(DataStore dataStore, Thing dataObject) {
        //清空原有的列
        //this.comboBox.setCellFactory(new DataStoreListCellFactory(dataStore));
        stringConverter = new DataObjectStringConverter(dataObject);
        this.comboBox.setConverter(stringConverter);
    }

    @Override
    public void onLoaded(DataStore dataStore) {
        comboBox.setItems(FXCollections.observableList(dataStore.getDatas()));

        stringConverter.setDataObjects(dataStore.getDatas());
        if(value != null){
            if(value instanceof DataObject){
                comboBox.setValue((DataObject) value);
            }else{
                for(DataObject data : dataStore.getDatas()){
                    if(data.equalsByKey(value)){
                        comboBox.setValue(data);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onChanged(DataStore dataStore) {

    }

    public void setValue(Object value){
        this.value = value;

        if(value instanceof DataObject){
            comboBox.setValue((DataObject) value);
        }else{
            for(DataObject data : dataStore.getDatas()){
                if(data.equalsByKey(value)){
                    comboBox.setValue(data);
                    break;
                }
            }
        }
    }
}
