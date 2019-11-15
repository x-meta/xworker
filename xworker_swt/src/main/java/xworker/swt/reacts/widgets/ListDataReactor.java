package xworker.swt.reacts.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.reacts.WidgetDataReactor;

public class ListDataReactor  extends WidgetDataReactor implements Listener{
	private static Logger logger = LoggerFactory.getLogger(ListDataReactor.class);
	
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
		
		for(int index : indices) {
			if(index >= 0 && index < datas.size()) {			
				ds.add(datas.get(index));
			}
		}
		
		if(ds.size() > 0) {			
			this.fireSelected(ds);
		}else {
			this.fireUnselected();
		}
	}
	
	@Override
	public void widgetDoOnSelected(List<Object> datas) {
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
	public void widgetDoOnUnselected() {
		list.setSelection(new int[0]);
	}

	private void reInitCombo() {
		list.removeAll();
		
		for(Object data : this.getDatas()) {
			list.add(String.valueOf(data));
		}
	}
	
	@Override
	public void widgetDoOnAdded(int index, List<Object> datas) {
		reInitCombo();
	}

	@Override
	public void widgetDoOnRemoved(List<Object> datas) {
		reInitCombo();
		
		if(this.getDatas().size() == 0) {
			this.fireUnselected();
		}
	}

	@Override
	public void widgetDoOnUpdated(List<Object> datas) {
		reInitCombo();
	}

	@Override
	public void widgetDoOnLoaded(List<Object> datas) {
		reInitCombo();
		
		this.fireUnselected();
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		org.eclipse.swt.widgets.List combo = self.doAction("getBindTo", actionContext);
		if(combo != null) {
			ListDataReactor reactor = new ListDataReactor(combo, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);
			return reactor;
		}else {
			logger.warn("List is null, can not create ListDataReactor, thing=" + self.getMetadata().getPath());
		}
		
		return null;
		
	}

}
