package xworker.app.weixin;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

public class MessageActions {
	/**
	 * 用在处理微信消息的文本处理器。
	 * 
	 * @param actionContext
	 * @throws IOException 
	 */
	public static void textMessageHandler(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
		Thing message = (Thing) actionContext.get("message");
		
		String content = message.getString("Content");
		String resContent = (String) self.doAction("handleText", actionContext, UtilMap.toMap("message", content));
		
		response.setContentType("text/plain; charset=utf-8");
		String xml = "<xml>\n";
		xml = xml + "<ToUserName><![CDATA[" + message.getString("FromUserName") + "]]></ToUserName>\n";
		xml = xml + "<FromUserName><![CDATA[" + message.getString("ToUserName") + "]]></FromUserName>\n";
		xml = xml + "<CreateTime>" + new Date().getTime() + "</CreateTime>\n";
		xml = xml + "<MsgType><![CDATA[text]]></MsgType>\n";
		xml = xml + "<Content><![CDATA[" + resContent + "]]></Content>\n";
		xml = xml + "</xml>";
		
		response.getWriter().println(xml);
	}
	
	public static String handleText(ActionContext actionContext){
		return (String) actionContext.get("message");
	}
}
