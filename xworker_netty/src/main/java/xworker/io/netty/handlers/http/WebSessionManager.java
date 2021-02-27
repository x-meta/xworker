package xworker.io.netty.handlers.http;


import org.xmeta.ActionContext;
import org.xmeta.ui.session.Session;
import org.xmeta.ui.session.SessionManager;

import io.netty.channel.ChannelHandlerContext;

public class WebSessionManager extends SessionManager{
	public static final String KEY = "__xworker_web_session__";
	
	private Session session;
	
	public void setSession(HttpSession httpSession, ActionContext actionContext) {
		session = (WebSession) httpSession.getAttribute(KEY);
		if(session == null) {
			session = new WebSession(httpSession);
			httpSession.setAttribute(KEY, session);
		}		
	}
	
	@Override
	public Session get(ActionContext actionContext) {
		if(this.session != null) {
			return this.session;
		}
		
		if(actionContext == null) {
			return SessionManager.getDefaultSessionManager().get(actionContext);
		}
		
				
		Object reqObj = actionContext.get("session");
		if(reqObj instanceof HttpSession) {
			HttpSession httpSession = (HttpSession) reqObj;

			session = (WebSession) httpSession.getAttribute(KEY);
			if(session == null) {
				session = new WebSession(httpSession);
				httpSession.setAttribute(KEY, session);
			}	
			return session;
		}
		
		return SessionManager.getDefaultSessionManager().get(actionContext);
	}

	@Override
	public Session delete(ActionContext actionContext) {
		//不需要删除，由Http自行管理会话的生命周期
		return get(actionContext);
	}

	@Override
	public boolean accept(ActionContext actionContext) {
		Object reqObj = actionContext.get("ctx");
		if(reqObj instanceof ChannelHandlerContext) {
			return true;
		} else {
			return false;
		}
	}

}
