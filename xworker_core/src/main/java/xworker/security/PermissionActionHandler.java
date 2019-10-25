package xworker.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

public class PermissionActionHandler {
	/**
	 * HTTP登录检查。
	 * 
	 * @param actionContext
	 * @return
	 * @throws IOException
	 */
	public static boolean httpLogin(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		if(request == null){
			throw new ActionException("HttpServletRequest not exists, path=" + self.getMetadata().getPath());
		}
		HttpSession session = request.getSession();
		String userAttributeName = self.getStringBlankAsNull("userAttributeName");
		if(userAttributeName == null){
			throw new ActionException("userAttributeName is null, path=" + self.getMetadata().getPath());
		}
		
		if(session.getAttribute(userAttributeName) != null){
			return true;
		}else{
			HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
			response.sendRedirect(self.getString("loginUrl"));
			return false;
		}
	}
}
