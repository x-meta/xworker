package xworker.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;

/**
 * 权限校验器。
 * 
 * @author Administrator
 *
 */
public class PermissionChecker extends SecurityChecker implements java.lang.Comparable<PermissionChecker>  {
	String name;
	
	/** 动作校验器缓存 */
	Map<String, PermissionActionChecker> actions = new HashMap<String, PermissionActionChecker>();
	
	public PermissionChecker(String name){
		this.name = name;
	}
	
	/**
	 * 设置权限处理器，如果pathRegex==null那么设置到动作校验器上，否则设置到路径校验器上。
	 * 
	 * @param action
	 * @param pathRegex
	 * @param securityHandler
	 */
	public void setSecurityHandler(String action, String pathRegex,  SecurityHandler securityHandler) {
		if(pathRegex != null) {
			getPermissionActionChecker(action).sePathtSecurityHandler(pathRegex, securityHandler);
		}else {
			getPermissionActionChecker(action).setSecurityHandler(securityHandler);
		}
	}
	
	public List<String> getActions(){
		List<String> list = new ArrayList<String>();
		for(String key : actions.keySet()) {
			list.add(String.valueOf(key));
		}
		
		Collections.sort(list);
		return list;
	}
	
	/**
	 * 返回动作权限校验器，如果不存在则创建一个。
	 * 
	 * @param action
	 * @return
	 */
	public PermissionActionChecker getPermissionActionChecker(String action) {
		synchronized(actions) {
			PermissionActionChecker  pac = actions.get(action);
			if(pac == null) {
				pac = new PermissionActionChecker(action);
				actions.put(action, pac);
			}
			
			return pac;
		}
	}
	
	/**
	 * 返回所有已注册的动作校验器列表。
	 * 
	 * @return
	 */
	public List<PermissionActionChecker> getPermissionActionCheckers(){
		synchronized(actions) {
			List<PermissionActionChecker> checkers = new ArrayList<PermissionActionChecker>();
			for(String key : actions.keySet()) {
				PermissionActionChecker checker = actions.get(key);
				checkers.add(checker);
			}
			
			Collections.sort(checkers);
			return checkers;
		}
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(PermissionChecker o) {
		if(name == null) {
			//null的排在最后面			
			return 1;
		}
		return name.compareTo(o.name);
	}
	
	/**
	 * 执行权限的校验。如果对用的动作校验器存在，则使用动作校验器校验，否则使用本对象校验。
	 * 
	 * @param env
	 * @param permission
	 * @param action
	 * @param path
	 * @param actionContext
	 * @return
	 */
	public boolean doCheck(String env, String permission, String action, String path, ActionContext actionContext) {
		//优先使用动作校验器来校验一个权限请求
		PermissionActionChecker actionChecker = actions.get(action);
		if(actionChecker != null) {
			return actionChecker.doCheck(env, permission, action, path, actionContext);
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
