package xworker.app.view.swt.widgets.form;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.updator.ObjectUpdator;

public class DataObjectFormUpdator {
	public static void create(ActionContext actionContext) {
		Thing dataObjectForm = actionContext.getObject("dataObjectForm");
		Thing self = actionContext.getObject("self");
		
		ObjectUpdator updator = new ObjectUpdator(self, actionContext);
		updator.setData("form", dataObjectForm);
		updator.start();
	}
	
	public static boolean isStoped(ActionContext actionContext) {
		ObjectUpdator updator = actionContext.getObject("updator");
		Thing form = updator.getData("form");
		if(form == null) {
			return true;
		}
		
		DataObjectForm dataObjectForm = DataObjectForm.getDataObjectForm(form);
		if(dataObjectForm.getControl() == null || dataObjectForm.getControl().isDisposed()) {
			return true;
		}else {
			return false;
		}
	}
	
	public static Object getObject(ActionContext actionContext) {
		ObjectUpdator updator = actionContext.getObject("updator");
		Thing form = updator.getData("form");
		if(form == null) {
			return null;
		}
		
		DataObjectForm dataObjectForm = DataObjectForm.getDataObjectForm(form);
		if(dataObjectForm.getControl() == null || dataObjectForm.getControl().isDisposed()) {
			return null;
		}else {
			return dataObjectForm.getDataObject();
		}
	}
}
