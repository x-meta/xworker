package xworker.javafx.dataobject.beans;

import javafx.beans.property.SimpleBooleanProperty;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectListener;

public class DataObjectBooleanProperty extends SimpleBooleanProperty implements AutoSaveable{
    DataObject dataObject;
    String name;
    boolean autoSave;

    public DataObjectBooleanProperty(DataObject dataObject, String name){
        this.dataObject = dataObject;
        this.name = name;

        if(dataObject.isInited()) {
            super.set(dataObject.getBoolean(name));
        }else{
            dataObject.loadBackground(new ActionContext());
        }

        this.dataObject.addListener(new DataObjectListener() {
            @Override
            public void changed(DataObject dataObject) {
                DataObjectBooleanProperty.super.set(dataObject.getBoolean(name));
            }
        });
    }

    public DataObjectBooleanProperty(DataObject dataObject, Thing attribute, String name){
        this(dataObject, name);
    }

    @Override
    public void set(boolean v) {
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
