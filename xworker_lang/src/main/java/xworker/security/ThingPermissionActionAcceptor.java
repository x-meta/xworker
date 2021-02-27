package xworker.security;

import java.util.regex.Pattern;

import org.xmeta.Thing;

public class ThingPermissionActionAcceptor {
	Pattern permissionPattern;
	Pattern actionPattern;
	long lastmodified = 0;
	private static final String KEY = "__ThingPermissionActionRegex_";
	
	public ThingPermissionActionAcceptor(Thing thing){
		init(thing);
	}
	
	public static ThingPermissionActionAcceptor getThingPermissionActionAcceptor(Thing thing){
		ThingPermissionActionAcceptor regex = (ThingPermissionActionAcceptor) thing.getData(KEY);
		if(regex == null){
			regex = new ThingPermissionActionAcceptor(thing);
			thing.setData(KEY, regex);
		}
		
		if(regex.lastmodified != thing.getMetadata().getLastModified()){
			regex.init(thing);
		}
		
		return regex;
	}
	
	public void init(Thing thing){
		String permission = thing.getStringBlankAsNull("permission");
		String action = thing.getStringBlankAsNull("action");
		if(permission != null){
			permissionPattern = Pattern.compile(permission);
		}else{
			permissionPattern = null;
		}
		if(action != null){
			actionPattern = Pattern.compile(action);
		}else{
			actionPattern = null;
		}
		
		lastmodified = thing.getMetadata().getLastModified();
	}
	
	public boolean accept(String permission, String action){
		if(permission != null && permissionPattern != null && !permissionPattern.matcher(permission).matches()){
			return false;
		}
		
		if(action != null && actionPattern != null && !actionPattern.matcher(action).matches()){
			return false;
		}
		
		return true;
	}
}
