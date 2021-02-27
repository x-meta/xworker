package xworker.swt.reacts.events;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

import xworker.lang.executor.Executor;
import xworker.swt.reacts.DataReactorContext;
import xworker.swt.reacts.WidgetDataReactor;

public class SelectionDataReactor extends WidgetDataReactor implements Listener{	
	private static final String TAG = SelectionDataReactor.class.getName();
	String action = null;
		
	public SelectionDataReactor(Widget widget, Thing self, ActionContext actionContext) {
		super(widget, self, actionContext);

		widget.addListener(SWT.Selection, this);
		action = self.doAction("getAction", actionContext);
	}

	
	@Override
	protected void widgetDoOnSelected(List<Object> datas, DataReactorContext context) {
		if(self.getBoolean("monitorStatus")) {
			if(this.datas == null || this.datas.size() == 0) {
				this.setWidgetEnabled(false);
			}else {
				this.setWidgetEnabled(true);
			}
			
		}
	}

	@Override
	protected void widgetDoOnUnselected(DataReactorContext context) {
		this.setWidgetEnabled(false);
	}

	@Override
	protected void widgetDoOnAdded(int index, List<Object> datas, DataReactorContext context) {
		widgetDoOnSelected(datas, context);
	}

	@Override
	protected void widgetDoOnRemoved(List<Object> datas, DataReactorContext context) {
		widgetDoOnSelected(datas, context);
	}

	@Override
	protected void widgetDoOnUpdated(List<Object> datas, DataReactorContext context) {
		widgetDoOnSelected(datas, context);
	}

	@Override
	protected void widgetDoOnLoaded(List<Object> datas, DataReactorContext context) {
		widgetDoOnSelected(datas, context);
	}


	@Override
	public void handleEvent(Event event) {
		try {
			//执行动作
			self.doAction("doAction", actionContext, "dataReactor", this, "action", action);
			
			//触发下级响应起的动作
			String fire = self.getString("fire");
			if("added".equals(fire)) {
				this.fireAdded(-1, datas, getContext());
			}else if("selected".equals(fire)) {
				this.fireSelected(datas, getContext());
			}else if("unselected".equals(fire)) {
				this.fireUnselected(getContext());
			}else if("updated".equals(fire)) {
				this.fireUpdated(datas, getContext());
			}else if("removed".equals(fire)) {
				this.fireRemoved(datas, getContext());
			}else if("loaded".equals(fire)) {
				this.fireLoaded(datas, getContext());
			}
			
			
		}catch(Exception e) {
			Executor.warn(TAG, "Execute doAction error, path=" + self.getMetadata().getPath(), e);
		}
	}
	
	public static void doAction(ActionContext actionContext) {
		//SelectionDataReactor dataReactor = actionContext.getObject("dataReactor");
		String action = actionContext.getObject("action");
		
		if(action == null || "".equals(action)) {
			return;
		}
		
		//查看是否是ActionContainer
		int index = action.indexOf(".");
		if(index != -1) {
			String name = action.substring(0, index);
			String actionName = action.substring(index + 1, action.length());
			ActionContainer actionContainer = actionContext.getObject(name);
			if(actionContainer != null) {
				actionContainer.doAction(actionName, actionContext);
				return;
			}
		}
		
		//是否是动作
		Action ac = World.getInstance().getAction(action);
		if(ac != null) {
			//作为东走
			ac.run(actionContext);
		} 
	}

	public static SelectionDataReactor create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Widget Widget = self.doAction("getBindTo", actionContext);
		if(Widget != null) {
			SelectionDataReactor reactor = new SelectionDataReactor(Widget, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);
			return reactor;
		}else {
			Executor.warn(TAG, "Widget is null, can not create SelectionDataReactor, thing=" + self.getMetadata().getPath());
			return null;
		}		
	}
}
