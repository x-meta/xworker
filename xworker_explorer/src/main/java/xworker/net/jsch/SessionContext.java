package xworker.net.jsch;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import xworker.lang.context.ContextUtil;

public class SessionContext {
	private static Logger log = LoggerFactory.getLogger(SessionContext.class);
	
	
	
	public static void init(ActionContext actionContext) throws  JSchException{
		Thing self = (Thing) actionContext.get("self");
		//World world = World.getInstance();
		ActionContext acContext = (ActionContext) actionContext.get("acContext");
		
		//数据源事物		
		String dsPath = self.getString("dataSourcePath");
		Thing sessionThing = ContextUtil.getThing(dsPath, acContext);

		if(sessionThing != null){
			Session session = (Session) sessionThing.doAction("create", actionContext);
			session.connect();
			
		    Bindings scope = acContext.peek();
		    scope.put(self.getString("sessionName"), session);    
		    actionContext.getScope(0).put("session", session);
		}else{
		    log.warn("SessinThing is null, contextThing=" + self.getMetadata().getName() + ",dataSourcePath=" + dsPath);
		}
	}
	
	public static void success(ActionContext actionContext) throws SQLException{
		Session session = (Session) actionContext.get("session");
		if(session != null){
			session.disconnect();
		}
	}
	
	public static void exception(ActionContext actionContext) throws SQLException{
		Session session = (Session) actionContext.get("session");
		if(session != null){
			session.disconnect();
		}
	}
}
