package xworker.net.jsch;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import xworker.util.XWorkerUtils;

public class JschActions {	
	static {
		Thing config = null;
		try {
			config = XWorkerUtils.getThingIfNotExistsCreate("_local.xworker.config.JschConfig", "_local", "xworker.net.jsch.JschConfig");
			if(config != null) {
				String preferredAuthentications = config.getStringBlankAsNull("preferredAuthentications");
				if(preferredAuthentications != null) {
					JSch.setConfig("PreferredAuthentications", preferredAuthentications);
				}
			}
		}catch(Exception e) {			
		}
	}
	
	private static JSch jsch = new JSch();
	
	public static JSch getJSch(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String jschPath = self.getStringBlankAsNull("jsch");
		if(jschPath != null){
			Thing thing = World.getInstance().getThing(jschPath);
			if(thing == null){
				throw new ActionException("JschThing is not exists, path=" + jschPath);
			}else{
				return (JSch) thing.doAction("create", actionContext);
			}
		}else{
			return jsch;
		}
	}
	
	public static Session createSession(ActionContext actionContext) throws JSchException{
		Thing self = (Thing) actionContext.get("self");
		JSch jsch = (JSch) self.doAction("createJSch", actionContext);
		if(jsch == null){
			throw new ActionException("Jsch is null, sessionPath=" + self.getMetadata().getPath());
		}
				
		String username = self.getStringBlankAsNull("username");		
		String host = self.getStringBlankAsNull("host");
		int port = self.getInt("port", 22);
		
		Session session = jsch.getSession(username, host, port);	
		UserInfo userInfo = (UserInfo) self.doAction("createUserInfo", actionContext);
		if(userInfo != null){
			session.setUserInfo(userInfo);
		}
		
		session.connect();
		return session;
		
	}
	
	public static UserInfo createUserInfo(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String userInfoStr = self.getStringBlankAsNull("userInfo");
		if("UserInfoSwing".equals(userInfoStr)){
			String password = self.getStringBlankAsNull("password");
			UserInfo userInfo = new UserInfoSwing(password);
			return userInfo;
		}else {
			String password = self.getStringBlankAsNull("password");
			UserInfo userInfo = new UserInfoDefault(password);
			return userInfo;
		}
	}
	
	public static JSch createJSch(ActionContext actionContext) throws JSchException {
		Thing self = actionContext.getObject("self");
		
		String key = "__jsch_key__"; 
		JSch jsch = self.getStaticData(key);
		if(jsch == null) {
			jsch = new JSch();
			String knownHosts = self.doAction("getKnownHosts", actionContext);
			if(knownHosts != null && !"".equals(knownHosts)) {
				jsch.setKnownHosts(knownHosts);
			}
			
			for(Thing child : self.getChilds()) {
				child.doAction("create", actionContext, "jsch", jsch);
			}
			
			self.setStaticData(key, jsch);
		}
		
		return jsch;
	}
	
	public static void createConfigs(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		//JSch jsch = actionContext.getObject("jsch");
		
		for(Thing child : self.getChilds()) {
			String value = child.getStringBlankAsNull("value");
			if(value != null) {
				JSch.setConfig(child.getMetadata().getName(), value);
			}
		}
	}
	
	public static void createIdentities(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
	}
	
	public static void createIdentity(ActionContext actionContext) throws JSchException {
		Thing self = actionContext.getObject("self");
		JSch jsch = actionContext.getObject("jsch");
		String prvkey = self.doAction("getPrvkey", actionContext);
		String pubkey = self.doAction("getPubkey", actionContext);
		byte[] passphrase = self.doAction("getPassphrase", actionContext);
		
		if(prvkey != null) {
			if(pubkey != null && passphrase != null) {
				jsch.addIdentity(prvkey, pubkey, passphrase);
			}else if(passphrase != null) {
				jsch.addIdentity(prvkey, passphrase);
			}else {
				jsch.addIdentity(prvkey);
			}
		}
				
	}
}
