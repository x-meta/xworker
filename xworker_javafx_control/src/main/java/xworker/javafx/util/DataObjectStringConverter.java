package xworker.javafx.util;

import javafx.util.StringConverter;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectList;

import java.util.List;

public class DataObjectStringConverter extends StringConverter<DataObject> {
    Thing dataObject;

    public DataObjectStringConverter(Thing dataObject){
        this.dataObject = dataObject;
    }

    @Override
    public String toString(DataObject object) {
        if(object == null){
            return "";
        }

        Thing desc = object.getMetadata().getDescriptor();
        if(desc == null){
            String value = (String) object.doAction("toString", new ActionContext());
            if(value != null){
                return value;
            }
            return object.toString();
        }else{
            String labelName = desc.getStringBlankAsNull("displayName");
            if(labelName != null){
                return object.getString(labelName);
            }else{
                StringBuilder sb = new StringBuilder();
                for(Thing key : object.getMetadata().getKeys()){
                    if(sb.length() > 0){
                        sb.append(",");
                    }
                    sb.append(object.get(key.getMetadata().getName()));
                }
                return sb.toString();
            }
        }
    }

    @Override
    public DataObject fromString(String string) {
        if(dataObject != null){
            DataObject data = new DataObject(dataObject);
            Thing[] keys = data.getMetadata().getKeys();
            if(keys != null && keys.length > 0) {
                data.put(keys[0].getMetadata().getName(), string);
                data = data.load(new ActionContext());
                return data;
            }
        }

        return null;
    }

    public static DataObjectStringConverter create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Thing dataObject = self.doAction("getDataObject", actionContext);
        DataObjectStringConverter converter = new DataObjectStringConverter(dataObject);
        actionContext.g().put(self.getMetadata().getName(), converter);

        actionContext.peek().put("parent", converter);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return converter;
    }
}
