package xworker.javafx.dataobject.beans;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectListener;

public class DataObjectIntegerProperty extends SimpleIntegerProperty {
    DataObject dataObject;
    String name;

    public DataObjectIntegerProperty(DataObject dataObject, String name){
        this.dataObject = dataObject;
        this.name = name;

        super.set(dataObject.getInt(name));

        this.dataObject.addListener(new DataObjectListener() {
            @Override
            public void changed(DataObject dataObject) {
                DataObjectIntegerProperty.super.set(dataObject.getInt(name));
            }
        });
    }

    public DataObjectIntegerProperty(DataObject dataObject, Thing attribute){
        this(dataObject, attribute.getMetadata().getName());
    }


    @Override
    public void set(int v) {
        super.set(v);

        dataObject.put(name, v);
    }
}
