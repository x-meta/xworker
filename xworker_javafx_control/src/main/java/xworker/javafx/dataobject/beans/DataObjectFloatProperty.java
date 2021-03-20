package xworker.javafx.dataobject.beans;

import javafx.beans.property.SimpleFloatProperty;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectListener;

public class DataObjectFloatProperty   extends SimpleFloatProperty {
    DataObject dataObject;
    String name;

    public DataObjectFloatProperty(DataObject dataObject, String name){
        this.dataObject = dataObject;
        this.name = name;

        super.set(dataObject.getFloat(name));

        this.dataObject.addListener(new DataObjectListener() {
            @Override
            public void changed(DataObject dataObject) {
                DataObjectFloatProperty.super.set(dataObject.getFloat(name));
            }
        });
    }

    public DataObjectFloatProperty(DataObject dataObject, Thing attribute){
        this(dataObject, attribute.getMetadata().getName());
    }


    @Override
    public void set(float v) {
        super.set(v);

        dataObject.put(name, v);
    }
}
