package xworker.javafx.util;

import javafx.util.StringConverter;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectList;

import java.util.ArrayList;
import java.util.List;

public class DataObjectStringConverter extends StringConverter<DataObject> {
    Thing dataObject;
    String labelField;
    List<DataObject> dataObjects = null;

    public DataObjectStringConverter(Thing dataObject){
        this.dataObject = dataObject;
    }

    public DataObjectStringConverter(Thing dataObject, String labelField){
        this.dataObject = dataObject;
        this.labelField = labelField;
    }

    public List<DataObject> getDataObjects() {
        return dataObjects;
    }

    /**
     * 用户从字符串转化为数据对象使用。
     *
     * @param dataObjects
     */
    public void setDataObjects(List<DataObject> dataObjects) {
        this.dataObjects = dataObjects;
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
            String labelName = labelField;
            if(labelName == null || labelName.isEmpty()) {
                labelName = desc.getStringBlankAsNull("displayName");
            }
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

    /**
     * 从字符串转化为数据对象。
     *
     * 从字符串转化为数据的
     * 如果字符串为空或null，返回null。
     *
     * 如果设置了数据对象列表，那么把字符作为数据对象的主键的值比较，或把列表中的数据对象转化为字符串，比较字符串的值，匹配则返回。
     *
     * 如果没有匹配的数据对象，那么创建一个新的对象。新的数据对象的第一个id和标签字段的值为字符串。
     *
     * @param string
     * @return
     */
    @Override
    public DataObject fromString(String string) {
        if(string == null || string.isEmpty()){
            return null;
        }

        if(dataObjects != null){
            for(DataObject data : dataObjects){
                if(data.equals(new Object[]{string})){
                    return data;
                }

                String label = toString(data);
                if(label != null && label.equals(string)){
                    return data;
                }
            }
        }

        if(dataObject != null){
            DataObject data = new DataObject(dataObject);
            Thing[] keys = data.getMetadata().getKeys();
            if(keys != null && keys.length > 0) {
                data.put(keys[0].getMetadata().getName(), string);
            }

            String labelName = labelField;
            if(labelName == null || labelName.isEmpty()) {
                labelName = dataObject.getStringBlankAsNull("displayName");
            }
            data.put(labelName, string);
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
