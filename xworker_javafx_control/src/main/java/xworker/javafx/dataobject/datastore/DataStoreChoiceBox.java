package xworker.javafx.dataobject.datastore;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataStoreListener;
import xworker.dataObject.DataStore;
import xworker.javafx.util.DataObjectStringConverter;

public class DataStoreChoiceBox implements DataStoreListener {
    DataStore dataStore;
    ChoiceBox<DataObject> choiceBox;
    DataObjectStringConverter stringConverter;
    Object value;

    public DataStoreChoiceBox(DataStore dataStore, ChoiceBox<DataObject> choiceBox){
        this.dataStore = dataStore;
        this.choiceBox = choiceBox;

        //hoiceBox.itemsProperty().bind(dataStore.datasProperty());
        dataStore.addListener(this);
    }

    @Override
    public void onReconfig(DataStore dataStore, Thing dataObject) {
        //清空原有的列
        stringConverter = new DataObjectStringConverter(dataObject);
        this.choiceBox.setConverter(stringConverter);
    }

    @Override
    public void onLoaded(DataStore dataStore) {
        choiceBox.setItems(FXCollections.observableList(dataStore.getDatas()));
        stringConverter.setDataObjects(dataStore.getDatas());
        if(value != null){
            if(value instanceof DataObject){
                choiceBox.setValue((DataObject) value);
            }else{
                for(DataObject data : dataStore.getDatas()){
                    if(data.equals(new Object[]{value})){
                        choiceBox.setValue(data);
                        break;
                    }
                }
            }
        }
    }

    public DataObjectStringConverter getStringConverter(){
        return stringConverter;
    }

    /**
     * 设置choiceBox选中的值，有可能只是主键的值，此时需要在加载后设置ChoiceBox的值。
     * @param value
     */
    public void setValue(Object value){
        this.value = value;

        if(value instanceof DataObject){
            choiceBox.setValue((DataObject) value);
        }else{
            for(DataObject data : dataStore.getDatas()){
                if(data.equals(new Object[]{value})){
                    choiceBox.setValue(data);
                    break;
                }
            }
        }
    }
}
