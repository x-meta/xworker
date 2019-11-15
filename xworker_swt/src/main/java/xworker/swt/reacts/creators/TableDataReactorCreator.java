package xworker.swt.reacts.creators;

import java.util.Map;

import org.eclipse.swt.widgets.Table;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.swt.reacts.DataReactor;
import xworker.swt.reacts.DataReactorCreator;
import xworker.swt.reacts.widgets.TableDataReactor;

public class TableDataReactorCreator implements DataReactorCreator{

	@Override
	public DataReactor create(Object control, String action, ActionContext actionContext) {
		Thing thing = new Thing("xworker.swt.reactors.widgets.TableDataReactor");
		Map<String, String> params = UtilString.getParams(action);
		thing.getAttributes().putAll(params);
		
		return new TableDataReactor((Table) control, thing, actionContext);
	}

}
