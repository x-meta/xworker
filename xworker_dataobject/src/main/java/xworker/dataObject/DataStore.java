package xworker.dataObject;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.lang.executor.Executor;
import xworker.util.UtilData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {
    private static final String TAG = DataStore.class.getName();

    /**
     * 数据对象。
     */
    Thing dataObject;
    /**
     * 查询结果对应的数据对象。
     */
    Thing resultDataObject;

    /**
     * 查询条件的定义。
     */
    Thing condition;
    /**
     * 查询参数。
     */
    Map<String, Object> params;
    /**
     * 分页设置。
     */
    PageInfo pageInfo = new PageInfo();

    /**
     * 查询结果数据对象列表。
     */
    DataObjectList datas = new DataObjectList();

    /**
     * DataStore模型的定义。
     */
    Thing thing;

    /**
     * 动作上下文。
     */
    ActionContext actionContext;

    /**
     * 是否后台加载，如果是那么加载时会启动供应给线程。
     */
    boolean loadBackground;

    List<DataStoreListener> listeners = new ArrayList<>();

    public DataStore(Thing thing, ActionContext actionContext){
        this.thing = thing;
        dataObject = thing.doAction("getDataObject", actionContext);
        resultDataObject = dataObject;
        datas = new DataObjectList(dataObject);

        condition = thing.doAction("getCondition", actionContext);
        if(this.condition == null){
            this.condition = dataObject.doAction("getQueryCondition", actionContext);
        }

        int pageSize = thing.doAction("getPageSize", actionContext);
        if(pageSize > 0){
            pageInfo.setPageSize(pageSize);
        }else if(dataObject != null){
            pageSize = dataObject.getInt("pageSize");
            if(pageSize > 0){
                pageInfo.setPageSize(pageSize);
            }
        }

        if(UtilData.isTrue(thing.doAction("isAutoLoad", actionContext))){
            this.load(new HashMap<>());
        }
    }

    public DataStore(Thing dataObject, Thing condition, ActionContext actionContext){
        this.dataObject = dataObject;
        this.resultDataObject = dataObject;
        this.condition = condition;
        this.actionContext = actionContext;

        if(this.condition == null){
            this.condition = dataObject.doAction("getQueryCondition", actionContext);
        }
    }

    public void addListener(DataStoreListener listener){
        listeners.add(listener);

        if(resultDataObject != null) {
            listener.onReconfig(this, resultDataObject);
        }
        if(datas.size() > 0){
            listener.onLoaded(this);
        }
    }

    public void removeListener(DataStoreListener listener){
        listeners.remove(listener);
    }

    public void load(Map<String, Object> params){
        this.params = params;
        pageInfo.setPage(0);
        doLoad();
    }

    public void load(int page){
        pageInfo.setPage(page);
        doLoad();
    }

    public void reload(){
        doLoad();
    }

    public DataObjectList getDatas(){
        return datas;
    }

    public PageInfo getPageInfo(){
        return pageInfo;
    }

    private void doLoad(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (params == null) {
                        params = new HashMap<>();
                    }

                    List<DataObject> dataObjects = dataObject.doAction("query", actionContext,
                            "conditionConfig", condition, "conditionData", params, "pageInfo", pageInfo.getPageInfoData());
                    if(pageInfo.getDataObject() != null && pageInfo.getDataObject() != resultDataObject){
                        DataStore.this.resultDataObject = pageInfo.getDataObject();
                        for(DataStoreListener listener : listeners){
                            listener.onReconfig(DataStore.this, DataStore.this.resultDataObject);
                        }
                    }

                    datas.clear();
                    datas.addAll(dataObjects);

                    for(DataStoreListener listener :listeners){
                        listener.onLoaded(DataStore.this);
                    }
                }catch(Exception e){
                    Executor.error(TAG, "Do dataobject query exception, dataObject=" + getDataObjectPath(), e);
                }
            }
        }).start();
    }

    private String getDataObjectPath(){
        if(dataObject != null){
            return dataObject.getMetadata().getPath();
        }else{
            return "DataObject is null";
        }
    }

    public static DataStore create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        DataStore store = new DataStore(self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), store);

        actionContext.peek().put("parent", store);
        for(Thing child : self.getChilds()){
            child.doAction("ceate", actionContext);
        }
        return store;
    }
}
