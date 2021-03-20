package xworker.javafx.dataobject;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.PageInfo;
import xworker.javafx.dataobject.datastore.DataStoreChoiceBox;
import xworker.javafx.dataobject.datastore.DataStoreComboBox;
import xworker.javafx.dataobject.datastore.DataStoreTableView;
import xworker.lang.executor.Executor;
import xworker.util.UtilData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 封装数据对象的查询。
 */
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
    SimpleListProperty<DataObject> datas = new SimpleListProperty<>(FXCollections.observableArrayList());

    /**
     * DataStore模型的定义。
     */
    Thing thing;

    /**
     * 动作上下文。
     */
    ActionContext actionContext;

    List<DataStoreListener> listeners = new ArrayList<>();

    public DataStore(Thing thing, ActionContext actionContext){
        this.thing = thing;
        dataObject = thing.doAction("getDataObject", actionContext);
        resultDataObject = dataObject;

        condition = thing.doAction("getCondition", actionContext);
        List<String> binds = thing.doAction("getBindTo", actionContext);
        for(String bind : binds){
            Object obj = actionContext.getObject(bind);
            if(obj instanceof TableView){
                new DataStoreTableView(this, (TableView<DataObject>) obj);
            }else if(obj instanceof ComboBox){
                new DataStoreComboBox(this, (ComboBox<DataObject>) obj);
            }else if(obj instanceof ChoiceBox){
                new DataStoreChoiceBox(this, (ChoiceBox<DataObject>) obj);
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
    }

    public void addListener(DataStoreListener listener){
        listeners.add(listener);

        if(resultDataObject != null) {
            listener.onReconfig(this, resultDataObject);
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

    public SimpleListProperty<DataObject> datasProperty(){
        return datas;
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
