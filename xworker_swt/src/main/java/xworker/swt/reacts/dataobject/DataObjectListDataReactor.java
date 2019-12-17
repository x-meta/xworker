package xworker.swt.reacts.dataobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectList;
import xworker.dataObject.DataObjectListListener;
import xworker.swt.reacts.DataReactor;
import xworker.swt.reacts.DataReactorUtils;

public class DataObjectListDataReactor extends DataReactor implements DataObjectListListener{
	DataObjectList dataObjectList;
	public DataObjectListDataReactor(DataObjectList dataObjectList, Thing self, ActionContext actionContext) {
		super(self, actionContext);

		this.dataObjectList = dataObjectList;
		Widget parent = this.getParentWidget(actionContext);
		dataObjectList.addListener(this);
		if(parent != null && parent.isDisposed() == false) {
			parent.addDisposeListener(new DisposeListener() {

				@Override
				public void widgetDisposed(DisposeEvent e) {
					try {
						DataObjectListDataReactor.this.dataObjectList.removeListener(DataObjectListDataReactor.this);
					}catch(Exception ee) {						
					}
				}
				
			});
		}
		
		this.fireLoaded(null);
	}

	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		DataObjectList list = self.doAction("getDataObjectList", actionContext);
		if(list != null) {
			DataObjectListDataReactor reactor = new DataObjectListDataReactor(list, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);
			return reactor;
		}
		return null;
	}

	@Override
	public void onLoaded(DataObjectList list) {
		List<Object> datas = new ArrayList<Object>();
		datas.addAll(list);
		this.fireLoaded(datas, null);
	}

	@Override
	public void onAdded(DataObjectList list, DataObject dataObject) {
		this.fireAdded(-1, DataReactorUtils.toObjectList(dataObject), null);
	}

	@Override
	public void onAdded(DataObjectList list, int index, DataObject dataObject) {
		this.fireAdded(index, DataReactorUtils.toObjectList(dataObject), null);
	}

	@Override
	public void onAddedAll(DataObjectList list, int index, Collection<? extends DataObject> c) {
		List<Object> datas = new ArrayList<Object>();
		datas.addAll(c);
		this.fireAdded(index, datas, null);
	}

	@Override
	public void onRemoved(DataObjectList list, int index, DataObject dataObject) {
		this.fireRemoved(DataReactorUtils.toObjectList(dataObject), null);
	}

	@Override
	public void onSeted(DataObjectList list, int index, DataObject newDataObject, DataObject oldDataObject) {
		this.fireRemoved(DataReactorUtils.toObjectList(oldDataObject), null);
		this.fireAdded(index, DataReactorUtils.toObjectList(newDataObject), null);
	}
}
