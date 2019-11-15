package xworker.swt.reacts.creators;

import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.swt.reacts.DataReactor;
import xworker.swt.reacts.DataReactorCreator;
import xworker.swt.reacts.widgets.BrowserDataReactor;

public class BrowserDataReactorCreator implements DataReactorCreator{

	@Override
	public DataReactor create(Object control, String action, ActionContext actionContext) {
		Thing thing = new Thing("xworker.swt.reactors.widgets.BrowserDataReactor");
		Map<String, String> params = UtilString.getParams(action);
		thing.getAttributes().putAll(params);
				
		return new BrowserDataReactor((Browser) control, thing, actionContext);
	}

}
