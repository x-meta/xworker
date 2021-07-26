package xworker.net.jsch.functions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import com.jcraft.jsch.Session;

import ognl.OgnlException;
import xworker.task.UserTask;
import xworker.ui.UIRequest;
import xworker.ui.function.FunctionRequest;
import xworker.ui.function.FunctionRequestUIFactory;
import xworker.ui.function.FunctionRequestUtil;

public class JschFunctions {
	public static Object scpToWithSessionThing(ActionContext actionContext){
		Thing sessionThing = getSessionThing(actionContext);
		
		//得到会话
		Session session = (Session) sessionThing.doAction("create", actionContext);		
		
		String localFile = (String) actionContext.get("localFile");
		String remoteFile = (String) actionContext.get("remoteFile");
		Boolean background = (Boolean) actionContext.get("background");
		if(background == null){
			background = false;
		}
		return scp("xworker.net.jsch.ScpTo", session, localFile, remoteFile, background, true, actionContext);
	}
	
	public static Object scpToWithSession(ActionContext actionContext){
		//得到会话
		Session session = (Session) actionContext.get("session");
		String localFile = (String) actionContext.get("localFile");
		String remoteFile = (String) actionContext.get("remoteFile");
		Boolean background = (Boolean) actionContext.get("background");
		if(background == null){
			background = false;
		}
		return scp("xworker.net.jsch.ScpTo", session, localFile, remoteFile, background, false, actionContext);
	}
	
	public static Object execWithSessionThing(ActionContext actionContext) throws IOException{
		Thing sessionThing = getSessionThing(actionContext);
		
		//得到会话
		Session session = (Session) sessionThing.doAction("create", actionContext);		
		
		String command = (String) actionContext.get("command");
		Boolean background = (Boolean) actionContext.get("background");
		if(background == null){
			background = false;
		}
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		return exec(session, command, true, actionContext, request, background);
	}
	
	public static Object execWithSession(ActionContext actionContext) throws IOException{
		//得到会话
		Session session = (Session) actionContext.get("session");
		String command = (String) actionContext.get("command");
		Boolean background = (Boolean) actionContext.get("background");
		if(background == null){
			background = false;
		}
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		
		return exec(session, command, false, actionContext, request, background);
	}
	
	public static Object scpFromWithSessionThing(ActionContext actionContext){
		Thing sessionThing = getSessionThing(actionContext);
		
		//得到会话
		Session session = (Session) sessionThing.doAction("create", actionContext);
		String localFile = (String) actionContext.get("localFile");
		String remoteFile = (String) actionContext.get("remoteFile");
		Boolean background = (Boolean) actionContext.get("background");
		if(background == null){
			background = false;
		}
		return scp("xworker.net.jsch.ScpFrom", session, localFile, remoteFile, background, true, actionContext);
	}
	
	public static Thing getSessionThing(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//会话事物
		Object sessionThingObj = actionContext.get("sessionThing");
		Thing sessionThing = null;
		if(sessionThingObj instanceof Thing){
			sessionThing = (Thing) sessionThingObj;
		}else if(sessionThingObj instanceof String){
			sessionThing = World.getInstance().getThing((String) sessionThingObj);
		}
		
		if(sessionThing == null){
			throw new ActionException("sessionThing is null, path=" + self.getMetadata().getPath());
		}
		
		return sessionThing;
	}
	
	public static Object scpFromWithSession(ActionContext actionContext){
		//得到会话
		Session session = (Session) actionContext.get("session");
		String localFile = (String) actionContext.get("localFile");
		String remoteFile = (String) actionContext.get("remoteFile");
		
		Boolean background = (Boolean) actionContext.get("background");
		if(background == null){
			background = false;
		}
		return scp("xworker.net.jsch.ScpFrom", session, localFile, remoteFile, background, false, actionContext);
	}
	
	public static Object scp(String scpThing, Session session, String localFile, String remoteFile, boolean runBackground, boolean closeSession, ActionContext actionContext){
		Bindings bindings = actionContext.peek();
		bindings.put("session", session);
		bindings.put("localFile", localFile);
		bindings.put("remoteFile", remoteFile);
		
		Thing scp = new Thing(scpThing);
		scp.put("sessionVar", "session");
		scp.put("localFile", "ognl:localFile");
		scp.put("remoteFile", "ognl:remoteFile");
		scp.put("closeSession", closeSession);
		
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request != null){
			scp.put("runBackground", true);
		}else{
			scp.put("runBackground", runBackground);
		}
				
		Object obj = scp.doAction("run", actionContext);
		if(request != null && runBackground == false){
			UIRequest uiRequest = new UIRequest(World.getInstance().getThing("xworker.net.jsch.functions.ScpComposite/@composite"), request.getActionContext());
			uiRequest.setRequestMessage(request);
			uiRequest.setData("task", obj);
			FunctionRequestUIFactory.requestUI(request, FunctionRequestUIFactory.UI_DIALOG_COMPOSITE, uiRequest, actionContext);
			return null;
		}else{
			if(request != null){
				FunctionRequestUtil.callbakMyselfOk(request, obj, actionContext);
			}
			return obj;
		}
	}
	
	public static Object exec(Session session, String command, boolean closeSession, ActionContext actionContext, final FunctionRequest request, boolean background) throws IOException{
		Bindings bindings = actionContext.peek();
		bindings.put("session", session);
		
		Thing exec = new Thing("xworker.net.jsch.Exec");
		exec.put("sessionVar", "session");
		exec.put("command", command);
		exec.put("closeSession", closeSession);
		
		//不是静默执行也是后台执行
		if(request != null || background){
			exec.put("runBackground", "true");
		}
		exec.put("outputStream", "errOut");
		
		if(request != null && background == false){
			//如果是前台的非静默的方式执行
			PipedOutputStream pout = new PipedOutputStream();
			PipedInputStream pin = new PipedInputStream(pout);
			exec.put("outputStream", "outputStream");
			UIRequest uiRequest = new UIRequest(World.getInstance().getThing("xworker.net.jsch.functions.ExecComposite/@mainComposite"), request.getActionContext());
			uiRequest.setRequestMessage(request);
			uiRequest.setData("inputStream", pin);
			uiRequest.setData("outputStream", pout);
			exec.put("errStream", "errOut");
			
			UserTask task = (UserTask) exec.doAction("run", actionContext, UtilMap.toMap("errOut", pout, "outputStream", pout));
			uiRequest.setData("task", task);
			FunctionRequestUIFactory.requestUI(request, FunctionRequestUIFactory.UI_DIALOG_COMPOSITE, uiRequest, actionContext);
			return null;
		}else{
			ByteArrayOutputStream errOut = new ByteArrayOutputStream();
			exec.put("errStream", "errOut");
			
			Object result = exec.doAction("run", actionContext, UtilMap.toMap("errOut", errOut));
			if(errOut.size() > 0){
				if(result != null){
					result = result + new String(errOut.toByteArray());
				}else{
					result = new String(errOut.toByteArray());
				}
			}
			
			if(request != null){
				FunctionRequestUtil.callbakMyselfOk(request, result, actionContext);
			}
			return result;
		}
	}
	
	public static Thing getSessonThingFromServer(ActionContext actionContext) throws OgnlException{
		Object server = actionContext.get("server");
		
		String adminIP = UtilData.getString(OgnlUtil.getValue("adminIP", server), null);		
		String adminPort = UtilData.getString(OgnlUtil.getValue("adminPort", server), null);
		String adminUserName = UtilData.getString(OgnlUtil.getValue("adminUserName", server), null);
		String adminPassword = UtilData.getString(OgnlUtil.getValue("adminPassword", server), null);
		
		Thing sessionThing = new Thing("xworker.net.jsch.Session");
		sessionThing.put("username", adminUserName);
		sessionThing.put("password", adminPassword);
		sessionThing.put("host", adminIP);
		sessionThing.put("port", adminPort);
		
		return sessionThing;
	}
}
