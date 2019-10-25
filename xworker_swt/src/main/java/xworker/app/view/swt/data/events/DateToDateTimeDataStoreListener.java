package xworker.app.view.swt.data.events;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;

public class DateToDateTimeDataStoreListener {
	@SuppressWarnings("unchecked")
	public static void onLoaded(ActionContext actionContext){
		Thing theStore = (Thing) actionContext.get("store");
		Thing self = (Thing) actionContext.get("self");
		
		List<DataObject> records = (List<DataObject>) theStore.get("records");
		Thing store = (Thing) self.get("rowColStore");
		store.put("records", store.doAction("convert", actionContext, 
				UtilMap.toMap("records", records, "cols", store.get("cols"), "dataObject", store.get("dataObject"))));

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
