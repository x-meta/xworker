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
	
	public static void main(String args[]){
		String bstr = "60, 112, 32, 115, 116, 121, 108, 101, 61, 34, 98, 97, 99, 107, 103, 114, 111, 117, 110, 100, 45, 99, 111, 108, 111, 114, 58, 114, 103, 98, 40, 50, 53, 53, 44, 32, 49, 54, 53, 44, 32, 48, 41, 34, 62, -26, -120, -111, 58, 32, 50, 48, 49, 52, 45, 48, 53, 45, 48, 53, 38, 110, 98, 115, 112, 59, 49, 54, 58, 51, 55, 58, 49, 57, 60, 98, 114, 47, 62, -24, -127, -118, -27, -92, -87, -27, -73, -78, -27, -69, -70, -25, -85, -117, -17, -68, -116, -25, -108, -88, -26, -120, -73, -26, -96, -121, -24, -81, -122, -17, -68, -102, 38, 110, 98, 115, 112, 59, -26, -118, -128, -26, -100, -81, -26, -108, -81, -26, -116, -127, -17, -68, -116, -25, -108, -88, -26, -120, -73, -27, -112, -115, -25, -89, -80, -17, -68, -102, 49, 49, 49, 49, 49, 60, 47, 112, 62, 60, 98, 114, 47, 62, 0, 0, 0, 19, 50, 48, 49, 52, 45, 48, 53, 45, 48, 53, 32, 49, 54, 58, 51, 55, 58, 49, 57, 0, 0, 99, 60, 112, 32, 115, 116, 121, 108, 101, 61, 34, 98, 97, 99, 107, 103, 114, 111, 117, 110, 100, 45, 99, 111, 108, 111, 114, 58, 114, 103, 98, 40, 50, 53, 53, 44, 32, 49, 54, 53, 44, 32, 48, 41, 34, 62, -26, -120, -111, 58, 32, 50, 48, 49, 52, 45, 48, 53, 45, 48, 53, 38, 110, 98, 115, 112, 59, 49, 54, 58, 51, 55, 58, 50, 52, 60, 98, 114, 47, 62, 110, 105, 104, 97, 111, 38, 110, 98, 115, 112, 59, 60, 47, 112, 62, 60, 98, 114, 47, 62";
		String bbs[] = bstr.split("[,]");
		byte[] bs = new byte[bbs.length];
		for(int i=0; i<bs.length; i++){
			bs[i] = Byte.parseByte(bbs[i].trim());
		}
		System.out.println(new String(bs));;
	}
}
