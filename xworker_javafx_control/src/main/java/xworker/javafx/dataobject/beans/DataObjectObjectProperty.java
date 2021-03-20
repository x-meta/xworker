package xworker.javafx.dataobject.beans;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectListener;

public class DataObjectObjectProperty<T> extends SimpleObjectProperty<T> {
    DataObject dataObject;
    String name;

    public DataObjectObjectProperty(DataObject dataObject, String name){
        this.dataObject = dataObject;
        this.name = name;

        super.set((T) dataObject.get(name));

        this.dataObject.addListener(new DataObjectListener() {
            @Override
            public void changed(DataObject dataObject) {
                DataObjectObjectProperty.super.set((T) dataObject.get(name));
            }
        });
    }

    public DataObjectObjectProperty(DataObject dataObject, Thing attribute){
        this(dataObject, attribute.getMetadata().getName());
    }

    @Override
    public void set(T v) {
        super.set(v);

        dataObject.put(name, v);
    }
}
