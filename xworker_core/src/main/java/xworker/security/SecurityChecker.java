package xworker.security;

import org.xmeta.ActionContext;

/**
 * 权限校验器。
 * 
 * @author zyx
 *
 */
public class SecurityChecker {
	/** 处理环境的安全处理器 ，用于校验是否有权限 */
	SecurityHandler securityHandler;
	
	public SecurityHandler getSecurityHandler() {
		return securityHandler;
	}

	public void setSecurityHandler(SecurityHandler securityHandler) {
		this.securityHandler = securityHandler;
	}
	
	/**
	 * 执行权限的校验。使用安全校验器检验，如果没有设置安全校验器，默认返回false。
	 * 
	 * @param permission
	 * @param action
	 * @param path
	 * @param actionContext
	 * @return
	 */
	public boolean doCheck(String env, String permission, String action, String path, ActionContext actionContext) {
		if(securityHandler != null) {
			return securityHandler.doCheck(env, permission, action, path, actionContext);
		}else {
			return false;
		}
	}
}
