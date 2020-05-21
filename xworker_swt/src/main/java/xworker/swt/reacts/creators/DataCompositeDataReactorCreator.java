package xworker.swt.reacts.creators;

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.swt.data.DataComposite;
import xworker.swt.reacts.DataReactor;
import xworker.swt.reacts.DataReactorCreator;
import xworker.swt.reacts.ProxyDataReactor;

public class DataCompositeDataReactorCreator implements DataReactorCreator{

	@Override
	public DataReactor create(Object control, String action, ActionContext actionContext) {
		Thing thing = new Thing("xworker.swt.reactors.DataReactor");
		Map<String, String> params = UtilString.getParams(action);
		thing.getAttributes().putAll(params);
		thing.put("dataReactorAction", action);
		
		ProxyDataReactor reactor = new ProxyDataReactor(thing, actionContext);
		DataComposite dataComposite = (DataComposite) control;
		dataComposite.setDataReactor(reactor);
		
		return reactor;
	}


}
