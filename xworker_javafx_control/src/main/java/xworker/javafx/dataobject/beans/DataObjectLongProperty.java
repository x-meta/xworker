package xworker.javafx.dataobject.beans;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectListener;

public class DataObjectLongProperty extends SimpleLongProperty {
    DataObject dataObject;
    String name;

    public DataObjectLongProperty(DataObject dataObject, String name){
        this.dataObject = dataObject;
        this.name = name;

        super.set(dataObject.getLong(name));

        this.dataObject.addListener(new DataObjectListener() {
            @Override
            public void changed(DataObject dataObject) {
                DataObjectLongProperty.super.set(dataObject.getLong(name));
            }
        });
    }

    public DataObjectLongProperty(DataObject dataObject, Thing attribute){
        this(dataObject, attribute.getMetadata().getName());
    }

    @Override
    public void set(long v) {
        super.set(v);

        dataObject.put(name, v);
    }
}
