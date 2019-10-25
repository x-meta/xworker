package xworker.swt.browser;

import org.eclipse.swt.browser.Browser;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.util.SwtUtils;
import xworker.util.XWorkerUtils;

public class TextContent {
	Thing thing;
	Browser browser;
	ActionContext actionContext;
	ActionContext parentContext;
	String text;
	
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
		
		if(thing.doAction("isLoadUrl", parentContext)) {
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
}
