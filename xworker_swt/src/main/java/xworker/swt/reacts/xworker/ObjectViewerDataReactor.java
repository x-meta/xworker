package xworker.swt.reacts.xworker;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.reacts.DataReactorContext;
import xworker.swt.reacts.WidgetDataReactor;

public class ObjectViewerDataReactor extends WidgetDataReactor{
	Thing objectViewer;
	
	public ObjectViewerDataReactor(Thing objectViewer, Widget widget, Thing self, ActionContext actionContext) {
		super(widget, self, actionContext);
		this.objectViewer = objectViewer;
	}

	@Override
	protected void widgetDoOnSelected(List<Object> datas, DataReactorContext context) {
		objectViewer.doAction("removeAll", actionContext);
		for(int i = 0; i<datas.size(); i++) {
			Object obj = datas.get(i);
			objectViewer.doAction("addObject", actionContext, "object", obj, "name", "data" + i);
		}
	}

	@Override
	protected void widgetDoOnUnselected(DataReactorContext context) {
		objectViewer.doAction("removeAll", actionContext);
	}

	@Override
	protected void widgetDoOnAdded(int index, List<Object> datas, DataReactorContext context) {
	}

	@Override
	protected void widgetDoOnRemoved(List<Object> datas, DataReactorContext context) {
	}

	@Override
	protected void widgetDoOnUpdated(List<Object> datas, DataReactorContext context) {
	}

	@Override
	protected void widgetDoOnLoaded(List<Object> datas, DataReactorContext context) {
		widgetDoOnSelected(datas, context);
	}
	
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Thing viewer = self.doAction("getBindTo", actionContext);
		
		if(viewer != null) {
			String name = viewer.getThingName();
			if("ObjectViewer".equals(name)) {
				Composite composite = viewer.doAction("getControl", actionContext);
				ObjectViewerDataReactor reactor = new ObjectViewerDataReactor(viewer, composite, self, actionContext);
				actionContext.g().put(self.getMetadata().getName(), reactor);
			}
		}
	}

}
