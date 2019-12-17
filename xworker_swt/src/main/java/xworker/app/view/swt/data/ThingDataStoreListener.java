package xworker.app.view.swt.data;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.lang.executor.Executor;

public class ThingDataStoreListener {
	public static final String TAG = ThingDataStoreListener.class.getName();
		
	@SuppressWarnings("unchecked")
	public static void onInsert(ActionContext actionContext) {
		ListenerInvoker li = new ListenerInvoker("onInsert", actionContext) {
			List<DataObject> records;
			Integer index;
			
			public void invoke() {
				if(index == null) {
					listener.onInsert(store, -1, records);;
				}else {
					listener.onInsert(store, index, records);;
				}
			}
			
			public void init(ActionContext actionContext) {
				records = (List<DataObject>) actionContext.get("records");
				index= actionContext.getObject("index");				
			}
		};
		
		li.init(actionContext);
		li.exec();
	}
	
	public static void beforeLoad(ActionContext actionContext){
		ListenerInvoker li = new ListenerInvoker("beforeLoad", actionContext) {
			public void invoke() {
				listener.beforeLoad(store);
			}
			
			public void init(ActionContext actionContext) {
			}
		};
		
		li.init(actionContext);
		li.exec();
	}
	
	public static void onLoaded(final ActionContext actionContext){
		ListenerInvoker li = new ListenerInvoker("onLoaded", actionContext) {
			@SuppressWarnings("unchecked")
			public void invoke() {
				listener.onLoaded(store, (List<DataObject>) store.get("records"));
			}
			
			public void init(ActionContext actionContext) {
			}
		};
		
		li.init(actionContext);
		li.exec();
	}
	
	public static void onReconfig(final ActionContext actionContext){
		ListenerInvoker li = new ListenerInvoker("onReconfig", actionContext) {
			public void invoke() {
				listener.onReconfig(store);
			}
			
			public void init(ActionContext actionContext) {
			}
		};
		
		li.init(actionContext);
		li.exec();
	}
	
	public static void onRemove(final ActionContext actionContext){
		ListenerInvoker li = new ListenerInvoker("onRemove", actionContext) {
			List<DataObject> records;
			public void invoke() {
				listener.onRemove(store, records);
			}
			
			@SuppressWarnings("unchecked")
			public void init(ActionContext actionContext) {
				records = (List<DataObject>) actionContext.get("records");
			}
		};
		li.init(actionContext);
		li.exec();
	}
	
	public static void onUpdate(final ActionContext actionContext){
		ListenerInvoker li = new ListenerInvoker("onUpdate", actionContext) {
			List<DataObject> records;
			public void invoke() {
				listener.onUpdate(store, records);
			}
			
			@SuppressWarnings("unchecked")
			public void init(ActionContext actionContext) {
				records = (List<DataObject>) actionContext.get("records");
			}
		};
		
		li.init(actionContext);
		li.exec();
	}
	
	public static Thing attach(final Thing store, DataStoreListener listener, final ActionContext actionContext) {
		Control control = listener.getControl();
		if(control == null || control.isDisposed()) {
			Executor.warn(TAG, "Control is null or disposed, can not add DataStoreListener");
			return null;
		}
		
		final Thing self = new Thing("xworker.app.view.swt.data.events.ThingDataStoreListener");
		self.setData("listener", listener);
		self.setData("control", control);
		
		store.doAction("addListener", actionContext, "listener", self);
		if(control != null && control.isDisposed()) {
			control.addListener(SWT.Dispose, new Listener() {
	
				@Override
				public void handleEvent(Event event) {
					store.doAction("removeListener", actionContext, "listener", self);
				}			
			});
		}
		
		return self;
	}
	
	public static abstract class ListenerInvoker implements Runnable{
		String name;
		DataStoreListener listener;
		Thing self;
		Control control = null;
		Thing store;
		
		public ListenerInvoker(String name, ActionContext actionContext) {
			this.self = actionContext.getObject("self");
			this.name = name;
			this.listener = self.getData("listener");	
			this.control = self.getData("control");
			this.store = (Thing) actionContext.get("store");	
		}
		
		public abstract void init(ActionContext actionContext);
		
		public abstract void invoke();
		
		public void run() {
			try {
				if(control != null && !control.isDisposed()) {
					invoke();
				}				
			}catch(Exception e) {
				Executor.error(TAG, "beforeLoad error", e);
			}
		}
		
		public void exec() {
			if(control != null && !control.isDisposed()) {
				control.getDisplay().asyncExec(this);
			}
		}
		
		public String getName() {
			return name;
		}
	}
}
