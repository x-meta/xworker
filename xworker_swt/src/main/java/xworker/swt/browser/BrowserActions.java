package xworker.swt.browser;

import org.eclipse.swt.browser.Browser;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.executor.Executor;
import xworker.swt.util.SwtUtils;
import xworker.util.XWorkerUtils;

public class BrowserActions {
	private static final String TAG = BrowserActions.class.getName();
	
	public static void openThingDesc(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Browser browser = (Browser) self.doAction("getBrowser", actionContext);
		String thingPath = (String) self.doAction("getThingPath", actionContext);
		
		if(browser != null && thingPath != null){
			browser.setUrl(XWorkerUtils.getThingDescUrl(thingPath));
		}else{
			Executor.info(TAG, "browser or thingPath is null, action=" + self.getMetadata().getPath());
		}
	}
	
	public static void openWebControl(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Browser browser = (Browser) self.doAction("getBrowser", actionContext);
		String webControlPath = (String) self.doAction("getWebControlPath", actionContext);
		Thing webControl = World.getInstance().getThing(webControlPath);
		
		if(browser != null && webControl != null){
			browser.setUrl(XWorkerUtils.getWebControlUrl(webControl));
		}else{
			Executor.info(TAG, "browser or webControl is null, action=" + self.getMetadata().getPath());
		}
	}
	
	public static void openUrl(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Browser browser = (Browser) self.doAction("getBrowser", actionContext);
		String url = (String) self.doAction("getUrl", actionContext);
		
		if(browser != null && url != null){
			browser.setUrl(url);
		}else{
			Executor.info(TAG, "browser or url is null, action=" + self.getMetadata().getPath());
		}
	}
	
	public static Object evaluate(final ActionContext actionContext) {
		final Thing self = actionContext.getObject("self");
		final Browser browser = (Browser) self.doAction("getBrowser", actionContext);
		final String code = self.doAction("getCode", actionContext);
		BrowserCallback callBack = new BrowserCallback() {
			@Override
			public void evaluationFailed(Exception exception) {
				self.doAction("evaluationFailed", actionContext, "exception", exception);
			}

			@Override
			public void evaluationSucceeded(Object result) {
				self.doAction("evaluationSucceeded", actionContext, "result", result);
			}
			
		};
		
		return SwtUtils.evaluateBrowserScript(browser, code, callBack, actionContext);
	}
}
