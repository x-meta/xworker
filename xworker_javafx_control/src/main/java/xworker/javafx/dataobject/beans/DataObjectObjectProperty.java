package xworker.javafx.dataobject.beans;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectListener;
import xworker.dataObject.DataStore;
import xworker.dataObject.DataStoreListener;

import java.util.List;
import java.util.Map;

public class DataObjectObjectProperty<T> extends SimpleObjectProperty<T> implements AutoSaveable{
    List<Thing> values = null;
    DataStore dataStore = null;

    DataObject dataObject;
    String name;
    Thing attribute;
    boolean autoSave;

    public DataObjectObjectProperty(DataObject dataObject, String name){
        this.dataObject = dataObject;
        this.name = name;

        super.set(getTValue());

        this.dataObject.addListener(new DataObjectListener() {
            @Override
            public void changed(DataObject dataObject) {
                DataObjectObjectProperty.super.set((T) dataObject.get(name));
                DataObjectObjectProperty.super.fireValueChangedEvent();
            }
        });
    }

    public DataObjectObjectProperty(DataObject dataObject, Thing attribute, String name){
        this(dataObject, name);

        this.attribute = attribute;
    }

    public DataObjectObjectProperty(DataObject dataObject, Thing attribute, String name,  List<Thing> values){
        this(dataObject, attribute, name);

        this.values = values;
    }

    public DataObjectObjectProperty(DataObject dataObject, Thing attribute, String name, DataStore dataStore){
        this(dataObject, attribute, name);

        this.dataStore = dataStore;
        this.dataStore.addListener(new DataStoreListener() {
            @Override
            public void onReconfig(DataStore dataStore, Thing dataObject) {

            }

            @Override
            public void onLoaded(DataStore dataStore) {
                DataObjectObjectProperty.this.fireValueChangedEvent();
            }

            @Override
            public void onChanged(DataStore dataStore) {
                onLoaded(dataStore);
            }

            @Override
            public void beforeLoad(DataStore dataStore, Thing condition, Map<String, Object> params) {

            }
        });
    }

    @Override
    public void set(T v) {
        super.set(v);

        if(v instanceof  Thing){
            Thing value = (Thing) v;
            dataObject.put(name, value.getString("value"));
        }else if(v instanceof DataObject){
            DataObject dataObject = (DataObject) v;
            this.dataObject.put(name, dataObject.getKeyValue());
        }else {
            dataObject.put(name, v);
        }

        if(isAutoSave()){
            dataObject.update(new ActionContext());
        }
    }

    private T getTValue(){
        if(!dataObject.isInited()){
            dataObject.loadBackground(new ActionContext());
            return null;
        }

        Object v = dataObject.get(name);
        if(v == null){
            return null;
        }
        if(values != null){
            //通过模型设置的常量列表
            for(Thing value : values){
                String str = value.getStringBlankAsNull("value");
                if(str != null && str.equals(v.toString())){
                    return (T) value;
                }
            }

            return null;
        }

        if(dataStore != null){
            //属性和其它数据对象关联
            for(DataObject dataObject : dataStore.getDatas()){
                if(dataObject.equalsByKey(v)){
                    return (T) dataObject;
                }
            }

            return null;
        }

        return (T) v;
    }

    @Override
    public T get() {
        return getTValue();
    }

    @Override
    public boolean isAutoSave() {
        return autoSave;
    }

    public void setAutoSave(boolean autoSave){
        this.autoSave = autoSave;
    }
}
