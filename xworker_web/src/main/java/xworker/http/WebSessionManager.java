package xworker.http;

import javax.servlet.http.HttpServletRequest;

import org.xmeta.ActionContext;
import org.xmeta.ui.session.Session;
import org.xmeta.ui.session.SessionManager;

import xworker.util.HttpUtils;

/**
 * WEB会话管理器，本机的请求使用默认会话。
 * 
 * @author zyx
 *
 */
public class WebSessionManager extends SessionManager{
	public static final String KEY = "__xworker_web_session__";
	
	private Session session;
	
	public void setSession(HttpServletRequest req, ActionContext actionContext) {
		WebSession session = (WebSession) req.getSession().getAttribute(KEY);
		if(session == null) {
			if(HttpUtils.isLocalHost(req)) {
				//本机的使用默认会话
				Session parent =  SessionManager.getDefaultSessionManager().get(actionContext);
				session = new WebSession(parent);
			} else {
				session = new WebSession(req.getSession());
			}
			req.getSession().setAttribute(KEY, session);
		}
		
		this.session = session;
	}
	
	@Override
	public Session get(ActionContext actionContext) {
		if(this.session != null) {
			return this.session;
		}
		
		if(actionContext == null) {
			return SessionManager.getDefaultSessionManager().get(actionContext);
		}
		
		Object reqObj = actionContext.get("request");
		if(reqObj instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest) reqObj;
			if(HttpUtils.isLocalHost(req)) {
				//本机的使用默认会话
				return SessionManager.getDefaultSessionManager().get(actionContext);
			}else {
				WebSession session = (WebSession) req.getSession().getAttribute(KEY);
				if(session == null) {
					session = new WebSession(req.getSession());
					req.getSession().setAttribute(KEY, session);
				}
				
				return session;
			}
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
		Object reqObj = actionContext.get("request");
		if(reqObj instanceof HttpServletRequest) {
			return true;
		} else {
			return false;
		}
	}

}
