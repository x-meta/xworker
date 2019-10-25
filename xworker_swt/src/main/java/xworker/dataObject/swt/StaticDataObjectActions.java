package xworker.dataObject.swt;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;

public class StaticDataObjectActions {
	public static void bindToDataObjectForm(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Thing form = actionContext.getObject("dataObjectForm");
		
		DataObject dataObject = self.doAction("getDataObject", actionContext);
		if(dataObject != null && form != null) {
			form.doAction("setDataObject", actionContext, "dataObject", dataObject);
		}
	}
}
