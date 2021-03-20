package xworker.javafx.dataobject.beans;

import javafx.beans.property.SimpleStringProperty;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectListener;

public class DataObjectStringProperty extends SimpleStringProperty {
    DataObject dataObject;
    String name;

    public DataObjectStringProperty(DataObject dataObject, String name){
        this.name = name;
        this.dataObject = dataObject;
        this.dataObject.addListener(new DataObjectListener() {
            @Override
            public void changed(DataObject dataObject) {
                DataObjectStringProperty.super.set(dataObject.getString(name));
            }
        });
        super.set(dataObject.getString(name));
    }

    public DataObjectStringProperty(DataObject dataObject, Thing attribute){
        this(dataObject, attribute.getMetadata().getName());
    }

    @Override
    public void set(String v) {
        super.set(v);

        dataObject.put(name, v);
    }
}
