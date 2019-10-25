package xworker.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.ui.session.Session;
import org.xmeta.ui.session.SessionManager;

/**
 * 会话权限处理器。使用X-Meta引擎的会话管理，如果Session存在且Session中指定的属性的值不为null则返回true,否则返回false。
 * 
 * @author zyx
 *
 */
public class SessionSecurityHandler extends AbstractSecurityHandler{
	List<String> attributeNames = new ArrayList<String>();
	
	public SessionSecurityHandler(Thing thing, ActionContext actionContext){
		super(thing);
		
		Collection<String> names = thing.doAction("getAttributeNames", actionContext);
		if(names != null) {
			attributeNames.addAll(names);
		}
	}

	@Override
	public boolean doCheck(String env, String permission, String action, String path, ActionContext actionContext) {
		Session session = SessionManager.getSession(actionContext);
		if(session != null) {
			for(String sessionName : attributeNames) {
				if(session.getAttribute(sessionName) != null) {
					return true;
				}
			}
		}
		
		return false;
	}

	public static SessionSecurityHandler create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new SessionSecurityHandler(self, actionContext);
	}
	
}
