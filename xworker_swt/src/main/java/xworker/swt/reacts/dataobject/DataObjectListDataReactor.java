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
import xworker.swt.reacts.DataReactorContext;
import xworker.swt.reacts.DataReactorUtils;

public class DataObjectListDataReactor extends DataReactor implements DataObjectListListener{
	DataObjectList dataObjectList;
	int max = 0;
	
	public DataObjectListDataReactor(DataObjectList dataObjectList, Thing self, ActionContext actionContext) {
		super(self, actionContext);

		this.dataObjectList = dataObjectList;
		this.max = self.doAction("getMax", actionContext);
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
	
	public void setDataObjectList(DataObjectList dataObjectList) {
		if(dataObjectList == null) {
			return;
		}
		
		if(this.dataObjectList == dataObjectList) {
			return;
		}
		
		if(dataObjectList != null) {
			dataObjectList.removeListener(this);
		}
		
		this.dataObjectList = dataObjectList;
		this.dataObjectList.addListener(this);
		
		List<Object> datas = new ArrayList<Object>();
		datas.addAll(dataObjectList);
		this.fireLoaded(datas, null);
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

	@Override
	protected void doOnSelected(List<Object> datas, DataReactorContext context) {
		dataObjectList.clear();
		
		for(Object data : datas) {
			if(data instanceof DataObject) {
				dataObjectList.add((DataObject) data);
			}
		}
		
	}

	@Override
	protected void doOnUnselected(DataReactorContext context) {
		dataObjectList.clear();
	}

	@Override
	protected void doOnAdded(int index, List<Object> datas, DataReactorContext context) {
		for(Object data : datas) {
			if(data instanceof DataObject) {
				dataObjectList.add(index, (DataObject) data);
				index++;
			}
		}
		
		if(max > 0) {
			while(dataObjectList.size() > max) {
				dataObjectList.remove(0);
			}
		}
	}

	@Override
	protected void doOnRemoved(List<Object> datas, DataReactorContext context) {
		dataObjectList.removeAll(datas);
	}

	@Override
	protected void doOnUpdated(List<Object> datas, DataReactorContext context) {		
	}

	@Override
	protected void doOnLoaded(List<Object> datas, DataReactorContext context) {
		dataObjectList.clear();
		dataObjectList.begin();
		try {
			for(Object data : datas) {
				if(data instanceof DataObject) {
					dataObjectList.add((DataObject) data);
				}
			}
		}finally {
			dataObjectList.finish();
		}
	}
}
