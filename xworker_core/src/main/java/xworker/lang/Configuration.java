package xworker.lang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.cache.ThingEntry;

import xworker.lang.executor.Executor;

public class Configuration {
	private static final String TAG = Configuration.class.getName();
	//配置缓存
	private static Map<String, ThingEntry> cache = new HashMap<String, ThingEntry>();	
	static String profile;
	
	//初始化系统的配置，如果存在
	static {
		//profile
		String profile = System.getProperty("xworker.profile");
		if(profile != null) {
			Configuration.profile = profile; 
		}
		
		//configuration
		String configPath = System.getProperty("xworker.configuration");
		if(configPath != null) {
			Thing config = World.getInstance().getThing(configPath);
			if(config != null) {
				try {
					config.doAction("init", new ActionContext());
				}catch(Exception e) {
					Executor.error(TAG, "Configuration init error, config=" + config.getMetadata().getPath(), e);
				}
			}
		}
	}
	
	public static void init() {		
	}
	
	public static void setProfile(String profile) {
		Configuration.profile = profile;
	}
	
	public static String getProfile() {
		return profile;
	}
	
	public static Thing getConfiguration(String name, String descriptor, Thing currentThing, ActionContext actionContext) {
		int index = name.indexOf(":");
		if(index != -1) {
			String nname = name.substring(0, index);
			String category = name.substring(index + 1, name.length());
			return getConfiguration(nname, descriptor, category, actionContext);
		}else {
			String appCategory = currentThing.getMetadata().getCategory().getName();
			
			return getConfiguration(name, descriptor, appCategory, actionContext);
		}
	}
	
	/**
	 * 获取配置。
	 * 
	 * @param name
	 * @param descriptor
	 * @param appCategory
	 * @return
	 */
	public static Thing getConfiguration(String name, String descriptor, String appCategory, ActionContext actionContext) {
		//首先从缓存中获取
		String key = name + "_" + descriptor + "_" + appCategory;
		ThingEntry entry = cache.get(key);
		if(entry != null) {
			Thing config = entry.getThing();
			if(config != null) {
				return config;
			}else {
				cache.put(key, null);
			}
		}
		
		//从配置中获取
		World world = World.getInstance();
		do{
			String configPath = "Configuration";
			if(appCategory == null || "".equals(appCategory)) {
				appCategory = null;
			}else {
				configPath = appCategory + "." + configPath;
			}
			
			Thing config = world.getThing(configPath);
			if(config != null) {
				Thing profile = getProfile(config, actionContext);
				
				if(profile != null) {
					for(Thing child : profile.getChilds()) {
						if(child.isThing(descriptor) && child.getMetadata().getName().equals(name)) {
							cache.put(key, new ThingEntry(child));
							return child;
						}
					}
				}
			}
			
			//获取上一级
			if(appCategory != null) {
				int index = appCategory.lastIndexOf(".");
				if(index != -1) {
					//xxx.xxx...
					appCategory = appCategory.substring(0, index);
				}else if(!"".equals(appCategory)) {
					//xxx
					appCategory = "";
				}else {
					//已经是根了
					appCategory = null;
				}
			}
		}while(appCategory != null);
		
		return null;
	}
		
	public static Thing getProfile(Thing config, ActionContext actionContext) {
		Thing profile = null;
		List<Thing> profiles = config.getChilds("Profile");
		String profileName = Configuration.profile;
		if(profileName != null) {						
			profileName = config.doAction("getProfile", actionContext);
		}
		if(profileName == null) {
			if(profiles.size() > 0) {
				profile = profiles.get(0);
			}
		}else {
			for(Thing p : profiles) {
				if(p.getMetadata().getName().equals(profileName)) {
					profile = p;
					break;
				}
			}
		}
		
		return profile;
	}
	
	public static void init(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Thing profile = getProfile(self, actionContext);
		if(profile != null) {
			for(Thing child : profile.getChilds()) {
				child.doAction("init", actionContext);
			}
		}
		
		self.doAction("doInit", actionContext);
	}
}
