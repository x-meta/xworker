package xworker.security;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.cache.ThingEntry;

import xworker.lang.executor.Executor;

public class WebTextSecurityHandler implements SecurityHandler{
	private static final String TAG = WebTextSecurityHandler.class.getName();
	//private static Logger logger = LoggerFactory.getLogger(WebTextSecurityHandler.class);
	
	ThingEntry thingEntry;
	List<Checker> checkers = new ArrayList<Checker>();
	
	public WebTextSecurityHandler(Thing thing){
		thingEntry = new ThingEntry(thing);
		
		init();
	}
	
	public void init(){
		List<Checker> checkers_ = new ArrayList<Checker>();
		
		Thing thing = thingEntry.getThing();
		if(thing == null) {
			Executor.warn(TAG, "Thing is null, path=" + thingEntry.getPath());
			return;
		}
		
		String web_permissions = thing.getStringBlankAsNull("securityHandler");
		int lcount = 0;
		if(web_permissions != null){
			for(String line : web_permissions.split("[\n]")){
				lcount++;
				line = line.trim();
				if("".equals(line) || line.startsWith("#")){
					continue;
				}
				
				String ls[] = line.split("[,]");
				if(ls.length >= 6){
					Checker chk = new Checker(null, ls[0], ls[1], ls[2], ls[3], ls[4], ls[5], ls);
					checkers_.add(chk);
				}else{
					throw new ActionException("Permission must has 7 fields, line=" + lcount + ", thing=" + thing.getMetadata().getPath());
				}
			}
		}
		
		this.checkers = checkers_;
	}
	
	@Override
	public boolean doCheck(String env, String permission, String action,	String path, ActionContext actionContext) {
		return true;
		/*
		if(thingEntry.isChanged()){
			init();
		}
		
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		if(request == null) {
			logger.warn("HttpRequest is null, can not check, return true as default");
			return true;
		}
		String realIp = HttpUtils.getRealRemoteAddr(request);
		
		for(Checker chk : checkers){
			if(!chk.matches(chk.p_permission, permission)){
				continue;
			}
			if(!chk.matches(chk.p_action, action)){
				continue;
			}
			if(!chk.matches(chk.p_path, path)){
				continue;
			}
			if(!chk.matches(chk.p_ip, realIp)){
				continue;
			}
			if(chk.sessionName != null && !"".equals(chk.sessionName) && !"*".equals(chk.sessionName)){				
				if(request.getSession().getAttribute(chk.sessionName) == null){
					continue;
				}
			}
			
			if(chk.allow == false) {
				logger.info("Check result={},env={}, permission={}, action={}, path={}, ip={}, session={}",
						chk.allow, env, permission, action, path, realIp, chk.sessionName);
			}
			
			if(chk.allow == false){
				Thing thing = thingEntry.getThing();
				String ac = "securityCheckFailed";
				if(chk.params.length >= 8){
					ac = chk.params[7];
				}
				thing.doAction(ac, actionContext, "env", env, "permission",permission, "action", action, "path", path);
			}
			return chk.allow;
		}
		
		logger.info("Check result={},env={}, permission={}, action={}, path={}, ip={}, session={}",
				"false", env, permission, action, path, realIp, null);
		return false;*/
	}

	
	
	@Override
	public Thing getThing() {
		return thingEntry.getThing();
	}
	
	static class Checker{
		Pattern p_type;
		Pattern p_permission;
		Pattern p_action;
		Pattern p_path;
		Pattern p_ip;
		String sessionName;
		String[] params;
		boolean allow;
		
		public Checker(String type, String permission, String action, String path, String ip, String sessionName, String allow, String[] params){
			p_type = getPattern(type);
			p_permission = getPattern(permission);
			p_action = getPattern(action);
			p_path = getPattern(path);
			p_ip = getPattern(ip);
			this.sessionName = sessionName;
			if("1".equals(allow)){
				this.allow = true;
			}else{
				this.allow = false;
			}
			this.params = params;
		}
		
		public Pattern getPattern(String str){
			if(str == null || "".equals(str.trim()) || "*".equals(str)){
				return Pattern.compile(".*");
			}else{
				return Pattern.compile(str);
			}
		}
		
		public boolean matches(Pattern p, String str){
			if(str == null || p.matcher(str).matches()){
				return true;
			}else{
				return false;
			}
		}
	}

	public static SecurityHandler createSecurityHandler(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		return new WebTextSecurityHandler(self);
	}
}
