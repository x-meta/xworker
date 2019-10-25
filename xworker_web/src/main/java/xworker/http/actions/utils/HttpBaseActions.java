package xworker.http.actions.utils;

import javax.servlet.http.HttpServlet;

import org.xmeta.ActionContext;
import org.xmeta.World;

public class HttpBaseActions {
	public static String getWebRootDir(ActionContext actionContext){
		HttpServlet servlet = actionContext.getObject("servlet");
		if(servlet != null){
			return servlet.getServletContext().getRealPath("/");
		}else{
			return World.getInstance().getPath() + "/webroot/";
		}
	}
}
