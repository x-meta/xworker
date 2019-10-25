package xworker.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.cache.ThingEntry;
import org.xmeta.util.UtilThing;

public class DefaultSecurityHandler implements SecurityHandler{
	private static Logger logger = LoggerFactory.getLogger(DefaultSecurityHandler.class);
	
	ThingEntry entry = null;
	//默认改成false，如果是true那么测试发现RAP可以直接编辑模型等
	boolean defaultAllow = false;
	/** 权限配置列表 */
	List<PermissionConfig> configs = new ArrayList<PermissionConfig>();
	
	public DefaultSecurityHandler(){
		try{
			Thing handler = UtilThing.getThingIfNotExistsCreate("_local.xworker.config.DefaultSecurityHandler", "_local",
					World.getInstance().getThing("xworker.lang.security.DefaultSecirutyHandlerImpl"));
			
			entry = new ThingEntry(handler);
			init();
		}catch(Exception e){
			logger.warn("init DefaultSecurityHandler error", e);
		}
	}
	
	@Override
	public boolean doCheck(String env, String permission, String action,
			String path, ActionContext actionContext) {
		if(entry == null){
			return defaultAllow;
		}
		
		if(entry.isChanged()){
			init();
		}
		
		
		PermissionConfig cfg = null;
		for(PermissionConfig c : configs) {
			if(c.accept(env,permission, action, path, actionContext)) {
				cfg = c;
				break;
			}
		}
					
		if(cfg != null){			
			return cfg.doCheck(env, actionContext);					
		}
		
		return defaultAllow;
	}
	
	
	public boolean isDefaultAllow() {
		return defaultAllow;
	}

	public void setDefaultAllow(boolean defaultAllow) {
		this.defaultAllow = defaultAllow;
	}

	public void init(){
		Thing thing = entry.getThing();
		defaultAllow = thing.getBoolean("defaultAllow");
		
		List<PermissionConfig> configs_ = new ArrayList<PermissionConfig>();
		for(Thing config : thing.getChilds("PermissionConfig")){
			PermissionConfig cfg = new PermissionConfig(config);
			configs_.add(cfg);
		}
		
		this.configs = configs_;
	}

	@Override
	public Thing getThing() {
		return entry.getThing();
	}
	
	static class PermissionConfig{
		byte type;
		String permission;
		String action;
		Pattern pathPattern;
		boolean allow;
		String sessionName;
		String redirectTo;
		String extjsMessage;
		String extjMessageType;
		
		public PermissionConfig(Thing thing){
			this.type = thing.getByte("type");
			this.permission = thing.getStringBlankAsNull("permissionName");
			if(permission == null){
				permission = "";
			}
			this.action = thing.getStringBlankAsNull("actionName");
			if(action == null){
				action = "";
			}
			String pathRegex = thing.getStringBlankAsNull("pathPattern");
			if(pathRegex == null){
				this.pathPattern = Pattern.compile("([\\s\\S]*)");
			}else{
				this.pathPattern = Pattern.compile(pathRegex);
			}
			
			this.allow = thing.getBoolean("allow");
			this.sessionName = thing.getStringBlankAsNull("sessionName");
			this.redirectTo = thing.getStringBlankAsNull("redirectTo");
			this.extjsMessage = thing.getStringBlankAsNull("extjsMessage");
			this.extjMessageType = thing.getStringBlankAsNull("extjMessageType");
		}
		
		/**
		 * 是否接受路径作为条件判断。
		 * 
		 * @param path
		 * @param actionContext
		 * @return
		 */
		public boolean accept(String env, String permission, String action, String path, ActionContext actionContext){
			if(this.permission != null && !this.permission.equals(permission)) {
				return false;
			}
			
			if(this.action != null && !this.action.equals(action)) {
				return false;
			}
			
			if(!pathPattern.matcher(path).matches()){
				return false;
			}
			
			return true;
		}
		
		public boolean doCheck(String env, ActionContext actionContext){
			if(true){
				if(sessionName != null){
					HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
					if(request.getSession().getAttribute(sessionName) != null){
						return true;
					}else{
						HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
						if(extjsMessage != null){
					        if(ServletFileUpload.isMultipartContent(request)){
					        	//extjs提交文件是使用iframe，contentType必须是text/html
					        	response.setContentType("text/html; charset=utf-8");
					        }else{
					        	response.setContentType("text/plain; charset=utf-8");
					        }
					        
					        String code = "{\n" + 
					        	"\"success\":false,\n" + 
					        	"\"msg\":\"" + extjsMessage + "\"\n" + 
					        	"}";
					        
					        if("javascript".equals(extjMessageType)){
					        	code = "alert('" + extjsMessage + "')";
					        }
					        try {
								response.getWriter().println(code);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}else if(redirectTo != null){
							try {
								response.sendRedirect(redirectTo);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						
						return false;
					}
				}else{
					return true;
				}
			}else{
				return false;
			}
		}
	}

}
