package xworker.swt.reacts.widgets;

import java.util.List;

import org.eclipse.swt.browser.Browser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.reacts.DataReactorContext;
import xworker.swt.reacts.WidgetDataReactor;

public class BrowserDataReactor extends WidgetDataReactor{
	private static Logger logger = LoggerFactory.getLogger(BrowserDataReactor.class);
	
	Browser browser;
	
	public BrowserDataReactor(Browser browser, Thing self, ActionContext actionContext) {
		super(browser, self, actionContext);
		
		this.browser = browser;
	}

	@Override
	protected void widgetDoOnSelected(List<Object> datas, DataReactorContext context) {
		if(datas != null && datas.size() > 0) {
			String text = String.valueOf(datas.get(0));
			
			if(self.getBoolean("url")) {
				browser.setUrl(text);
			}else {
				browser.setText(text);
			}
		}
	}

	@Override
	protected void widgetDoOnLoaded(List<Object> datas, DataReactorContext context) {
		widgetDoOnSelected(datas, context);
	}

	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Browser widget = self.doAction("getBindTo", actionContext);
		if(widget != null) {
			BrowserDataReactor reactor = new BrowserDataReactor(widget, self, actionContext);
			actionContext.g().put(self.getMetadata().getName(), reactor);
			return reactor;
		}else {
			logger.warn("Browser is null, can not create BrowserDataReactor, thing=" + self.getMetadata().getPath());
		}
		
		return null;
		
	}
	
	
	
}
