package xworker.swt.reacts.creators;

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.swt.app.IEditorContainer;
import xworker.swt.app.reactors.EditorContainerDataReactor;
import xworker.swt.reacts.DataReactor;
import xworker.swt.reacts.DataReactorCreator;

public class EditorContainerDataReactorCreator implements DataReactorCreator{

	@Override
	public DataReactor create(Object control, String action, ActionContext actionContext) {
		Thing thing = new Thing("xworker.swt.app.reactors.EditorContainerDataReactor");
		Map<String, String> params = UtilString.getParams(action);
		thing.getAttributes().putAll(params);
		
		return new EditorContainerDataReactor((IEditorContainer) control, thing, actionContext);
	}


}
