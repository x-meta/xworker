package xworker.swt.reacts.creators;

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.dataObject.DataObjectList;
import xworker.swt.reacts.DataReactor;
import xworker.swt.reacts.DataReactorCreator;
import xworker.swt.reacts.DataReactorFactory;
import xworker.swt.reacts.datas.DataStoreDataReactor;

public class DataObjectListDataReactorCreator implements DataReactorCreator{

	@Override
	public DataReactor create(Object control, String action, ActionContext actionContext) {
		if(control instanceof DataObjectList) {
			Thing self = new Thing("xworker.swt.reactors.datas.DataStoreDataReactor");
			Map<String, String> params = UtilString.getParams(action);
			self.getAttributes().putAll(params);
			
			String name = DataReactorFactory.getDataReactorName();			
			Thing dataStore = actionContext.getObject(name + "DataStore");
			
			if(dataStore != null) {
				return new DataStoreDataReactor(dataStore, self, actionContext);
			}
		}
		
		return null;
	}
}
