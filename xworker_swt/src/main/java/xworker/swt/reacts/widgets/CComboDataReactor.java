package xworker.swt.reacts.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.reacts.DataReactorContext;
import xworker.swt.reacts.WidgetDataReactor;

public class CComboDataReactor extends WidgetDataReactor implements Listener{
	private static Logger logger = LoggerFactory.getLogger(CComboDataReactor.class);
	
	CCombo combo;

	public CComboDataReactor(CCombo combo, Thing self, ActionContext actionContext) {
		super(combo, self, actionContext);
		
		this.combo = combo;
		combo.addListener(SWT.Selection, this);
	}

	@Override
	public void handleEvent(Event event) {
		int index = combo.getSelectionIndex();
		List<Object> datas = this.getDatas();
		
		if(index >= 0 && index < datas.size()) {
			List<Object> ds = new ArrayList<Object>();
			ds.add(datas.get(index));
			
			this.fireSelected(ds, getContext());
		}else {
			this.fireUnselected(getContext());
		}
	}
	
	@Override
	protected void widgetDoOnSelected(List<Object> datas, DataReactorContext context) {
		if(datas != null & datas.size() > 0) {
			combo.setText(String.valueOf(datas.get(0)));		
		}
	}

	@Override
	protected void widgetDoOnUnselected(DataReactorContext context) {
		combo.setText("");
	}

	private void reInitCombo() {
		combo.removeAll();
		
		for(Object data : this.getDatas()) {
			combo.add(String.valueOf(data));
		}
	}
	
	@Override
	protected void widgetDoOnAdded(int index, List<Object> datas, DataReactorContext context) {
		reInitCombo();
	}

	@Override
	protected void widgetDoOnRemoved(List<Object> datas, DataReactorContext context) {
		reInitCombo();
		
		if(this.getDatas().size() == 0) {
			this.fireUnselected(context);
		}
	}

	@Override
	protected void widgetDoOnUpdated(List<Object> datas, DataReactorContext context) {
		reInitCombo();
	}

	@Override
	protected void widgetDoOnLoaded(List<Object> datas, DataReactorContext context) {
		reInitCombo();
		
		this.fireUnselected(context);
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		CCombo combo = self.doAction("getBindTo", actionContext);
		if(combo != null) {
			CComboDataReactor reactor = new CComboDataReactor(combo, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);
			return reactor;
		}else {
			logger.warn("CCombo is null, can not create CComboDataReactor, thing=" + self.getMetadata().getPath());
		}
		
		return null;
		
	}

}
