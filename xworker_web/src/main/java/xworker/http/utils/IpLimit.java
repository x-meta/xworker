package xworker.http.utils;

import javax.servlet.http.HttpServletRequest;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.util.limit.RegexLimit;

/**
 * IP限制动作。
 * 
 * @author zyx
 *
 */
public class IpLimit {
	private static final String key = "__my_RegexLimit__";
	
	public static boolean run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		RegexLimit rl = (RegexLimit) self.getData(key);
		if(rl == null){
			self.doAction("reset", actionContext);
			rl = (RegexLimit) self.getData(key);
		}
		
		if(rl == null){
			throw new ActionException("RegexLimit not inited, action=" + self.getMetadata().getPath());
		}
		
		HttpServletRequest request = (HttpServletRequest) actionContext.getObject("request");
		if(request == null){
			throw new ActionException("Must be run under http enviroment, action=" + self.getMetadata().getPath());
		}		
		
		String ip = IpUtils.getIpAddress(request);
		return rl.isLimited(ip) || rl.isLimited(ip);
	}
	
	public static void reset(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String ipTables = (String) self.doAction("getIptables", actionContext);		
		
		RegexLimit rl =  (RegexLimit) self.getData(key);
		if(rl == null){
			rl = new RegexLimit(ipTables);
			self.setData(key, rl);
		}
		rl.setConfig(ipTables);
		self.setData(key, rl);
	}
}
