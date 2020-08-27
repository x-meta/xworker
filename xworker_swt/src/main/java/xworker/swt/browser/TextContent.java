package xworker.swt.browser;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.util.SwtUtils;
import xworker.util.UtilData;
import xworker.util.XWorkerUtils;

public class TextContent implements ProgressListener {
	Thing thing;
	Browser browser;
	ActionContext actionContext;
	ActionContext parentContext;
	String text;
	boolean ready = false;
	
	public TextContent(Thing thing, Browser browser, ActionContext parentContext) {
		this.thing = thing;
		this.browser = browser;
		
		this.actionContext = new ActionContext();
		this.actionContext.put("parentContext", parentContext);
		this.actionContext.put("textContent", this);
		
		//创建JavaScript函数
		World world = World.getInstance();
		Thing function = world.getThing("xworker.swt.browser.prototypes.TextContentBrowser/@getXWText");
		function.doAction("create", actionContext, "parent", browser);
		
		browser.addProgressListener(this);
		if(UtilData.isTrue(thing.doAction("isLoadUrl", parentContext))) {
			String url = thing.doAction("getUrl", parentContext);
			if(url == null || "".equals(url)) {
				//使用默认的URL
				Thing control = world.getThing("xworker.swt.browser.prototypes.TextContentWeb");
				url = XWorkerUtils.getWebControlUrl(control);
			}
			
			browser.setUrl(url);
		}
	}
	
	public void setText(String text) {
		this.text = text;
				
		//告诉浏览器取内容
		if(ready == false) {
			return;
		}
		SwtUtils.evaluateBrowserScript(browser, "updateXWText()", null, actionContext);
	}
	
	public String getText() {
		if(text != null) {
			return text;
		}else {
			return (String) thing.doAction("getText", actionContext);
		}
	}
	
	/**
	 * getXWText的JavaScript函数的实现。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String getXWText(ActionContext actionContext) {
		TextContent textContent = actionContext.getObject("textContent");
		
		return textContent.getText();
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Browser browser = actionContext.getObject("parent");
		TextContent textContent = new TextContent(self, browser, actionContext);
		actionContext.g().put(self.getMetadata().getName(), textContent);
		return textContent;
	}

	@Override
	public void changed(ProgressEvent arg0) {
	}

	@Override
	public void completed(ProgressEvent arg0) {
		ready = true;
		
		if(text != null) {
			SwtUtils.evaluateBrowserScript(browser, "updateXWText()", null, actionContext);
		}
	}
}
