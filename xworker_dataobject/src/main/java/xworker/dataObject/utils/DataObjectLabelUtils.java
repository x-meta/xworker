package xworker.dataObject.utils;

import org.xmeta.Thing;
import xworker.dataObject.AttributeDataStore;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataStore;
import xworker.lang.executor.Executor;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataObjectLabelUtils {
    private static final String TAG = DataObjectLabelUtils.class.getName();

    final Map<String, Format> formatMap = new HashMap<>();
    final Map<String, DataStore> dataStoreMap = new HashMap<>();
    final Map<String, List<Thing>> constantsMap = new HashMap<>();

    Thing dataObject;

    public DataObjectLabelUtils(Thing dataObject){
        this.dataObject = dataObject;

        for(Thing attribute : dataObject.getChilds("attribute")){
            String attributeName = attribute.getMetadata().getName();
            String inputtype = attribute.getStringBlankAsNull("inputtype");
            if("select".equals(inputtype) || "inputSelect".equals(inputtype)) {
                DataStore attributeDataStore = AttributeDataStore.getDataStore(attribute, null);
                if (attributeDataStore != null) {
                    //加载数据
                    attributeDataStore.load(new HashMap<>());
                    dataStoreMap.put(attributeName, attributeDataStore);
                }else{
                    constantsMap.put(attributeName, attribute.getChilds("value"));
                }
            }else{
                String type = attribute.getString("type");
                String viewPattern = attribute.getStringBlankAsNull("viewPattern");
                if(viewPattern != null){
                    switch(type){
                        case "date":
                        case "datetime":
                        case "time":
                            formatMap.put(attributeName, new SimpleDateFormat(viewPattern));
                            break;
                        case "byte":
                        case "short":
                        case "int":
                        case "long":
                        case "float":
                        case "double":
                            formatMap.put(attributeName, new DecimalFormat(viewPattern));
                            break;
                        default:
                            Executor.warn(TAG, "Can not determine formator, attribute=" + attribute.getMetadata().getPath() + ",type=" + type + ", pattern=" + viewPattern);
                    }
                }
            }
        }
    }

    public String getLabel(DataObject data, String attributeName){
        Object value = data.get(attributeName);
        if(value == null){
            return "";
        }

        try {
            //查看是否有格式化器
            Format format = formatMap.get(attributeName);
            if (format != null) {
                return format.format(value);
            }

            //select等可选择的数据标签
            List<Thing> values = constantsMap.get(attributeName);
            if(values != null){
                String strval = value.toString();
                for(Thing thing : values){
                    if(strval.equals(thing.getString("value"))){
                        return thing.getMetadata().getLabel();
                    }
                }

                return strval;
            }

            DataStore dataStore = dataStoreMap.get(attributeName);
            if(dataStore != null){
                for(DataObject d : dataStore.getDatas()){
                    if(d.equalsByKey(value)){
                        return d.getLabel();
                    }
                }

                return value.toString();
            }

            //是否时引用的
            int index = attributeName.indexOf(".");
            if(index != -1){
                //是取关联的对象的值
                String relationName = attributeName.substring(0, index);
                attributeName = attributeName.substring(index + 1, attributeName.length());

                DataObject relation = (DataObject) data.get(relationName);
                if(relation != null){
                    return relation.getLabel(attributeName);
                }else{
                    return value.toString();
                }
            }

            //最后是默认的，直接转化为字符串
            return value.toString();
        }catch(Exception e){
            Executor.warn(TAG, "Get attribute label error, dataObject="
                    + dataObject.getMetadata().getPath(), ", attribute=" + attributeName, e);
        }

        return String.valueOf(value);
    }
}
