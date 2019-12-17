package xworker.swt.reacts.creators;

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.dataObject.DataObject;
import xworker.swt.reacts.DataReactor;
import xworker.swt.reacts.DataReactorCreator;
import xworker.swt.reacts.dataobject.DataObjectDataReactor;

public class DataObjectDataReactorCreator implements DataReactorCreator{

	@Override
	public DataReactor create(Object control, String action, ActionContext actionContext) {
		Thing thing = new Thing("xworker.swt.reactors.datas.DataObjectDataReactor");
		Map<String, String> params = UtilString.getParams(action);
		thing.getAttributes().putAll(params);
		
		return new DataObjectDataReactor((DataObject) control, thing, actionContext);
	}

}
