package xworker.swt.reacts.creators;

import java.util.Map;

import org.eclipse.swt.custom.CTabFolder;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.swt.reacts.DataReactor;
import xworker.swt.reacts.DataReactorCreator;
import xworker.swt.reacts.widgets.CTabFolderDataReactor;

public class CTabFolderDataReactorCreator implements DataReactorCreator{

	@Override
	public DataReactor create(Object control, String action, ActionContext actionContext) {
		Thing thing = new Thing("xworker.swt.reactors.widgets.CTabFolderDataReactor");
		Map<String, String> params = UtilString.getParams(action);
		thing.getAttributes().putAll(params);
		
		return new CTabFolderDataReactor((CTabFolder) control, thing, actionContext);
	}

}
