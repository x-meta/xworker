package xworker.rap;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rap.rwt.RWT;
import org.xmeta.ActionContext;
import org.xmeta.ui.session.Session;
import org.xmeta.ui.session.SessionManager;

public class RapSessionManager extends SessionManager{
	//private static Logger logger = Logger.getLogger(RapSessionManager.class.getName());
	
	private static final String KEY = "__xworker.rap.RapSessionManager_session__";
	//private static final String ENV_RAP = "RAP";
	
	/**
	 * 注册会话管理器。
	 */
	public static void regist() {
		boolean have = false;
		for(SessionManager sessionManager : SessionManager.getSessionManagers()) {
			if(sessionManager instanceof RapSessionManager) {
				have = true;
				break;
			}
		}
		
		if(!have) {
			SessionManager.registSessionManager(new RapSessionManager());
		}
		/*
		SessionManager old = SessionManager.getSessionManager(ENV_RAP); 
		if(old == null || !(old instanceof RapSessionManager) ){
			SessionManager sessionManager = new RapSessionManager();			
			SessionManager.setSessionManager(ENV_RAP, sessionManager);						
		}*/
	}

	@Override
	public Session get(ActionContext actionContext) {
		try {
			RapSession session = (RapSession) RWT.getUISession().getAttribute(KEY);
			if(session == null) {
				session = new RapSession();
				RWT.getUISession().setAttribute(KEY, session);
				RWT.getUISession().getHttpSession().setAttribute(KEY, session);
			}
			return session;
		}catch(Exception e) {
			//有可能是通过其它后台线程或其它网页的			
			if(actionContext != null) {
				
				Object reqObj = actionContext.get("request");
				if(reqObj instanceof HttpServletRequest) {
					HttpServletRequest req = (HttpServletRequest) reqObj;
					
					RapSession session = (RapSession) req.getSession().getAttribute(KEY);
					if(session != null) {
						return session;
					}
				}
			}
			
			//使用默认会话，应该存在某种情况没有获取到RAP的环境
			//logger.log(Level.INFO, "RapSessionManager use defualt sessionManager, ");	
			Session session = SessionManager.getDefaultSessionManager().get(actionContext);				
			return session;
		}
	}

	@Override
	public Session delete(ActionContext actionContext) {
		Session session = get(actionContext);
		RWT.getUISession().removeAttribute(KEY);
		return session;
	}

	@Override
	public boolean accept(ActionContext actionContext) {
		try {
			RWT.getUISession();
			return true;
		}catch(Throwable t){
			return false;
		}
	}

}
