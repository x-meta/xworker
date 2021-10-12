package xworker.spring.mybatis;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.session.RowBounds;
import org.springframework.cglib.beans.BeanMap;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import xworker.dataObject.DataObject;
import xworker.dataObject.PageInfo;
import xworker.dataObject.query.Condition;
import xworker.spring.SpringConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MybatisPlusDataObject {
        public static DataObject doCreate(ActionContext actionContext){
        DataObject theData = actionContext.getObject("theData");

        Thing descriptor = theData.getMetadata().getDescriptor();
        Class<?> entityClass = descriptor.doAction("getEntityClass", actionContext);
        if(entityClass == null){
            throw new ActionException("Entity class is null, dataObject=" + descriptor.getMetadata().getPath());
        }

        try {
            Object bean = createBean(theData, entityClass, true, false);
            Class<?> mapperClass = descriptor.doAction("getMapperClass", actionContext);
            BaseMapper<Object> mapper = (BaseMapper<Object>) SpringConfig.getApplicationContext().getBean(mapperClass);
            if(mapper.insert(bean) > 0){
                beanToDataObject(bean, theData);
                return theData;
            }else{
                return null;
            }
        }catch(Exception e){
            throw new ActionException(e.getMessage() + ", dataObject=" + descriptor.getMetadata().getPath(), e);
        }
    }

    public static Integer doUpdate(ActionContext actionContext){
        DataObject theData = actionContext.getObject("theData");

        Thing descriptor = theData.getMetadata().getDescriptor();
        Class<?> entityClass = descriptor.doAction("getEntityClass", actionContext);
        if(entityClass == null){
            throw new ActionException("Entity class is null, dataObject=" + descriptor.getMetadata().getPath());
        }

        try {
            Object bean = createBean(theData, entityClass, false, true);
            Class<?> mapperClass = descriptor.doAction("getMapperClass", actionContext);
            BaseMapper<Object> mapper = (BaseMapper<Object>) SpringConfig.getApplicationContext().getBean(mapperClass);

            return mapper.updateById(bean);
        }catch(Exception e){
            throw new ActionException(e.getMessage() + ", dataObject=" + descriptor.getMetadata().getPath(), e);
        }
    }

    public static List<DataObject> doQuery(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Wrapper<Object> wrapper = null;
        Thing condition = actionContext.getObject("conditionConfig");
        if(condition != null) {
            Map<String, Object> condtionData = actionContext.getObject("conditionData");
            Condition condition1 = Condition.parse(condition, condtionData, actionContext);
            wrapper = MyBatisDataObjectUtils.createWrapperFromCondition(condition1);
        }else{
            wrapper = new QueryWrapper<>();
        }

        Class<?> mapperClass = self.doAction("getMapperClass", actionContext);
        BaseMapper<Object> mapper = (BaseMapper<Object>) SpringConfig.getApplicationContext().getBean(mapperClass);
        PageInfo pageInfo = PageInfo.getPageInfo(actionContext);
        List<Map<String, Object>> datas = null;
        if(pageInfo != null && pageInfo.getLimit() > 0){
            String sort = pageInfo.getSort();
            if(sort != null && !sort.isEmpty()){
                //wrapper.orderBy(sort, pageInfo.isSortAsc());
            }

            Page<Map<String, Object>> page = new Page(pageInfo.getPage(), pageInfo.getLimit());
            page.setMaxLimit((long) pageInfo.getLimit());
            //int count = mapper.selectCount(wrapper);
            //pageInfo.setTotalCount(count);
            //if(wrapper.isEmptyOfWhere()){
            //    page = mapper.selectMapsPage(page, null);
            //}else {
                page = mapper.selectMapsPage(page, wrapper);
            //}
            datas = page.getRecords();
            pageInfo.setTotalCount((int) page.getTotal());
        }else{
            datas = mapper.selectMaps(wrapper);
        }

        List<DataObject> dataObjects = new ArrayList<>();
        for(Map<String, Object> data : datas){
            DataObject dataObject = new DataObject(self);
            dataObject.setInited(false);
            dataObject.putAll(data);
            dataObject.setInited(true);
            dataObjects.add(dataObject);
        }

        return dataObjects;
    }

    public static int deleteBatch(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Wrapper<Object> wrapper = null;
        Thing condition = actionContext.getObject("conditionConfig");
        if(condition != null) {
            Map<String, Object> condtionData = actionContext.getObject("conditionData");
            Condition condition1 = Condition.parse(condition, condtionData, actionContext);
            wrapper = MyBatisDataObjectUtils.createWrapperFromCondition(condition1);
        }else{
            wrapper = new QueryWrapper<>();
        }

        Class<?> mapperClass = self.doAction("getMapperClass", actionContext);
        BaseMapper<Object> mapper = (BaseMapper<Object>) SpringConfig.getApplicationContext().getBean(mapperClass);
        return mapper.delete(wrapper);
    }

    public static int updateBatch(ActionContext actionContext){
        DataObject theData = actionContext.getObject("theData");
        Thing self = actionContext.getObject("self");

        Wrapper<Object> wrapper = null;
        Thing condition = actionContext.getObject("conditionConfig");
        if(condition != null) {
            Map<String, Object> condtionData = actionContext.getObject("conditionData");
            Condition condition1 = Condition.parse(condition, condtionData, actionContext);
            wrapper = MyBatisDataObjectUtils.createWrapperFromCondition(condition1);
        }else{
            wrapper = new QueryWrapper<>();
        }

        Class<?> entityClass = self.doAction("getEntityClass", actionContext);
        if(entityClass == null){
            throw new ActionException("Entity class is null, dataObject=" + self.getMetadata().getPath());
        }
        Class<?> mapperClass = self.doAction("getMapperClass", actionContext);
        BaseMapper<Object> mapper = (BaseMapper<Object>) SpringConfig.getApplicationContext().getBean(mapperClass);
        try {
            Object bean = createBean(theData, entityClass, false, false);
            return mapper.update(bean, wrapper);
        }catch(Exception e){
            throw new ActionException(e.getMessage() + ", dataObject=" + self.getMetadata().getPath(), e);
        }
    }

    public static int doDelete(ActionContext actionContext){
        DataObject theData = actionContext.getObject("theData");

        Thing descriptor = theData.getMetadata().getDescriptor();
        Class<?> entityClass = descriptor.doAction("getEntityClass", actionContext);
        if(entityClass == null){
            throw new ActionException("Entity class is null, dataObject=" + descriptor.getMetadata().getPath());
        }

        try {
            Class<?> mapperClass = descriptor.doAction("getMapperClass", actionContext);
            BaseMapper<Object> mapper = (BaseMapper<Object>) SpringConfig.getApplicationContext().getBean(mapperClass);
            return mapper.deleteByMap(theData);
        }catch(Exception e){
            throw new ActionException(e.getMessage() + ", dataObject=" + descriptor.getMetadata().getPath(), e);
        }
    }

    public static void beanToDataObject(Object bean, DataObject dataObject){
        BeanMap beanMap = BeanMap.create(bean);
        Thing descriptor = dataObject.getMetadata().getDescriptor();

        for (Thing attribute : descriptor.getChilds("attribute")) {
            if (attribute.getBoolean("dataField")) {
                String name = attribute.getMetadata().getName();
                dataObject.set(name, beanMap.get(name));
            }
        }
    }

    public static Object createBean(DataObject dataObject, Class<?> beanClass, boolean allField, boolean addKey) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Object bean = ClassUtils.newInstance(beanClass);
        BeanMap beanMap = BeanMap.create(bean);
        //Object bean = beanClass.getConstructor().newInstance();

        if(allField) {
            //return BeanUtils.mapToBean(dataObject, beanClass);
            Thing descriptor = dataObject.getMetadata().getDescriptor();

            for (Thing attribute : descriptor.getChilds("attribute")) {
                putValue(dataObject, attribute, beanMap);
                /*
                if (attribute.getBoolean("dataField")) {
                    String name = attribute.getMetadata().getName();
                    beanMap.put(name, dataObject.get(name));
                }*/
            }
        }else{
            if(addKey) {
                //放入主键的值
                for (Thing key : dataObject.getMetadata().getKeys()) {
                    String name = key.getMetadata().getName();
                    beanMap.put(name, dataObject.get(name));
                }
            }
            for(String name : dataObject.getMetadata().getDirtyFields()){
                beanMap.put(name, dataObject.get(name));
            }
        }
        return bean;
    }

    public static boolean isNumber(String type){
        String[] types = new String[]{"byte", "short", "int", "long","float", "double"};
        for(String t : types){
            if(t.equals(type)){
                return true;
            }
        }

        return false;
    }
    public static void putValue(DataObject dataObject, Thing attribute, BeanMap beanMap){
        if (attribute.getBoolean("dataField")) {
            String name = attribute.getMetadata().getName();
            Object value = dataObject.get(name);
            if(value == null){
                String type = attribute.getString("type");
                if(isNumber(type)){
                    //数字的不赋值
                }else{
                    beanMap.put(name, dataObject.get(name));
                }
            }else{
                beanMap.put(name, dataObject.get(name));
            }

        }
    }
}
