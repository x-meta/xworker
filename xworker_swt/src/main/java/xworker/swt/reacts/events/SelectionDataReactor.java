package xworker.swt.reacts.events;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

import xworker.swt.reacts.WidgetDataReactor;

public class SelectionDataReactor extends WidgetDataReactor implements Listener{	
	private static Logger logger = LoggerFactory.getLogger(SelectionDataReactor.class);
	String action = null;
		
	public SelectionDataReactor(Widget widget, Thing self, ActionContext actionContext) {
		super(widget, self, actionContext);

		widget.addListener(SWT.Selection, this);
		action = self.doAction("getAction", actionContext);
	}

	
	@Override
	protected void widgetDoOnSelected(List<Object> datas) {
		if(self.getBoolean("monitorStatus")) {
			if(this.datas == null || this.datas.size() == 0) {
				this.setWidgetEnabled(false);
			}else {
				this.setWidgetEnabled(true);
			}
			
		}
	}

	@Override
	protected void widgetDoOnUnselected() {
		this.setWidgetEnabled(false);
	}

	@Override
	protected void widgetDoOnAdded(int index, List<Object> datas) {
		widgetDoOnSelected(datas);
	}

	@Override
	protected void widgetDoOnRemoved(List<Object> datas) {
		widgetDoOnSelected(datas);
	}

	@Override
	protected void widgetDoOnUpdated(List<Object> datas) {
		widgetDoOnSelected(datas);
	}

	@Override
	protected void widgetDoOnLoaded(List<Object> datas) {
		widgetDoOnSelected(datas);
	}


	@Override
	public void handleEvent(Event event) {
		try {
			//执行动作
			self.doAction("doAction", actionContext, "dataReactor", this, "action", action);
			
			//触发下级响应起的动作
			String fire = self.getString("fire");
			if("added".equals(fire)) {
				this.fireAdded(-1, datas);
			}else if("selected".equals(fire)) {
				this.fireSelected(datas);
			}else if("unselected".equals(fire)) {
				this.fireUnselected();
			}else if("updated".equals(fire)) {
				this.fireUpdated(datas);
			}else if("removed".equals(fire)) {
				this.fireRemoved(datas);
			}else if("loaded".equals(fire)) {
				this.fireLoaded(datas);
			}
			
			
		}catch(Exception e) {
			logger.warn("Execute doAction error, path=" + self.getMetadata().getPath(), e);
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

}
