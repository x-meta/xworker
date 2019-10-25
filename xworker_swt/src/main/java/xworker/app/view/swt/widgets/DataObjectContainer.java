package xworker.app.view.swt.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.app.view.swt.data.DataStore;
import xworker.app.view.swt.data.DataStoreListener;
import xworker.dataObject.DataObject;
import xworker.swt.util.ThingCompositeCreator;

public class DataObjectContainer implements DataStoreListener{
	public static final String RECORD = "__DataObjectContainer_recrod__";
	public static final String ACTIONCONTEXT = "__DataObjectContainer_actionContext__";
	
	ActionContext actionContext;
	ScrolledComposite scrollComposite;
	Composite mainComposite;
	DataStore dataStore;
	
	public DataObjectContainer(ActionContext actionContext) {
		this.actionContext = actionContext;
		
		this.scrollComposite = actionContext.getObject("scrollComposite");
		this.mainComposite = actionContext.getObject("mainComposite");
		
		scrollComposite.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event) {
				layout();
			}
			
		});
	}
	
	@Override
	public Control getControl() {
		return scrollComposite;
	}

	public static Object create(ActionContext actionContext) {
		final Thing self = actionContext.getObject("self");
		
		ThingCompositeCreator sc = new ThingCompositeCreator(self, actionContext) {

			@Override
			public void beforeCreateChilds(Object parent, ActionContext actionContext, ActionContext newActionContext) {
				DataObjectContainer dc = new DataObjectContainer(newActionContext);
				actionContext.g().put(self.getMetadata().getName(), dc);
			}
			
		};
		sc.setCompositeThing(World.getInstance().getThing("xworker.app.view.swt.widgets.prototype.DataObjectContainer/@scrollComposite"));
		
		Composite com = sc.create();
		
		return com;
	}

	public Control createRecordComposite(Thing store, DataObject record) {
		if(record == null) {
			return null;
		}
		
		ActionContext ac = new ActionContext();
		ac.put("parent", mainComposite);
		ac.put("store", store);
		ac.put("dataStore", dataStore);	
		ac.put("parentContext", actionContext.get("parentContext"));
		
		Control control = (Control) record.doAction("createSwtControl", ac);
		if(control != null) {
			control.setData(RECORD, record);
			control.setData(ACTIONCONTEXT, ac);
		}
		
		return control;
	}
	
	public void layout() {
		//scrollComposite.getClientArea().width
		//mainComposite.layout();
		Point size = mainComposite.computeSize(scrollComposite.getClientArea().width, SWT.DEFAULT, true);
		mainComposite.setSize(size);
		scrollComposite.setMinSize(size);
	}
	
	@Override
	public void onInsert(Thing store, int index, List<DataObject> records) {
		Control lastControl = null;
		if(index >= 0 && index < mainComposite.getChildren().length) {
			lastControl = mainComposite.getChildren()[index];
		}
		for(DataObject record : records) {
			Control control = createRecordComposite(store, record);
			if(control != null && lastControl != null) {
				control.moveAbove(lastControl);
				lastControl = control;
			}
		}
		
		layout();
	}

	@Override
	public void onLoaded(Thing store, List<DataObject> records) {
		for(Control child : mainComposite.getChildren()) {
			child.dispose();
		}
		
		for(DataObject record : records) {
			createRecordComposite(store, record);			
		}
		
		layout();
	}

	@Override
	public void onReconfig(Thing store) {
		for(Control child : mainComposite.getChildren()) {
			child.dispose();
		}
	}

	@Override
	public void onRemove(Thing store, List<DataObject> records) {
		List<Control> removed = new ArrayList<Control>();
		for(Control child : mainComposite.getChildren()) {
			DataObject record = (DataObject) child.getData(RECORD);
			if(records.contains(record)) {
				removed.add(child);
			}
		}
		
		for(Control control : removed) {
			control.dispose();
		}
		layout();
	}

	@Override
	public void onUpdate(Thing store, List<DataObject> records) {
		//由数据对象创建的Composite自身监控数据对象的变化
	}

	@Override
	public void beforeLoad(Thing store) {		
	}
}
