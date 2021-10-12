package xworker.javafx.dataobject.datastore;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataStore;
import xworker.javafx.dataobject.beans.*;

import java.util.List;

public class DataStoreTableViewCellValueFactory implements Callback<TableColumn.CellDataFeatures<DataObject, Object>, ObservableValue<Object>>{
    private static DataStoreTableViewCellValueFactory instance = new DataStoreTableViewCellValueFactory();
    public static final DataStoreTableViewCellValueFactory getInstance(){
        return instance;
    }

    @Override
    public ObservableValue call(TableColumn.CellDataFeatures<DataObject, Object> param) {
        Thing attribute = (Thing) param.getTableColumn().getUserData();
        DataObject data = param.getValue();
        DataStore tableDataStore = (DataStore) param.getTableView().getProperties().get(DataStoreTableView.DATASTORE);

        String name = attribute.getMetadata().getName();
        int index = name.indexOf(".");
        if(index != -1){
            //是取关联的对象的值
            String relationName = name.substring(0, index);
            name = name.substring(index + 1, name.length());

            DataObject relation = (DataObject) data.get(relationName);
            if(relation != null){
                data = relation;
            }else{
                //关联的对象不存在，返回空的字符串
                return new SimpleStringProperty();
            }
        }

        DataStore dataStore = (DataStore) param.getTableColumn().getProperties().get(DataStoreTableView.DATASTORE);
        AutoSaveable autoSaveable = null;
        if(dataStore != null){
            autoSaveable = new DataObjectObjectProperty(data, attribute, name, dataStore);
        }

        if(autoSaveable == null){
            List<Thing> values = (List<Thing>) param.getTableColumn().getProperties().get(DataStoreTableView.VALUES);
            if(values != null){
                autoSaveable = new DataObjectObjectProperty(data, attribute, name, values);
            }
        }

        if(autoSaveable ==  null) {
            String inputtype = attribute.getStringBlankAsNull("inputtype");
            //if("truefalse".equals())

            String type = attribute.getStringBlankAsNull("type");
            if ("string".equals(type)) {
                autoSaveable = new DataObjectStringProperty(data, attribute, name);
            } else if ("byte".equals(type) || "short".equals(type) || "int".equals(type)) {
                autoSaveable = new DataObjectIntegerProperty(data, attribute, name);
            } else if ("long".equals(type)) {
                autoSaveable = new DataObjectLongProperty(data, attribute, name);
            } else if ("float".equals(type)) {
                autoSaveable = new DataObjectFloatProperty(data, attribute, name);
            } else if ("double".equals(type) || "float".equals(type)) {
                autoSaveable = new DataObjectDoubleProperty(data, attribute, name);
            } else if ("boolean".equals(type)) {
                autoSaveable = new DataObjectBooleanProperty(data, attribute, name);
            } else {
                autoSaveable = new DataObjectObjectProperty<Object>(data, attribute, name);
            }
        }

        if(tableDataStore != null){
            autoSaveable.setAutoSave(tableDataStore.isAutoSave());
        }
        return (ObservableValue) autoSaveable;
    }
}
