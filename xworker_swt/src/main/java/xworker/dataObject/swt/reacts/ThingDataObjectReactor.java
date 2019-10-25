package xworker.dataObject.swt.reacts;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingDataObjectReactor extends DataObjectReactor{
	protected Thing self;
	protected ActionContext actionContext;
	
	public ThingDataObjectReactor(Thing self, ActionContext actionContext) {
		this.self = self;
		this.actionContext = actionContext;
	}

	@Override
	protected List<DataObjectReactor> getNextReactors() {
		List<String> nextReactors = self.doAction("getNextReactors", actionContext);
		List<DataObjectReactor> list = new ArrayList<DataObjectReactor>();
		if(nextReactors != null) {
			for(String ext : nextReactors) {
				Object obj = actionContext.get(ext);
				if(obj instanceof DataObjectReactor) {
					list.add((DataObjectReactor) obj);
				}
			}
		}
		
		return list;
	}
	
	
}
