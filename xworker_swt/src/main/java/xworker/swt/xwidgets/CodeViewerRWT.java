package xworker.swt.xwidgets;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionParams;

import xworker.swt.util.SwtUtils;
import xworker.util.UtilTemplate;
import xworker.util.XWorkerUtils;

public class CodeViewerRWT {
	private static Logger logger = LoggerFactory.getLogger(CodeViewerRWT.class);
	
	public static void setCodeActions(ActionContext actionContext) throws Throwable{
		Browser browser = actionContext.getObject("browser");
		String codeName = actionContext.getObject("codeName");
		String code = actionContext.getObject("code");
		setCode(browser, codeName, code, actionContext);
	}
	
	public static void setCode(Browser browser, String codeName, String code, ActionContext actionContext) throws Throwable{
		if(code == null){
			code = "";
		}else{
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("str", code);
			code = UtilTemplate.process(context, "xworker/ide/worldExplorer/swt/dialogs/StringToHtml.ftl", "freemarker");
		}
				
		//Pattern CRLF = Pattern.compile("(\r\n|\r|\n|\n\r)");
		//Matcher m = CRLF.matcher(code);
		//if (m.find()) {
		//	code = m.replaceAll("<br/>");
		//}
		
		String html = "<pre class=\"brush: " + codeName + ";\" id=\"codePre\">" + code + "</pre>";
		browser.setData("html",html);
		
		//System.out.println(html);
		if(SwtUtils.isRWT()){
			Thing swt = World.getInstance().getThing("xworker.swt.SWT");
			swt.doAction("rwtBrowserEvaluate", actionContext, "browser", browser, "script", "setHtml()", "callback", null);
		}else{
			//browser.evaluate(script);
			browser.evaluate("setHtml()");
			
			//browser.evaluate("$(\"div#content\").html('<b>hello</b>')");
			//browser.evaluate("alert($(\"#content\"))");
		}
	}
	
	@ActionParams(names="browser")
	public static void init(final Browser browser, final ActionContext actionContext){
		final Thing codeViewerThing = actionContext.getObject("codeViewerThing");
		final String webUrl = XWorkerUtils.getWebUrl() + "do?sc=xworker.swt.xwidgets.prototypes.CodeViewerRWTControl";
		if(codeViewerThing != null) {
			String codeName = codeViewerThing.doAction("getCodeName", actionContext);
			String code = codeViewerThing.doAction("getCode", actionContext);
			
			if(codeName != null && code != null){
				String html = "<pre class=\"brush: " + codeName + ";\" id=\"codePre\">" + code + "</pre>";
				browser.setData("html",html);				
			}
		}
		
		browser.addLocationListener(new LocationListener() {

			@Override
			public void changing(LocationEvent event) {
			}

			@Override
			public void changed(LocationEvent event) {
				new BrowserFunction(browser, "getHtml"){
					@Override
					public Object function(Object[] arg0) {
						Object html = browser.getData("html");
						if(html == null) {
							return "";
						}else {
							return html;
						}
					}
					
				};
			}
			
		});
		browser.setUrl(webUrl);	
	}
}
