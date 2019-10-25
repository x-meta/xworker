package xworker.app.weixin;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.codes.XmlCoder;
import org.xmeta.util.UtilAction;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

public class WeixinServer {
	private static Logger logger = LoggerFactory.getLogger(WeixinServer.class);
	
	public static void run(ActionContext actionContext) throws Exception{
		Thing self = (Thing) actionContext.get("self");
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
		String method = (String) actionContext.get("requestMethod");
		
		if("GET".equals(method)){
			//服务器校验
			checkUrl(request, response, self, actionContext);
		}else{
			Thing message = new Thing();
			XmlCoder.parse(message, request.getInputStream());
			
			if(UtilAction.getDebugLog(self, actionContext)){
				logger.info("message=" + message.getAttributes());
			}
			self.doAction("messageReceived", actionContext, UtilMap.toMap("message", message));
		}				
	}
	
	public static void messageReceived(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing message = (Thing) actionContext.get("message");
		if(UtilAction.getDebugLog(self, actionContext)){
			logger.info("message=" + message.getAttributes());
		}
	}
	
	/**
	 * 服务器校验URL。
	 * 
	 * @param request
	 * @param response
	 * @param self
	 * @param actionContext
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static void checkUrl(HttpServletRequest request, HttpServletResponse response, Thing self, ActionContext actionContext) throws NoSuchAlgorithmException, IOException{
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");
		
		if(signature == null){
			response.getWriter().println("not a weixing request!");
			return;
		}
		
		if(UtilAction.getDebugLog(self, actionContext)){
		String p = "微信校验参数：\n";
			p = p + "    signature=" + signature + "\n";
			p = p + "    timestamp=" + timestamp + "\n";
			p = p + "    nonce=" + nonce + "\n";		
			p = p + "    echostr=" + echostr + "\n";		
			logger.info(p); 
		}
		//排序
		List<String> list = new ArrayList<String>();
		list.add(timestamp);
		list.add(nonce);
		list.add(self.getString("token"));
		Collections.sort(list);
		
		//sha1加密
		 MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
		 String temp = list.get(0) + list.get(1) + list.get(2);
         digest.update(temp.getBytes());
         byte messageDigest[] = digest.digest();
         String message = UtilString.toHexString(messageDigest);
         
         if(UtilAction.getDebugLog(self, actionContext)){
        	 logger.info("生成的sha1校验：" + message);
         }
         if(signature.toLowerCase().equals(message.toLowerCase())){
        	 response.getWriter().print(echostr);
         }else{
        	 response.getWriter().print("error");
         }
	}
}
