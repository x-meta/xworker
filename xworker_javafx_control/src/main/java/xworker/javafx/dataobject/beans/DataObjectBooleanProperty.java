package xworker.javafx.dataobject.beans;

import javafx.beans.property.SimpleBooleanProperty;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectListener;

public class DataObjectBooleanProperty extends SimpleBooleanProperty {
    DataObject dataObject;
    String name;

    public DataObjectBooleanProperty(DataObject dataObject, String name){
        this.dataObject = dataObject;
        this.name = name;

        super.set(dataObject.getBoolean(name));

        this.dataObject.addListener(new DataObjectListener() {
            @Override
            public void changed(DataObject dataObject) {
                DataObjectBooleanProperty.super.set(dataObject.getBoolean(name));
            }
        });
    }

    public DataObjectBooleanProperty(DataObject dataObject, Thing attribute){
        this(dataObject, attribute.getMetadata().getName());
    }

    @Override
    public void set(boolean v) {
        super.set(v);

        dataObject.put(name, v);
    }

}
