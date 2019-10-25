package xworker.net.jsch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionException;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class UserInfoDefault implements UserInfo, UIKeyboardInteractive {
	private static Logger logger = LoggerFactory.getLogger(UserInfoDefault.class);
	
	String passwd;
	
	public UserInfoDefault(String password){
		this.passwd = password;
	}
	
	@Override
	public String[] promptKeyboardInteractive(String arg0, String arg1,
			String arg2, String[] arg3, boolean[] arg4) {
		throw new ActionException("userInfoDefault not support KeyboardInteractive");
	}

	@Override
	public String getPassphrase() {
		return null;
	}

	@Override
	public String getPassword() {
		return passwd;
	}

	@Override
	public boolean promptPassphrase(String arg0) {
		return true;
	}

	@Override
	public boolean promptPassword(String arg0) {
		return true;
	}

	@Override
	public boolean promptYesNo(String arg0) {
		return true;
	}

	@Override
	public void showMessage(String message) {
		logger.info(message);
	}

}
