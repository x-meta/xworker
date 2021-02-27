package xworker.swt.reacts.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.lang.executor.Executor;
import xworker.swt.reacts.DataReactorContext;
import xworker.swt.reacts.WidgetDataReactor;
import xworker.swt.util.SwtUtils;

public class ListDataReactor  extends WidgetDataReactor implements Listener{
	private static final String TAG = ListDataReactor.class.getName();
	
	org.eclipse.swt.widgets.List list;

	public ListDataReactor(org.eclipse.swt.widgets.List list, Thing self, ActionContext actionContext) {
		super(list, self, actionContext);
		
		this.list = list;
		list.addListener(SWT.Selection, this);
	}

	@Override
	public void handleEvent(Event event) {
		int[] indices = list.getSelectionIndices();
		List<Object> datas = this.getDatas();
		List<Object> ds = new ArrayList<Object>();
		
		if(datas == null || datas.size() == 0) {
			//可能是绑定了数据仓库
			List<DataObject> dobjs = SwtUtils.getSelectedDataObjects(list);
			if(dobjs != null){
				for(DataObject obj : dobjs) {
					ds.add(obj);
				}
			}
		}else {
			for(int index : indices) {
				if(index >= 0 && index < datas.size()) {			
					ds.add(datas.get(index));
				}
			}
		}		
		
		if(ds.size() > 0) {			
			this.fireSelected(ds, getContext());
		}else {
			this.fireUnselected(getContext());
		}
	}
	
	@Override
	public void widgetDoOnSelected(List<Object> datas, DataReactorContext context) {
		List<Integer> indices = new ArrayList<Integer>();
		if(datas != null & datas.size() > 0) {
			for(Object data : datas) {
				int index = this.getDataIndex(data);
				if(index >= 0) {
					indices.add(index);
				}
			}
		}
		
		if(indices.size() > 0) {
			int[] ids = new int[indices.size()];
			for(int i=0; i<indices.size(); i++) {
				ids[i] = indices.get(i);
			}
			list.setSelection(ids);
		}else {
			list.setSelection(new int[0]);
		}
	}

	@Override
	public void widgetDoOnUnselected(DataReactorContext context) {
		list.setSelection(new int[0]);
	}

	private void reInitCombo() {
		list.removeAll();
		
		for(Object data : this.getDatas()) {
			list.add(String.valueOf(data));
		}
	}
	
	@Override
	public void widgetDoOnAdded(int index, List<Object> datas, DataReactorContext context) {
		reInitCombo();
	}

	@Override
	public void widgetDoOnRemoved(List<Object> datas, DataReactorContext context) {
		reInitCombo();
		
		if(this.getDatas().size() == 0) {
			this.fireUnselected(context);
		}
	}

	@Override
	public void widgetDoOnUpdated(List<Object> datas, DataReactorContext context) {
		reInitCombo();
	}

	@Override
	public void widgetDoOnLoaded(List<Object> datas, DataReactorContext context) {
		reInitCombo();
		
		this.fireUnselected(context);
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		org.eclipse.swt.widgets.List combo = self.doAction("getBindTo", actionContext);
		if(combo != null) {
			ListDataReactor reactor = new ListDataReactor(combo, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);
			return reactor;
		}else {
			Executor.warn(TAG, "List is null, can not create ListDataReactor, thing=" + self.getMetadata().getPath());
		}
		
		return null;
		
	}

}
