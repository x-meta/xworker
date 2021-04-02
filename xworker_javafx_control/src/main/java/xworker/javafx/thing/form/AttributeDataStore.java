package xworker.javafx.thing.form;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataStore;
import xworker.javafx.dataobject.DataStoreActions;

public class AttributeDataStore {

    /**
     * 从属性定义上和获取数据仓库。
     *
     * @param attribute
     * @return
     */
    public static DataStore getDataStore(Thing attribute, ActionContext actionContext){
        Thing dataStoreThing = getDataStoreThing(attribute);
        if(dataStoreThing != null) {
            Thing dataObject = getDataObject(dataStoreThing);
            Thing condition = getCondition(dataStoreThing);
            if(dataObject != null){
                return new DataStore(dataObject, condition, actionContext);
            }
        }

        return null;
    }

    /**
     * 返回数据仓库上引用的数据对象。
     *
     * 顺序如下：
     * 1.通过dataObject属性，一般是数据对象模型的路径。
     * 2.通过DataObjects子节点，如果有则返回它的第一个子节点。JavaFX的DataStore的定义。
     * 3.通过dataObjects子节点，如果有则返回它的第一个子节点。SWT的DataStore的定义。
     *
     * @param dataStoreThing 数据仓库模型，支持JavaFX和SWT下的数据仓库定义
     * @return
     */
    public static Thing getDataObject(Thing dataStoreThing){
        Thing dataObject = World.getInstance().getThing(dataStoreThing.getStringBlankAsNull("dataObject"));
        if(dataObject == null){
            dataObject = getThing(dataStoreThing, "DataObjects", true);
        }
        if(dataObject == null){
            dataObject = getThing(dataStoreThing, "dataObjects", true);
        }

        return dataObject;
    }

    /**
     * 返回查询条件的定义。
     *
     * @param dataStoreThing
     * @return
     */
    public static Thing getCondition(Thing dataStoreThing){
        Thing condition = World.getInstance().getThing(dataStoreThing.getStringBlankAsNull("condition"));
        if(condition == null){
            condition = World.getInstance().getThing(dataStoreThing.getStringBlankAsNull("queryConfig"));
        }
        if(condition == null){
            condition = getThing(dataStoreThing, "Condition", false);
        }
        if(condition == null){
            condition = getThing(dataStoreThing, "queryConfig", false);
        }

        return condition;
    }


    private static Thing getThing(Thing thing, String childName, boolean firstChild){
        Thing child = thing.getThing(childName + "@0");
        if(child != null){
            if(!firstChild){
                return child;
            }

            if(child.getChilds().size() > 0){
                return child.getChilds().get(0);
            }
        }

        return null;
    }

    /**
     * 获取属性上定义的数据仓库模型。
     *
     * 获取顺序，如果某个步骤获取到了数据仓库模型，那后面的步骤就不执行了：
     * 1.如果设置了inputattrs，那么分析inputattrs，试图解析出数据仓库模型。
     * 2.如果设置了DataStore子节点，那么也不执行了。
     * 3.通过属性的数据仓库设置。
     * 4.以上都没有获取到数据仓库模型，返回null。
     *
     * @param attribute 属性模型
     *
     * @return 数据仓库模型，可能为null
     */
    public static Thing getDataStoreThing(Thing attribute){
        if(attribute == null){
            return null;
        }

        //是否是本地
        World world = World.getInstance();
        String inputAttrs = attribute.getString("inputattrs");
        Thing dataStoreThing = null;
        if(inputAttrs != null && !"".equals(inputAttrs)){
            String params[] = inputAttrs.split("[,]");
            //第一个是数据仓库的地址
            dataStoreThing = world.getThing(params[0]);
        }

        if(dataStoreThing == null){
            //从属性设置获取
            dataStoreThing = world.getThing(attribute.getString("dataStore"));
        }

        if(dataStoreThing == null){
            //从子事物中获取
            dataStoreThing = attribute.getThing("DataStore@0");

            if(dataStoreThing == null && attribute.getStringBlankAsNull("relationDataObject") != null){
                //从数据对象创建
                dataStoreThing = new Thing("xworker.dataObject.DataStore");
                dataStoreThing.initDefaultValue();
                dataStoreThing.put("pageSize", 10000); //下拉列表应该不用分页，这里设置一个预估的很大的值
                dataStoreThing.put("attachToParent", "true");

                dataStoreThing.put("dataObject", attribute.get("relationDataObject"));
                String queryConfig = attribute.getStringBlankAsNull("relationQueryConfig");
                dataStoreThing.put("condition", queryConfig);
                if(queryConfig == null){
                    Thing qcfg = attribute.getThing("SelectCondition@0");
                    //Executor.info(TAG, "select condition =" + qcfg);
                    if(qcfg != null){
                        dataStoreThing.put("condition", qcfg.getMetadata().getPath());
                    }
                }
                dataStoreThing.put("autoLoad", "true");
                dataStoreThing.put("labelField", attribute.get("relationLabelField"));
            }
            return dataStoreThing;
        }else{
            //实例或者没有绑定父控件，那么包装一下
            return dataStoreThing;
        }
    }
}
