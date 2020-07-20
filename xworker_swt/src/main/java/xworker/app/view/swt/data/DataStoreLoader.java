package xworker.app.view.swt.data;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectList;
import xworker.dataObject.PageInfo;
import xworker.dataObject.utils.DataObjectUtil;
import xworker.lang.executor.Executor;
import xworker.task.UserTask;

/**
 * 异步加载Store的执行器。
 * 
 * @author zhangyuxiang
 *
 */
public class DataStoreLoader implements Runnable{
	private static final String TAG = DataStoreLoader.class.getName();
	
	Thing store;
	ActionContext actionContext;
	
	public DataStoreLoader(Thing store, ActionContext actionContext) {
		this.store = store;
		this.actionContext = actionContext;
	}
	
	public void run() {
		DataObject.beginThreadCache();
        Thing sourceDataObject = (Thing) ((Thing) store.get("dataObject")).getData("sourceDataObject");
        DataStore dataStore = (DataStore) store.get("dataStore");
        UserTask userTask = DataObject.getUserTask(sourceDataObject, actionContext);
        try{
        	Bindings bindings = actionContext.push();
            //源数据对象		                
            if(sourceDataObject == null){
            	PageInfo pageInfo  = PageInfo.getPageInfo(store.get("pageInfo"));
            	sourceDataObject = (Thing) pageInfo.get("sourceDataObject");
            }
            if(sourceDataObject == null){
                sourceDataObject = (Thing) store.get("dataObject");
            }
            store.put("userTask", userTask);
            List<DataObject> records = null;
            bindings.putAll(UtilMap.toMap("store", store, "conditionData", store.get("params"), 
    				"conditionConfig", store.get("queryConfig"), "pageInfo", store.get("pageInfo")));
            Executor.debug(TAG, dataStore.getDataObject().getMetadata().getPath());
            Executor.debug(TAG, "" + store.get("queryConfig"));
            if(dataStore.sourceDatas != null) {
            	records = DataObjectUtil.query(dataStore.sourceDatas, actionContext);
            }else {
            	records = sourceDataObject.doAction("query", actionContext);
        	}
            DataObjectList datas = DataStore.setDataObjectList(store, records);
            store.put("records", datas);            
        }catch(Exception e) {
        	Executor.error(TAG, "Load dataobject error, dataObject=" + sourceDataObject, e);
        }finally{
        	actionContext.pop();
            DataObject.finishThreadCache();
            if(userTask != null) {
            	userTask.finished();
            }
            store.put("userTask", null);
        }
        //log.info("records=" + store.records);
        store.put("dataLoaded", true);
        
        //如果是动态查询，重新初始化数据对象
        DataStore.initDataObject(store, actionContext);
        store.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "onLoaded"));
        //store.doAction("fireEvent", acContext, ["eventName":"afterLoaded"]);
	}
}
