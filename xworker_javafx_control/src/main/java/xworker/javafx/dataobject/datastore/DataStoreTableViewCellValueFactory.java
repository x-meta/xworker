package xworker.javafx.dataobject.datastore;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.javafx.dataobject.beans.*;

public class DataStoreTableViewCellValueFactory implements Callback<TableColumn.CellDataFeatures<DataObject, Object>, ObservableValue<Object>>{
    private static DataStoreTableViewCellValueFactory instance = new DataStoreTableViewCellValueFactory();
    public static final DataStoreTableViewCellValueFactory getInstance(){
        return instance;
    }

    @Override
    public ObservableValue call(TableColumn.CellDataFeatures<DataObject, Object> param) {
        Thing attribute = (Thing) param.getTableColumn().getUserData();
        DataObject data = param.getValue();

        String type = attribute.getStringBlankAsNull("type");
        if("string".equals(type)){
            return new DataObjectStringProperty(data, attribute);
        }else if("byte".equals(type) || "short".equals(type) || "int".equals(type)){
            return new DataObjectIntegerProperty(data, attribute);
        }else if("long".equals(type)){
            return new DataObjectLongProperty(data, attribute);
        }else if("float".equals(type)){
            return new DataObjectFloatProperty(data, attribute);
        }else if("double".equals(type) || "checkBox".equals(type)){
            return new DataObjectDoubleProperty(data, attribute);
        }else if("boolean".equals(type)){
            return new DataObjectBooleanProperty(data, attribute);
        }else{
            return new DataObjectObjectProperty<Object>(data, attribute);
        }
    }
}
