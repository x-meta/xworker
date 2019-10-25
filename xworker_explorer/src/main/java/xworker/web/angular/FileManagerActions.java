package xworker.web.angular;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class FileManagerActions {
	public static void httpDo(ActionContext actionContext) throws ServletException, IOException{
		Thing self = actionContext.getObject("self");
		
		AngularFileManagerServlet af = (AngularFileManagerServlet) self.getData("AngularFileManagerServlet");
		if(af == null){
			af = new AngularFileManagerServlet((String) self.doAction("getBaseUrl", actionContext), (HttpServlet) actionContext.getObject("servlet"));
			self.setData("AngularFileManagerServlet", af);
		}
		
		String method = actionContext.getObject("requestMethod");
		HttpServletRequest request = actionContext.getObject("request");
		HttpServletResponse response = actionContext.getObject("response");
		
		if("POST".equals(method)){
			af.doPost(request, response);
		}else{
			af.doGet(request, response);
		}
	}
	
	
}
