package xworker.app.view.swt.data.events;

import java.util.Collections;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;

public class RowVsColDataStoreListener {
	@SuppressWarnings("unchecked")
	public static void onLoaded(ActionContext actionContext){
		Thing theStore = (Thing) actionContext.get("store");
		Thing self = (Thing) actionContext.get("self");		
		List<DataObject> records = (List<DataObject>) theStore.get("records");
		Thing store = (Thing) self.get("rowColStore");
		
		
		if("rowToCol".equals(store.getString("rowcolType"))){
		    store.put("records", store.doAction("rowToCols", actionContext, 
		    		UtilMap.toMap("records", records, "cols", store.get("cols"), "dataObject", store.get("dataObject"))));
		}else if("colToRow".equals(store.getString("rowcolType"))){
		    store.put("records", store.doAction("colToRows", actionContext, 
		    		UtilMap.toMap("records", records, "cols", store.get("cols"), "dataObject", store.get("dataObject"))));
		} else {
		    store.put("records", Collections.EMPTY_LIST);
		}  
		store.put("dataLoaded", true);
		store.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "onLoaded", "store", store));
		store.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "afterLoaded", "store", store));
	}
	
	public static void beforeLoad(ActionContext actionContext){
		//Thing theStore = (Thing) actionContext.get("store");
		Thing self = (Thing) actionContext.get("self");
		Thing store = (Thing) self.get("rowColStore");
		
		store.doAction("fireEvent", actionContext, UtilMap.toMap("eventName", "beforeLoad"));		
	}
}
