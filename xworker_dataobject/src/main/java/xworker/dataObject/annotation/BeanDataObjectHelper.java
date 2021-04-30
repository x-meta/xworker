package xworker.dataObject.annotation;

import org.apache.commons.collections.MapUtils;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;
import xworker.dataObject.DataObject;
import xworker.dataObject.PageInfo;
import xworker.util.UtilData;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BeanDataObjectHelper {
    private final static String KEY = "__BeanDataObjectHelper__";
    private static Map<Class<?>, BeanDataObjectHelper> helpers = new HashMap<>();

    private Map<String, FieldHelper> fieldHelpes = new HashMap<String, FieldHelper>();

    /**
     * 创建bean的方法。
     */
    Method beanCreator;

    Method createMethod;
    Method updateMethod;
    Method deleteMethod;
    Method queryMethod;
    Method loadMethod;

    Thing dataObjectThing;

    Class<?> cls;

    public static BeanDataObjectHelper parse(Thing dataObjectThing, Class<?> cls) {
        synchronized(helpers){
            BeanDataObjectHelper helper = helpers.get(cls);

            if(helper == null){
                BeanDataObject[] dataObjects = cls.getAnnotationsByType(BeanDataObject.class);
                if(dataObjects.length > 0){
                    helper = new BeanDataObjectHelper();
                    helper.cls = cls;
                    helper.loadMethod = getMethod(cls, "doLoad", cls);
                    helper.createMethod = getMethod(cls, "doCreate", cls);
                    helper.updateMethod = getMethod(cls, "doUpdate", cls);
                    helper.deleteMethod = getMethod(cls, "doDelete", cls);
                    helper.queryMethod = getMethod(cls, "doQuery", Map.class, PageInfo.class);

                    helper.dataObjectThing = createDataObjectThing(cls, dataObjects[0]);
                    helper.dataObjectThing.setData(KEY, helper);

                    for(Field field : cls.getDeclaredFields()){
                        BeanDataObjectField[] fields = field.getAnnotationsByType(BeanDataObjectField.class);
                        if(fields.length > 0){

                        }
                    }


                }else{
                    throw new ActionException("Class " + cls.getName() + " is not a BeanDataObject, please add annotation @BeanDataObject.");
                }
            }

            return helper;
        }
    }

    public static Method getMethod(Class<?> cls, String methodName, Class<?> ... params){
        try {
            return cls.getMethod(methodName, params);
        }catch(Exception e){
            return null;
        }
    }
    public static BeanDataObjectHelper getHelper(Thing dataObjectThing){
        BeanDataObjectHelper helper = (BeanDataObjectHelper) dataObjectThing.getData(KEY);
        if(helper == null){
            Class<?> cls = dataObjectThing.doAction("getClass", new ActionContext());
            if(cls != null){
                helper = BeanDataObjectHelper.parse(dataObjectThing, cls);
                dataObjectThing.setData(KEY, helper);
            }
        }

        return null;
    }

    private static Thing createFieldThing(Field field, BeanDataObjectField dataObjectField){
        return null;
    }

    private static Thing createDataObjectThing(Class<?> cls, BeanDataObject dataObject){
        Thing thing = new Thing("xworker.dataObject.AbstractDataObject");
        thing.set("name", cls.getSimpleName());
        thing.set("paging", dataObject.paging());
        thing.set("pageSize", dataObject.pageSize());
        thing.set("gridEditable", dataObject.gridEditable());
        thing.set("autoLoad", dataObject.autoLoad());
        thing.set("autoSave", dataObject.autoSave());
        thing.set("valueName", dataObject.valueName());
        thing.set("displayName", dataObject.displayName());

        String attributes = dataObject.attributes();
        if(!attributes.isEmpty()){
            thing.getAttributes().putAll(UtilString.getParams(attributes));
        }

        Thing actions = new Thing("xworker.dataObject.AbstractDataObject/@actions");
        thing.addChild(actions);

        return thing;
    }

    public Thing getDataObjectThing(){
        return null;
    }

    public DataObject toDataObject(DataObject data, Object obj){
        if(obj == null){
            return null;
        }

        data.setWrappedObject(obj);
        for(String key : fieldHelpes.keySet()){
            FieldHelper fieldHelper = fieldHelpes.get(key);
            data.put(key, fieldHelper.getValue(obj));
        }
        return data;
    }


    public Object toBean(DataObject dataObject){
        if(dataObject.getWrappedObject() != null){
            return dataObject.getWrappedObject();
        }

        return null;
    }

    public DataObject create(DataObject dataObject){
        if(createMethod == null){
            throw new ActionException("Can not create, please add public static " + cls.getSimpleName()
                    + " doCreate(" + cls.getSimpleName() + "bean)");
        }
        Object bean = toBean(dataObject);
        try {
            Object newBean = createMethod.invoke(null, bean);
            return toDataObject(dataObject, newBean);
        }catch(Exception e){
            throw new ActionException(e);
        }
    }


    static class FieldHelper{
        Field field;
        Method getMethod;
        Method setMethod;

        public Object getValue(Object obj) {
            try {
                if (getMethod != null) {
                    return getMethod.invoke(obj);
                } else if (field != null) {
                    return field.get(obj);
                } else {
                    throw new ActionException("Can not get value, not field or getMethod");
                }
            }catch (Exception e){
                throw new ActionException(e);
            }
        }

        public void setValue(Object obj, Object value) {
            try {
                if (setMethod != null) {
                    setMethod.invoke(obj, value);
                } else if (field != null) {
                    field.set(obj, value);
                } else {
                    throw new ActionException("Ca not set value, no field or setMethod");
                }
            }catch(Exception e){
                throw new ActionException(e);
            }
        }
    }
}
