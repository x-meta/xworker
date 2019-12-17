package xworker.swt.reacts.datas;

import java.util.Collection;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.reacts.DataReactor;
import xworker.swt.reacts.DataReactorFactory;

public class ConstantsDataReactor extends DataReactor{

	public ConstantsDataReactor(Thing self, ActionContext actionContext) {
		super(self, actionContext);
		
		Object values = self.doAction("getValues", actionContext);
		if(values instanceof Collection) {
			datas.addAll((Collection<?>) values);
		}else if(values != null) {
			datas.add(values);
		}
		
		DataReactorFactory.addToAutoLoad(this);
	}
	
	public void setValues(List<Object> values) {
		datas.clear();
		
		if(values != null) {
			datas.addAll(values);
		}
	}

	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ConstantsDataReactor reactor = new ConstantsDataReactor(self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), reactor);
		return reactor;
	}

}
