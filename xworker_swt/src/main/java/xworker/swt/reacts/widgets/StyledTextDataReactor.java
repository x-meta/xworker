package xworker.swt.reacts.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.reacts.WidgetDataReactor;

public class StyledTextDataReactor  extends WidgetDataReactor implements Listener{
	private static Logger logger = LoggerFactory.getLogger(StyledTextDataReactor.class);
	StyledText text;	
	
	public StyledTextDataReactor(StyledText text, Thing self, ActionContext actionContext) {
		super(text, self, actionContext);

		this.text = text;
		if(self.getBoolean("modify")) {
			text.addListener(SWT.Modify, this);
		}
		if(self.getBoolean("defaultSelection")) {
			text.addListener(SWT.DefaultSelection, this);
		}
	}

	@Override
	public void handleEvent(Event event) {
		List<Object> datas = getDatas();
		if(datas.size() == 0) {
			//没有前置Reactor或者datas为空
			String textStr = text.getText().trim();
			if("".equals(textStr)) {
				fireUnselected();
			}else {
				datas = new ArrayList<Object>();
				datas.add(textStr);
				fireSelected(datas);
			}
		}else {
			fireSelected(datas);
		}
	}

	private boolean isAppend() {
		return self.getBoolean("append") & ((text.getStyle() & SWT.MULTI) == SWT.MULTI);
	}
	
	@Override
	protected void widgetDoOnSelected(List<Object> datas) {		
		boolean append = isAppend();
		if(append) {
			for(Object data : datas) {
				text.append(String.valueOf(data));
				text.append("\n");
			}
		}else {
			resetText();
			
			for(Object data : datas) {
				text.append(String.valueOf(data));
				text.append("\n");
			}
		}
	}

	private void resetText() {
		text.setText("");
		
		List<Object> datas = getDatas();
		for(Object data : datas) {
			text.append(String.valueOf(data));
			text.append("\n");
		}
	}
	
	@Override
	protected void widgetDoOnUnselected() {
		if(!isAppend()) {
			text.setText("");
		}
	}

	@Override
	protected void widgetDoOnAdded(int index, List<Object> datas) {
		widgetDoOnSelected(datas);
	}

	@Override
	protected void widgetDoOnRemoved(List<Object> datas) {
		if(!isAppend()) {
			resetText();
		}
	}

	@Override
	protected void widgetDoOnUpdated(List<Object> datas) {
		widgetDoOnSelected(datas);
	}

	@Override
	protected void widgetDoOnLoaded(List<Object> datas) {
		widgetDoOnSelected(datas);
	}

	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		StyledText widget = self.doAction("getBindTo", actionContext);
		if(widget != null) {
			StyledTextDataReactor reactor = new StyledTextDataReactor(widget, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);
			return reactor;
		}else {
			logger.warn("StyledText is null, can not create StyledTextDataReactor, thing=" + self.getMetadata().getPath());
		}
		
		return null;
		
	}

}
