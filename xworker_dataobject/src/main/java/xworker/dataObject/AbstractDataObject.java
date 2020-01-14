package xworker.dataObject;

import java.util.Collections;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.utils.DataObjectUtil;

public class AbstractDataObject {	
	public static List<DataObject> doQuery(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		List<DataObject> datas = self.doAction("getQueryDataObjects", actionContext);
		if(datas != null) {
			return DataObjectUtil.query(datas, actionContext);
		}else {
			return Collections.emptyList();
		}
	}
}
