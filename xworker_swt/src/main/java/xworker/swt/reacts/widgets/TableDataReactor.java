package xworker.swt.reacts.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;
import xworker.swt.reacts.DataReactorContext;
import xworker.swt.reacts.WidgetDataReactor;

public class TableDataReactor extends WidgetDataReactor implements Listener{
	private static final String TAG = TableDataReactor.class.getName();
	
	Table table;
	
	public TableDataReactor(Table table, Thing self, ActionContext actionContext) {
		super(table, self, actionContext);
		
		this.table = table;
		this.table.addListener(SWT.Selection, this);
	}

	@Override
	public void handleEvent(Event event) {
		boolean check = self.getBoolean("check") & ((table.getStyle() & SWT.CHECK) == SWT.CHECK);
		List<Object> datas = new ArrayList<Object>();
		if(!check) {
			for(TableItem item : table.getSelection()) {
				datas.add(item.getData());
			}
		}else {
			for(TableItem item : table.getItems()) {
				if(item.getChecked()) {
					datas.add(item.getData());
				}
			}
		}
		
		if(datas.size() > 0) {
			this.fireSelected(datas, getContext());
		}else {
			this.fireUnselected(getContext());
		}
	}

	@Override
	protected void widgetDoOnSelected(List<Object> datas, DataReactorContext context) {
		String type = self.getStringBlankAsNull("type");
		if(!"dataStore".equals(type)) {
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
				table.setSelection(ids);
			}else {
				table.setSelection(new int[0]);
			}
		}
	}

	@Override
	protected void widgetDoOnUnselected(DataReactorContext context) {
		table.setSelection(new int[0]);
	}

	private void initItem(Object data, TableItem item) {
		item.setData(data);
		
		String[] texts = self.doAction("getTexts", actionContext, "data", data);
		if(texts != null) {
			item.setText(texts);
		}else {
			item.setText(String.valueOf(data));
		}
	}
	
	@Override
	protected void widgetDoOnAdded(int index, List<Object> datas, DataReactorContext context) {
		String type = self.getStringBlankAsNull("type");
		if(!"dataStore".equals(type)) {
			if(index >=0 && index < table.getItemCount()) {
				for(Object data : datas) {
					TableItem item = new TableItem(table, index, SWT.NONE);
					initItem(data, item);
				}
			}else {
				for(Object data : datas) {
					TableItem item = new TableItem(table, SWT.NONE);
					initItem(data, item);
				}
			}
		}
	}

	@Override
	protected void widgetDoOnRemoved(List<Object> datas, DataReactorContext context) {
		String type = self.getStringBlankAsNull("type");
		if(!"dataStore".equals(type)) {
			TableItem items[] = table.getItems();
			for(TableItem item : items) {
				Object d = item.getData();
				for(Object data : datas) {
					if(d == data) {
						item.dispose();
						break;
					}
				}
			}
			
			handleEvent(null);
		}
	}

	@Override
	protected void widgetDoOnUpdated(List<Object> datas, DataReactorContext context) {
		String type = self.getStringBlankAsNull("type");
		if(!"dataStore".equals(type)) {
			TableItem items[] = table.getItems();
			for(TableItem item : items) {
				Object d = item.getData();
				for(Object data : datas) {
					if(d == data) {
						initItem(data, item);
						break;
					}
				}
			}
		}
	}

	@Override
	protected void widgetDoOnLoaded(List<Object> datas, DataReactorContext context) {
		String type = self.getStringBlankAsNull("type");
		if(!"dataStore".equals(type)) {
			table.removeAll();
			
			for(Object data : datas) {
				TableItem item = new TableItem(table, SWT.NONE);
				initItem(data, item);
			}
		}
		
		this.fireUnselected(context);
	}

	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Table widget = self.doAction("getBindTo", actionContext);
		if(widget != null) {
			TableDataReactor reactor = new TableDataReactor(widget, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);
			return reactor;
		}else {
			Executor.warn(TAG, "Table is null, can not create TableDataReactor, thing=" + self.getMetadata().getPath());
		}
		
		return null;
		
	}
}
