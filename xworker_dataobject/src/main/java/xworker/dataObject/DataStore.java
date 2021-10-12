package xworker.dataObject;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.dataObject.query.QueryConfig;
import xworker.lang.executor.Executor;
import xworker.util.UtilData;

import java.util.*;

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
    PageInfo pageInfo = new PageInfo(0, 100);

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

    /**
     * 是否自动保存数据。
     */
    boolean autoSave;

    /**
     * 标签字段。
     */
    String labelField;

    List<DataStoreListener> listeners = new ArrayList<>();

    boolean loading = true;

   /**
     * 加载序列，每次加载都会++，监听器可以根据它过滤重复。
     */
    int loadSeq = 0;

    public DataStore(Thing thing, ActionContext actionContext){
        this.thing = thing;
        dataObject = thing.doAction("getDataObject", actionContext);
        resultDataObject = dataObject;
        this.actionContext = actionContext;
        if(this.actionContext == null){
            this.actionContext = new ActionContext();
        }

        datas = new DataObjectList(dataObject);

        condition = thing.doAction("getCondition", actionContext);
        if(this.condition == null && dataObject != null){
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
        autoSave = thing.getBoolean("autoSave");
        labelField = thing.getStringBlankAsNull("labelField");
    }

    public boolean isLoading() {
        return loading;
    }

    public DataStore(Thing dataObject, Thing condition, ActionContext actionContext){
        this.dataObject = dataObject;
        this.resultDataObject = dataObject;
        this.condition = condition;
        this.actionContext = actionContext;
        if(this.actionContext == null){
            this.actionContext = new ActionContext();
        }

        if(this.condition == null){
            this.condition = dataObject.doAction("getQueryCondition", actionContext);
        }
    }

    public QueryConfig createQueryConfig(){
        return new QueryConfig(condition, params, new PageInfo(), actionContext);
    }

    public boolean isAutoSave(){
        return autoSave;
    }

    public void setAutoSave(boolean autoSave){
        this.autoSave = autoSave;
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

    public void load(Map<String, Object> params, int page){
        this.params = params;
        pageInfo.setPage(page);
        doLoad();
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
        loading = true;
        synchronized (this){
            loadSeq++;
            new Thread(new DataStoreLoader(this, loadSeq)).start();
        }
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

    public Thing getDataObject(){
        return dataObject;
    }

    public String getLabelField() {
        return labelField;
    }

    public void setLabelField(String labelField) {
        this.labelField = labelField;
    }

    /**
     * 返回数据仓库的定义模型。
     */
    public Thing getThing(){
        return thing;
    }

    public ActionContext getActionContext(){
        return actionContext;
    }

    public void add(DataObject dataObject){
        datas.add(dataObject);

        fireChanged();
    }

    public void fireChanged(){
        for(DataStoreListener listener : listeners){
            listener.onChanged(this);
        }
    }
    public void add(int index, DataObject dataObject){
        datas.add(index, dataObject);

        fireChanged();
    }

    public void addAll(Collection<DataObject> datas){
        this.datas.addAll(datas);

        fireChanged();
    }

    public void addAll(int index, Collection<DataObject> datas){
        this.datas.addAll(index, datas);

        fireChanged();
    }

    public boolean remove(DataObject data){
        if(this.datas.remove(data)){
            this.fireChanged();

            return true;
        }

        return false;
    }

    public DataObject remove(int index){
        DataObject data = datas.remove(index);
        if(data != null){
            this.fireChanged();
        }

        return data;
    }

    public boolean removeAll(Collection<DataObject> datas){
        if(this.datas.removeAll(datas)){
            this.fireChanged();
            return true;
        }else{
            return false;
        }
    }

    public void setDataObject(Thing dataObject){
        Thing condition = dataObject.getThing("Condition@0");
        setDataObject(dataObject, condition);
    }

    public void setDataObject(Thing dataObject, Thing condition){
        this.dataObject = dataObject;
        this.condition = condition;

        int pageSize = thing.doAction("getPageSize", actionContext);
        if(pageSize > 0){
            pageInfo.setPageSize(pageSize);
        }else if(dataObject != null){
            pageSize = dataObject.getInt("pageSize");
            if(pageSize > 0){
                pageInfo.setPageSize(pageSize);
            }
        }

        for(DataStoreListener listener : listeners){
            listener.onReconfig(this, dataObject);
        }
    }

    public static class DataStoreLoader implements  Runnable{
        DataStore dataStore;
        int loadSeq;

        public DataStoreLoader(DataStore dataStore, int loadSeq){
            this.dataStore = dataStore;
            this.loadSeq = loadSeq;
        }

        public void run(){
            try {
                DataObject.beginThreadCache();
                Thing dataObject = dataStore.dataObject;
                ActionContext actionContext = dataStore.actionContext;
                Map<String, Object> params = dataStore.params;
                PageInfo pageInfo = dataStore.pageInfo;
                Thing condition = dataStore.condition;
                Thing resultDataObject = dataStore.resultDataObject;
                List<DataStoreListener> listeners = dataStore.listeners;

                if(loadSeq != dataStore.loadSeq){
                    return;
                }

                if(dataObject == null){
                    return;
                }

                if (params == null) {
                    dataStore.params = new HashMap<>();
                    params = dataStore.params;
                }

                for(DataStoreListener listener :dataStore.listeners){
                    listener.beforeLoad(dataStore, dataStore.condition, dataStore.params);
                }

                List<DataObject> dataObjects = dataObject.doAction("query", actionContext,
                        "conditionConfig", condition, "conditionData", params, "pageInfo", pageInfo.getPageInfoData());

                if(loadSeq == dataStore.loadSeq) {
                    //序列如果不匹配，那么丢弃本次加载
                    if (pageInfo.getDataObject() != null && pageInfo.getDataObject() != resultDataObject) {
                        dataStore.resultDataObject = pageInfo.getDataObject();
                        for (DataStoreListener listener : listeners) {
                            listener.onReconfig(dataStore, dataStore.resultDataObject);
                        }
                    }

                    dataStore.datas.clear();
                    dataStore.datas.addAll(dataObjects);

                    for (DataStoreListener listener : listeners) {
                        listener.onLoaded(dataStore);
                    }
                }
            }catch(Exception e){
                Executor.error(TAG, "Do dataobject query exception, dataObject=" + dataStore.getDataObjectPath(), e);
            }finally{
                dataStore.loading = false;
                DataObject.finishThreadCache();
            }
        }
    }
}
