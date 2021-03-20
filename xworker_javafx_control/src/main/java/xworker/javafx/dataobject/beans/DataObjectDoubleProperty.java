package xworker.javafx.dataobject.beans;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectListener;

public class DataObjectDoubleProperty  extends SimpleDoubleProperty {
    DataObject dataObject;
    String name;

    public DataObjectDoubleProperty(DataObject dataObject, String name){
        this.dataObject = dataObject;
        this.name = name;

        super.set(dataObject.getDouble(name));

        this.dataObject.addListener(new DataObjectListener() {
            @Override
            public void changed(DataObject dataObject) {
                DataObjectDoubleProperty.super.set(dataObject.getDouble(name));
            }
        });
    }

    public DataObjectDoubleProperty(DataObject dataObject, Thing attribute){
        this(dataObject, attribute.getMetadata().getName());
    }


    @Override
    public void set(double v) {
        super.set(v);

        dataObject.put(name, v);
    }

}
