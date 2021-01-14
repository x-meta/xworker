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
	//private static final String CONFIG_PROFILE_KEY = "__cofnig__profile__";
	//配置缓存
	private static Map<String, ThingEntry> cache = new HashMap<String, ThingEntry>();	
	private static String profile;
	private static ThreadLocal<String> profileLocal = new ThreadLocal<String>();
	
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
		if(profile == null || profile.equals(Configuration.profile)) {
			return;
		}
		
		//清空原有的缓存
		cache.clear();
		Configuration.profile = profile;
	}
	
	
	/**
	 * 获取当前的配置，有限返回Local的设置。
	 * 
	 * @return
	 */
	public static String getProfile() {
		String p = profileLocal.get();
		if(p != null) {
			return p;
		}else {
			return profile;
		}
	}
	
	/**
	 * 设置ProfileLocal，应改在finally中调用clearProfileLocal()。
	 * 
	 * @param profile
	 */
	public static void setProfileLocal(String profile) {
		profileLocal.set(profile);
	}
	
	/**
	 * 清除ProfileLocal。
	 */
	public static void clearProfileLocal() {
		profileLocal.set(null);;
	}
	
	public static Thing getConfiguration(String name, Thing currentThing, ActionContext actionContext) {
		int index = name.indexOf(":");
		if(index != -1) {
			String nname = name.substring(0, index);
			String category = name.substring(index + 1, name.length());
			return getConfiguration(nname, category, actionContext);
		}else {
			String appCategory = currentThing.getMetadata().getCategory().getName();
			
			return getConfiguration(name, appCategory, actionContext);
		}
	}
	
	/**
	 * 获取配置。
	 * 
	 * @param name
	 * @param appCategory
	 * @param actionContext
	 * @return
	 */
	public static Thing getConfiguration(String name, String appCategory, ActionContext actionContext) {
		//首先从缓存中获取
		String key = name + "_" + "_" + appCategory;
		ThingEntry entry = cache.get(key);
		if(entry != null) {	
			if(entry.isChanged()) {
				cache.remove(key);
			}else {
				Thing config = entry.getThing();
				if(config != null) {
					return config;
				}else {
					cache.put(key, null);
				}
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
						if(child.getMetadata().getName().equals(name)) {
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
		String profileName = getProfile();
		if(profileName == null) {						
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
