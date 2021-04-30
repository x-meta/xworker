package xworker.javafx.dataobject.beans;

import javafx.beans.property.SimpleFloatProperty;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectListener;

public class DataObjectFloatProperty   extends SimpleFloatProperty implements AutoSaveable{
    DataObject dataObject;
    String name;
    boolean autoSave;

    public DataObjectFloatProperty(DataObject dataObject, String name){
        this.dataObject = dataObject;
        this.name = name;

        if(dataObject.isInited()) {
            super.set(dataObject.getFloat(name));
        }else{
            dataObject.loadBackground(new ActionContext());
        }

        this.dataObject.addListener(new DataObjectListener() {
            @Override
            public void changed(DataObject dataObject) {
                DataObjectFloatProperty.super.set(dataObject.getFloat(name));
            }
        });
    }

    public DataObjectFloatProperty(DataObject dataObject, Thing attribute, String name){
        this(dataObject, name);
    }


    @Override
    public void set(float v) {
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
