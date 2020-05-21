package xworker.swt.reacts;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

/**
 * 使用事物实现的数据响应器。
 * 
 * @author zhangyuxiang
 *
 */
public class AbstractDataReactor extends DataReactor{

	public AbstractDataReactor(Thing self, ActionContext actionContext) {
		super(self, actionContext);
	}

	@Override
	protected void doOnSelected(List<Object> datas, DataReactorContext context) {
		self.doAction("doOnSelected", actionContext, "dataReactor", this, "datas", datas, "context", context);
	}

	@Override
	protected void doOnUnselected(DataReactorContext context) {
		self.doAction("doOnUnselected", actionContext, "dataReactor", this, "datas", datas, "context", context);
	}

	@Override
	protected void doOnAdded(int index, List<Object> datas, DataReactorContext context) {
		self.doAction("doOnAdded", actionContext, "dataReactor", this, "index", index, "datas", datas, "context", context);
	}

	@Override
	protected void doOnRemoved(List<Object> datas, DataReactorContext context) {
		self.doAction("doOnRemoved", actionContext, "dataReactor", this, "datas", datas, "context", context);
	}

	@Override
	protected void doOnUpdated(List<Object> datas, DataReactorContext context) {
		self.doAction("doOnUpdated", actionContext, "dataReactor", this, "datas", datas, "context", context);
	}

	@Override
	protected void doOnLoaded(List<Object> datas, DataReactorContext context) {
		self.doAction("doOnLoaded", actionContext, "dataReactor", this, "datas", datas, "context", context);
	}

	public static AbstractDataReactor create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		AbstractDataReactor dataReactor = new AbstractDataReactor(self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), dataReactor);
		
		return dataReactor;
	}
}
