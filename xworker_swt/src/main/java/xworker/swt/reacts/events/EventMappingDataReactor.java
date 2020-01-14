package xworker.swt.reacts.events;

import java.util.Collections;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.reacts.DataReactor;
import xworker.swt.reacts.DataReactorContext;

public class EventMappingDataReactor extends DataReactor{

	public EventMappingDataReactor(Thing self, ActionContext actionContext) {
		super(self, actionContext);
	}

	private void fire(String source, List<Object> datas, DataReactorContext context) {
		String mapped = this.self.getStringBlankAsNull(source);
		if(mapped != null) {
			if("selected".equals(mapped)) {
				this.fireSelected(datas, context);
			}else if("unselected".equals(mapped)) {
				this.fireUnselected(context);
			}else if("added".equals(mapped)) {
				this.fireAdded(-1, datas, context);
			}else if("loaded".equals(mapped)) {
				this.fireLoaded(datas, context);
			}else if("updated".equals(mapped)) {
				this.fireUpdated(datas, context);
			}else if("removed".equals(mapped)) {
				this.fireRemoved(datas, context);
			}
		}
	}
	
	@Override
	protected void doOnSelected(List<Object> datas, DataReactorContext context) {
		fire("selected", datas, context);
	}

	@Override
	protected void doOnUnselected(DataReactorContext context) {
		fire("unselected", Collections.emptyList(), context);
	}

	@Override
	protected void doOnAdded(int index, List<Object> datas, DataReactorContext context) {
		fire("added", datas, context);
	}

	@Override
	protected void doOnRemoved(List<Object> datas, DataReactorContext context) {
		fire("removed", datas, context);
	}

	@Override
	protected void doOnUpdated(List<Object> datas, DataReactorContext context) {
		fire("updated", datas, context);
	}

	@Override
	protected void doOnLoaded(List<Object> datas, DataReactorContext context) {
		fire("loaded", datas, context);
	}
	
	public static DataReactor create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		EventMappingDataReactor reactor = new EventMappingDataReactor(self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), reactor);
		return reactor;
	}

}
