package xworker.javafx.dataobject.beans;

import javafx.beans.property.SimpleStringProperty;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectListener;

public class DataObjectStringProperty extends SimpleStringProperty implements AutoSaveable{
    DataObject dataObject;
    String name;
    boolean autoSave;

    public DataObjectStringProperty(DataObject dataObject, String name){
        this.name = name;
        this.dataObject = dataObject;
        this.dataObject.addListener(new DataObjectListener() {
            @Override
            public void changed(DataObject dataObject) {
                DataObjectStringProperty.super.set(dataObject.getString(name));
            }
        });
        if(dataObject.isInited()) {
            super.set(dataObject.getString(name));
        }else{
            dataObject.loadBackground(new ActionContext());
        }
    }

    public DataObjectStringProperty(DataObject dataObject, Thing attribute, String name){
        this(dataObject, name);
    }

    @Override
    public void set(String v) {
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
