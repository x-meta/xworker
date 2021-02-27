package xworker.security;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;

/**
 * 权限动作校验器。
 * 
 * @author Administrator
 *
 */
public class PermissionActionChecker extends SecurityChecker implements java.lang.Comparable<PermissionActionChecker> {
	/** 动作名称 */
	String name;

	/** 内容路径的校验列表 */
	List<PathChecker> pathCheckers = new ArrayList<PathChecker>();
	
	public PermissionActionChecker(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * 甚至一个路径的权限处理器。
	 * 
	 * @param pathRegex
	 * @param securityHandler
	 */
	public void sePathtSecurityHandler(String pathRegex, SecurityHandler securityHandler) {		
		for(PathChecker pc : pathCheckers) {
			if((pc.pathRegex == null && pathRegex == null) || pc.pathRegex.equals(pathRegex)) {
				pc.setSecurityHandler(securityHandler);
				return;
			}
		}
		
		PathChecker pc = new PathChecker(pathRegex);
		pathCheckers.add(pc);
		pc.setSecurityHandler(securityHandler);
		
	}
	
	
	@Override
	public int compareTo(PermissionActionChecker o) {
		if(name == null) {
			//null的排在最后
			return 1;
		}
		
		return name.compareTo(o.name);
	}

	/**
	 * 返回所有已注册的路径校验器列表。
	 * 
	 * @return
	 */
	public List<PathChecker> getPathCheckers() {
		return pathCheckers;
	}

	/**
	 * 执行权限的校验。如有有路径校验器匹配路径，则使用路径校验器校验，否则使用本对象校验。
	 * 
	 * @param env
	 * @param permission
	 * @param action
	 * @param path
	 * @param actionContext
	 * @return
	 */
	public boolean doCheck(String env, String permission, String action, String path, ActionContext actionContext) {
		//优先使用路径校验器来校验一个权限请求
		for(int i=0; i<pathCheckers.size(); i++) {
			PathChecker pc = pathCheckers.get(i);
			if(pc.accept(path)) {
				return pc.doCheck(env, permission, action, path, actionContext);
			}
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
