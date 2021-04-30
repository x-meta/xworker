package xworker.javafx.dataobject.beans;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectListener;

public class DataObjectIntegerProperty extends SimpleIntegerProperty implements AutoSaveable{
    DataObject dataObject;
    String name;
    boolean autoSave;

    public DataObjectIntegerProperty(DataObject dataObject, String name){
        this.dataObject = dataObject;
        this.name = name;

        if(dataObject.isInited()) {
            super.set(dataObject.getInt(name));
        }else{
            dataObject.loadBackground(new ActionContext());
        }

        this.dataObject.addListener(new DataObjectListener() {
            @Override
            public void changed(DataObject dataObject) {
                DataObjectIntegerProperty.super.set(dataObject.getInt(name));
            }
        });
    }

    public DataObjectIntegerProperty(DataObject dataObject, Thing attribute, String name){
        this(dataObject, name);
    }


    @Override
    public void set(int v) {
        super.set(v);

        dataObject.put(name, v);

        if(isAutoSave()){
            dataObject.update(new ActionContext());
        }
    }

    @Override
    public boolean isAutoSave() {
        return autoSave;
    }

    public void setAutoSave(boolean autoSave){
        this.autoSave = autoSave;
    }
}
