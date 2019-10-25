package xworker.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;

/**
 * 环境，是需要执行校验时的环境，如WEB、RAP、LOCAL等。
 * 
 * @author zyx
 *
 */
public class EnviromentChecker extends SecurityChecker{
	/** 环境的名称 */
	String name;
	
	/** 注册到该环境下的权限 */
	Map<String, PermissionChecker> permissions = new HashMap<String, PermissionChecker>();
	
	public EnviromentChecker(String name) {
		this.name = name;
	}

	/**
	 * 返回环境的名称。
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 注册一个权限处理器，权限处理器可能会设置到不同校验器上。
	 *     permission == null，设置到当前对象上。
	 *     action == null && pathRegex == null，那么设置到权限校验器上，。
	 *     pathRegex != null，设置到路径校验器上。
	 *     action != null && pathRegex == null，设置到动作校验器上。
	 * 
	 * @param permission
	 * @param action;
	 * @param pathRegex
	 * @param securityHandler
	 */
	public void setSecurityHandler(String permission, String action, String pathRegex, SecurityHandler securityHandler) {
		if(permission == null) {
			this.setSecurityHandler(securityHandler);
		}else {
			PermissionChecker pc = getPermissionChecker(permission);
			if(action == null && pathRegex == null) {
				pc.setSecurityHandler(securityHandler);
			}else {
				pc.setSecurityHandler(action, pathRegex, securityHandler);
			}
		}
	}
	
	/**
	 * 通过权限的名字返回权限校验器，如果不
	 * 
	 * @param permission
	 * @return
	 */
	public PermissionChecker getPermissionChecker(String permission) {
		synchronized(permissions) {
			PermissionChecker p = permissions.get(permission);
			if(p == null) {
				p = new PermissionChecker(permission);
				permissions.put(permission, p);
			}
			
			return p;
		}
	}
	
	/**
	 * 返回所有已注册的权限校验器的列表。
	 * 
	 * @return
	 */
	public List<PermissionChecker> getPermissionCheckers(){
		synchronized(permissions) {
			List<PermissionChecker> checkers = new ArrayList<PermissionChecker>();
			for(String key : permissions.keySet()) {
				checkers.add(permissions.get(key));
			}
			
			Collections.sort(checkers);
			return checkers;
		}
	}
	
	/**
	 * 执行权限的校验。使用安全校验器检验，如果没有设置安全校验器，默认返回false。
	 * 如果permission != null，使用权限校验器校验。
	 * 
	 * @param env
	 * @param permission
	 * @param action
	 * @param path
	 * @param actionContext
	 * @return
	 */
	public boolean doCheck(String env, String permission, String action, String path, ActionContext actionContext) {
		//优先使用权限校验器来校验一个权限请求
		PermissionChecker checker = permissions.get(permission);
		if(checker != null) {
			return checker.doCheck(env, permission, action, path, actionContext);
		}
		
		//使用本对象的校验器
		if(securityHandler != null) {
			return securityHandler.doCheck(env, permission, action, path, actionContext);
		}else {
			//默认返回false
			return false;
		}
	}
}
