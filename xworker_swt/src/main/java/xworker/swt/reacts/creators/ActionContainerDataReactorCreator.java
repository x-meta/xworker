package xworker.swt.reacts.creators;

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilString;

import xworker.swt.reacts.DataReactor;
import xworker.swt.reacts.DataReactorCreator;

public class ActionContainerDataReactorCreator implements DataReactorCreator{

	@Override
	public DataReactor create(Object control, String action, ActionContext actionContext) {
		ActionContainer actionContainer = (ActionContainer) control;
		Map<String, String> params = UtilString.getParams(action);
		return actionContainer.doAction("createDataReactor", actionContext, "params", params);
	}

}
