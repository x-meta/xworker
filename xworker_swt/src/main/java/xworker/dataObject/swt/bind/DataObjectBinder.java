package xworker.dataObject.swt.bind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectListener;
import xworker.task.TaskManager;

public class DataObjectBinder implements DataObjectListener{
	private static Logger logger = LoggerFactory.getLogger(DataObjectBinder.class);
	
	Thing thing;
	ActionContext actionContext;
	Display display;
	DataObject dataObject;
	
	/** 如果为true表示当前正在等待执行doUpdate，是为了避免执行太平凡 */
	boolean waittingUpdate = false;
	/** 更新执行延迟时间 */
	int updateInterval = 100;
	long lastUpdateTime = 0;
	Object lockObj = new Object();
	
	/** 绑定功能的具体实现列表 */
	List<BinderItem> items = new ArrayList<BinderItem>();
	
	public DataObjectBinder(Thing thing, Display display, ActionContext actionContext) {
		this.thing = thing;
		this.display = display;
		this.actionContext = actionContext;
		
		initDataObject();
		this.updateInterval = thing.doAction("getUpdateInterval", actionContext);
	}
	
	public DataObjectBinder(DataObject dataObject, Control parent, ActionContext actionContext) {
		this.display = parent.getDisplay();
		this.dataObject = dataObject;
		this.dataObject.addListener(this);
		
		parent.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				try {
					DataObjectBinder.this.dataObject.removeListener(DataObjectBinder.this);
				}catch(Exception ee) {					
				}				
			}			
		});
	}
	
	/**
	 * 添加一个绑定条目。
	 * 
	 * @param item
	 */
	public void addBinderItem(BinderItem item) {
		if(item != null && items.contains(item) == false) {
			items.add(item);
		}
	}
	
	/**
	 * 移除一个绑定条目。
	 * 
	 * @param item
	 */
	public void removeBinderItem(BinderItem item) {
		items.remove(item);
	}
	
	
	@Override
	public void changed(DataObject dataObject) {
		for(int i=0; i<items.size(); i++) {
			BinderItem item = items.get(i);
			if(item.isDisposed()) {
				items.remove(i);
				i--;
			}else {
				item.changed(DataObjectBinder.this, dataObject);
			}
		}
		
		executeUpdate();
	}
	
	/**
	 * 从被绑定的控件上取值并存放到Map中返回，其中值的变量名是Item的名字。
	 * 
	 * @return
	 */
	public Map<String, Object> getValues(){
		Map<String, Object> values = new HashMap<String, Object>();
		for(BinderItem item : items) {
			values.put(item.thing.getMetadata().getName(), item.getValue());
		}
		
		return values;
	}

	private void initDataObject() {
		Object obj  = thing.doAction("getDataObject", actionContext);
		if(obj instanceof DataObject) {
			dataObject = (DataObject) obj;
		}else if(obj instanceof String) {
			String str = (String) obj;
			str = str.trim();
			if(str.equals("new") || str.equals("new:")) {
				dataObject = new DataObject();
			}else if(str.startsWith("new:")) {
				dataObject = new DataObject(str.substring(4, str.length()));
			}else if(str.startsWith("wrap:")){
				str = str.substring(5,  str.length());
				try {
					obj = UtilData.getData(str, actionContext);
				} catch (Exception e) {
					e.printStackTrace();
					obj = new Object();
				}
				dataObject = new DataObject(obj);
			}else{
				dataObject = new DataObject(str);
			}
			
			//创建的数据对象保存到全局变量上下文中
			actionContext.g().put(thing.getMetadata().getName() + "DataObject", dataObject);
		}
	
		if(dataObject != null) {
			dataObject.addListener(this);
		}
	}
	
	/**
	 * 设置新的数据对象，并监听新的数据对象。对于旧的数据对象则会取消监听。
	 * 
	 * @param dataObject 如果为null则
	 */
	public void setDataObject(DataObject dataObject) {
		if(this.dataObject != null) {
			this.dataObject.removeListener(this);
		}
		
		this.dataObject = dataObject;
		if(this.dataObject != null) {
			this.dataObject.addListener(this);
			
			update();
		}
	}
	
	/**
	 * 执行更新，为避免更新过于频繁，指定
	 */
	private void executeUpdate() {
		synchronized(lockObj) {
			if(waittingUpdate) {
				return;
			}else {
				waittingUpdate = true;
				long waitTime = System.currentTimeMillis() - lastUpdateTime;
				if(waitTime > updateInterval) {
					//如果大于更新间隔，立即执行
					doUpdate();
				}else {
					//延迟执行
					TaskManager.getScheduledExecutorService().schedule(new Runnable() {
						public void run() {
							DataObjectBinder.this.doUpdate();
						}
					}, updateInterval - waitTime, TimeUnit.MILLISECONDS);
				}
			}
		}
	}
	
	/**
	 * 更新当前数据对象到数据对象上。
	 */
	public void update() {
		doUpdate();
	}
	
	private void doUpdate(){
		display.asyncExec(new Runnable() {
			public void run() {
				synchronized(lockObj) {
					try {
						for(int i=0; i<items.size(); i++) {
							BinderItem item = items.get(i);
							if(item.isDisposed()) {
								items.remove(i);
								i--;
							}else {
								item.update(DataObjectBinder.this, dataObject);
							}
						}
					}catch(Exception e) {
						logger.error("Execute dataobject do update error, path=" + thing.getMetadata().getPath(), e);			
					}finally {
						waittingUpdate = false;
						lastUpdateTime = System.currentTimeMillis();
					}
				}
			}
		});		
	}
	
	public static DataObjectBinder create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		DataObjectBinder binder = self.doAction("getBinderInstance", actionContext);
		if(binder == null) {
			Control control = self.doAction("getControlForDisplay", actionContext);
			binder = new DataObjectBinder(self, control.getDisplay(), actionContext);
			actionContext.g().put(self.getMetadata().getName(), binder);
		}
		
		for(Thing child : self.getChilds()) {
			try {
				child.doAction("create", actionContext, "binder", binder);
				//BinderItem function = child.doAction("create", actionContext, "binder", binder);
				//if(function != null) {
				//	binder.items.add(function);
				//}
			}catch(Exception e) {
				logger.warn("Create BinderItem error, path=" + child.getMetadata().getPath(), e);
				
			}
		}
		
		return binder;
	}
}
