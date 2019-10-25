package xworker.security;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilThing;

/**
 * 安全管理器。
 * 
 * 默认的权限处理是_local.xworker.config.DefaultSecurityManager，如果不存在尝试使用xworker.lang.security.DefaultSecurityManager
 * 并创建_local下的。
 * 
 * @author Administrator
 *
 */
public class SecurityManager {
	private static Logger logger = LoggerFactory.getLogger(SecurityManager.class);
	
	/** 环境校验器缓存 */
	private static Map<String, EnviromentChecker> envCheckers = new HashMap<String, EnviromentChecker>();
	
	//----------------------常见环境------------------
	public static final String RAP = "RAP";
	
	//---------------------常见权限-------------------
	/** XWorker的管理权限，比如编辑事物的权限 */
	public static final String XWorker_Thing_Manager = "XWorker_Thing_Manager";
	
	//初始化默认的权限处理。
	static {
		try{
			Thing defaultSecurityManager = World.getInstance().getThing("xworker.lang.security.DefaultSecurityManager");
			try{		
				Thing localDefaultSecurityManager = UtilThing.getThingIfNotExistsCreate("_local.xworker.config.DefaultSecurityManager", "_local", defaultSecurityManager);
				if(localDefaultSecurityManager != null) {
					defaultSecurityManager = localDefaultSecurityManager;
				}
			}catch(Exception e) {			
			}
		
			if(defaultSecurityManager != null) {
				defaultSecurityManager.doAction("run");
			}
		}catch(Exception e) {
			logger.error("Init defaultSecurityManager error", e);
		}
	}
	
	public static EnviromentChecker getEnviromentChecker(String env) {
		synchronized(envCheckers) {
			EnviromentChecker envc = envCheckers.get(env);
			if(envc == null) {
				envc = new EnviromentChecker(env);
				envCheckers.put(env, envc);
			}
			
			return envc;
		}
	}
    /**
     * 注册安全处理器。
     * 
     * @param permission
     * @param action
     * @param path
     * @param handler
     */
	public static void registSecurityHandler(String env, String permission, String action, String pathRegex, SecurityHandler handler, ActionContext actionContet){		
		EnviromentChecker envc = getEnviromentChecker(env);
		envc.setSecurityHandler(permission, action, pathRegex, handler);;
	}

	/**
	 * 校验一个权限。
	 * 
	 * @param env
	 * @param permission
	 * @param action
	 * @param path
	 * @param actionContext
	 * @return
	 */
	public static boolean doCheck(String env, String permission, String action, String path , ActionContext actionContext){
		EnviromentChecker envc = envCheckers.get(env);
		if(envc == null) {
			logger.info("EnviromentChecker is null, return false, env={}, permission={}, action={}, path={}",
					env, permission, action, path);
			return false;
		}else {
			return envc.doCheck(env, permission, action, path, actionContext);
		}
	}
	
	/**
	 * 处理安全。为兼容旧的机制而保留的。type==1使用WEB环境校验，其他使用DEFAULT环境校验。
	 * 
	 * @param type
	 * @param permission
	 * @param type
	 * @param path
	 * @param actionContext
	 * @return
	 * @deprecated
	 */
	public static boolean doCheck(byte type, String permission, String action, String path , ActionContext actionContext){
		switch(type) {
		case 1:
			return doCheck("WEB", permission, action, path, actionContext);
		default:
			return doCheck("DEFAULT", permission, action, path, actionContext);
		}
	}
}
