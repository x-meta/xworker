package xworker.swt.reacts.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.reacts.DataReactorContext;
import xworker.swt.reacts.WidgetDataReactor;

public class ComboDataReactor extends WidgetDataReactor implements Listener{
	private static Logger logger = LoggerFactory.getLogger(ComboDataReactor.class);
	
	Combo combo;

	public ComboDataReactor(Combo combo, Thing self, ActionContext actionContext) {
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
	public void widgetDoOnSelected(List<Object> datas, DataReactorContext context) {
		if(datas != null & datas.size() > 0) {
			combo.setText(String.valueOf(datas.get(0)));		
		}
	}

	@Override
	public void widgetDoOnUnselected(DataReactorContext context) {
		combo.setText("");
	}

	private void reInitCombo() {
		combo.removeAll();
		
		for(Object data : this.getDatas()) {
			combo.add(String.valueOf(data));
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
		Combo combo = self.doAction("getBindTo", actionContext);
		if(combo != null) {
			ComboDataReactor reactor = new ComboDataReactor(combo, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);
			return reactor;
		}else {
			logger.warn("Combo is null, can not create ComboDataReactor, thing=" + self.getMetadata().getPath());
		}
		
		return null;
		
	}

}
