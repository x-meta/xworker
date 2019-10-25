package xworker.ai.chatterbean;

import org.eclipse.swt.browser.Browser;

import xworker.swt.util.SwtUtils;

public class AliceUtils {
	public static String renderMessageToHtml(String message, boolean isAlice){
		String html = "";
		String script = "<div class=\"media\">";
		if(isAlice){
			script = script + "<div class=\"media-left\"><img class=\"media-object\" src=\"/icons/head/leela.png\" width=\"32\"></div>";			
		}else{
			script = script + "<div class=\"media-left\" width=\"32\"></div>";		
		}
		script = script + "<div class=\"media-body\">";
		//SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//String dstr = sf.format(new Date());
		//script = script + "<h4 class=\"media-heading\">" + dstr + "</h4>" + html;
		if(!isAlice){
			html = "<div align=\"right\" class=\"bg-success\">" + message + "</div>";
		}else{
			html = "<div align=\"left\" class=\"bg-info\">" + message + "</div>";
		}
		script = script + html + "</div>";
		if(!isAlice){
			script = script + "<div class=\"media-right\"><img class=\"media-object\" src=\"/icons/head/frai.png\" width=\"32\"></div>";	
		}else{
			script = script + "<div class=\"media-left\" width=\"32\"></div>";	
		}
		
		return script;
	}
	
	public static void appendHtml(Browser browser, String divId, String html, boolean isAlice){
		String script = "<div class=\"media\">";
		if(isAlice){
			script = script + "<div class=\"media-left\"><img class=\"media-object\" src=\"/icons/head/leela.png\" width=\"32\"></div>";			
		}else{
			script = script + "<div class=\"media-left\" width=\"32\"></div>";		
		}
		script = script + "<div class=\"media-body\">";
		//SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//String dstr = sf.format(new Date());
		//script = script + "<h4 class=\"media-heading\">" + dstr + "</h4>" + html;
		if(!isAlice){
			html = "<div align=\"right\" class=\"bg-success\">" + html + "</div>";
		}else{
			html = "<div align=\"left\" class=\"bg-info\">" + html + "</div>";
		}
		script = script + html + "</div>";
		if(!isAlice){
			script = script + "<div class=\"media-right\"><img class=\"media-object\" src=\"/icons/head/frai.png\" width=\"32\"></div>";	
		}else{
			script = script + "<div class=\"media-left\"><img class=\"media-object\" src=\"\" width=\"32\"></div>";	
		}
		script = script + "</div>";
		script = script.replaceAll("[\"]", "\\\\\"");
		//script = script.replaceAll("\"", "\\\"");		
		script = script.replaceAll("[\\t\\n\\r]", "<br/>");
		script = "$(\"#" + divId + "\").append(\"" + script + "\")";
		//System.out.println(script);
		SwtUtils.evaluateBrowserScript(browser, script, null, null);
		//browser.evaluate(script);
		
		//滚动到底部
		script = "document.body.scrollTop=document.body.scrollHeight";
		SwtUtils.evaluateBrowserScript(browser, script, null, null);
		//browser.evaluate();
	}
}
