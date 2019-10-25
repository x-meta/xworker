package xworker.app.view.swt.data;

import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;

public interface DataStoreListener {	
	public void onInsert(Thing store, int index, List<DataObject> records);
	
	public void onLoaded(Thing store, List<DataObject> records);
	
	public void onReconfig(Thing store);
	
	public void onRemove(Thing store, List<DataObject> records);
	
	public void onUpdate(Thing store, List<DataObject> records);
	
	public void beforeLoad(Thing store);
	
	/**
	 * 用来监控DataStoreListener生命周期的控件，如果Control.isDisposed()=true，表示要删除该监听了。
	 * @return
	 */
	public Control getControl();
}
