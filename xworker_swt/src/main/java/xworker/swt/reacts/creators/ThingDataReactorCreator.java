package xworker.swt.reacts.creators;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.app.view.swt.widgets.form.DataObjectForm;
import xworker.swt.reacts.DataReactor;
import xworker.swt.reacts.DataReactorCreator;
import xworker.swt.reacts.dataobject.DataObjectFormDataReactor;
import xworker.swt.reacts.datas.DataStoreDataReactor;
import xworker.swt.reacts.xworker.ObjectViewerDataReactor;
import xworker.swt.reacts.xworker.ThingFormDataReactor;
import xworker.swt.xworker.ThingForm;

public class ThingDataReactorCreator implements DataReactorCreator{

	@Override
	public DataReactor create(Object control, String action, ActionContext actionContext) {
		Thing thing = (Thing) control;
		String thingName = thing.getThingName();
		if("DataStore".equals(thingName)) {
			Thing self = new Thing("xworker.swt.reactors.datas.DataStoreDataReactor");
			Map<String, String> params = UtilString.getParams(action);
			self.getAttributes().putAll(params);
			
			return new DataStoreDataReactor(thing, self, actionContext);
		}else if("DataObjectForm".equals(thingName)) {
			Thing self = new Thing("xworker.swt.reactors.dataobject.DataObjectFormDataReactor");
			Map<String, String> params = UtilString.getParams(action);
			self.getAttributes().putAll(params);
			
			DataObjectForm form = DataObjectForm.getDataObjectForm(thing);
			return new DataObjectFormDataReactor(form.getControl(), form, self, actionContext);
		}else if("ThingForm".equals(thingName)) {
			ThingForm thingForm = ThingForm.getThingForm(thing);
			if(thingForm != null) {
				Thing self = new Thing("xworker.swt.reactors.xworker.ThingFormDataReactor");
				Map<String, String> params = UtilString.getParams(action);
				self.getAttributes().putAll(params);
								
				return new ThingFormDataReactor(thingForm, self, actionContext);
			}
		}else if("ObjectViewer".equals(thingName)) {
			Thing self = new Thing("xworker.swt.reactors.xworker.ObjectViewerDataReactor");
			Map<String, String> params = UtilString.getParams(action);
			self.getAttributes().putAll(params);
			
			Composite composite = thing.doAction("getControl", actionContext);
			return new ObjectViewerDataReactor(thing, composite, self, actionContext);
		}
		
		
		return null;
	}

}
