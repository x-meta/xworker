package xworker.javafx.dataobject.beans;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectListener;

public class DataObjectLongProperty extends SimpleLongProperty implements AutoSaveable{
    DataObject dataObject;
    String name;
    boolean autoSave;

    public DataObjectLongProperty(DataObject dataObject, String name){
        this.dataObject = dataObject;
        this.name = name;

        if(dataObject != null) {
            if(dataObject.isInited()) {
                super.set(dataObject.getLong(name));
            }else{
                dataObject.loadBackground(new ActionContext());
            }

            this.dataObject.addListener(new DataObjectListener() {
                @Override
                public void changed(DataObject dataObject) {
                    DataObjectLongProperty.super.set(dataObject.getLong(name));
                }
            });
        }
    }

    public DataObjectLongProperty(DataObject dataObject, Thing attribute, String name){
        this(dataObject, name);
    }

    @Override
    public void set(long v) {
        super.set(v);

        if(dataObject != null) {
            dataObject.put(name, v);

            if(isAutoSave()){
                dataObject.update(new ActionContext());
            }
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
