package xworker.lang;

import java.util.ArrayList;
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
	private static Map<String, Configuration> cache = new HashMap<>();
	private static String profile;
	private static ThreadLocal<String> profileLocal = new ThreadLocal<String>();

	ThingEntry thingEntry;
	List<Profile> profiles =  new ArrayList<>();;

	private Configuration(Thing thing){
		thingEntry = new ThingEntry(thing);
	}

	public Profile getProfile(String name){
		for(Profile profile : profiles){
			if(name.equals(profile.getName())){
				return profile;
			}
		}

		for(Thing thing : thingEntry.getThing().getChilds("Profile")){
			if(name.equals(thing.getMetadata().getName())){
				Profile profile = new Profile(thing);
				profiles.add(profile);
				return profile;
			}
		}

		return null;
	}

	//初始化系统的配置，如果存在
	static {
		try {
			//profile
			String profile = System.getProperty("xworker.profile");
			if (profile != null) {
				Configuration.profile = profile;
			}

			//configuration
			String configPath = System.getProperty("xworker.configuration");
			if (configPath != null) {
				Thing config = World.getInstance().getThing(configPath);
				if (config != null) {
					try {
						config.doAction("init", new ActionContext());
					} catch (Exception e) {
						Executor.error(TAG, "Configuration init error, config=" + config.getMetadata().getPath(), e);
					}
				}
			}
		}catch(Exception e){
			Executor.error(TAG, "Configuration init error", e);
		}
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

	/**
	 * 返回当前Profile下对应的模型。
	 * @param name 模型的名字
	 * @param currentThing 用于提供包名的模型
	 * @param actionContext 变量上下文
	 * @return 配置的模型，有可能返回null
	 */
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
	 * 返回当前Profile下对应的模型。
	 * 
	 * @param name 模型的名字
	 * @param appCategory 包名，根据包名寻找配置模型
	 * @param actionContext 变量上下文
	 * @return 配置的模型，有可能为null
	 */
	public static Thing getConfiguration(String name, String appCategory, ActionContext actionContext) {
		//从配置中获取
		World world = World.getInstance();
		String profileName = getProfile();
		do{
			String configPath = "Configuration";
			if(appCategory == null || "".equals(appCategory)) {
				appCategory = null;
			}else {
				configPath = appCategory + "." + configPath;
			}

			Configuration configuration = cache.get(configPath);
			if(configuration == null){
				Thing config = world.getThing(configPath);
				if(config != null) {
					configuration = new Configuration(config);
					cache.put(configPath, configuration);
				}
			}
			if(configuration != null){
				Profile profile = configuration.getProfile(profileName);
				if(profile != null){
					Thing resource = profile.getResource(name);
					if(resource != null){
						return resource;
					}
				}
			}
			
			//获取上一级
			if(appCategory != null) {
				int index = appCategory.lastIndexOf(".");
				if(index != -1) {
					//xxx.xxx...
					appCategory = appCategory.substring(0, index);
				}else {
					//xxx
					appCategory = "";
				}
			}
		}while(appCategory != null);
		
		return null;
	}

	private static Thing getProfile(Thing config, ActionContext actionContext) {
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

	//动作模型：xworker.lang.Configuration/@actions/@run
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
	
    public static Object getValues(ActionContext actionContext){
	    //xworker.lang.Configuration/@nameReplacement/@actions/@getValues
	    Thing self = actionContext.getObject("self");
	    
	    Thing thing = self.getParent();
	    
	    List<Thing> list = new ArrayList<Thing>();
	    List<Thing> values = thing.getChilds("Profile");
	    for(Thing value : values){
	        Thing v = new Thing();
	        v.put("label", value.getMetadata().getLabel());
	        v.put("value", value.getMetadata().getName());
	        list.add(v);
	    }
	    
	    return list;
	}

	private static class Profile{
		ThingEntry thingEntry;

		public Profile(Thing thing){
			thingEntry = new ThingEntry(thing);

			init();
		}

		public String getName(){
			Thing thing = thingEntry.getThing();
			if(thing != null){
				return thing.getMetadata().getName();
			}else{
				return null;
			}
		}

		private void init(){
			ActionContext actionContext = new ActionContext();
			Thing profile = thingEntry.getThing();
			if(profile != null){
				try {
					for(Thing child : profile.getChilds()) {
						child.doAction("init", new ActionContext());
					}
				} catch (Exception e) {
					Executor.error(TAG, "Init profile error, profile=" + profile.getMetadata().getPath(), e);
				}
			}
		}

		public Thing getThing(){
			if(thingEntry.isChanged()){
				init();
			}

			return thingEntry.getThing();
		}

		public Thing getResource(String name){
			if(thingEntry.isChanged()){
				init();
			}

			for(Thing child : thingEntry.getThing().getChilds()){
				if(name.equals(child.getMetadata().getName())){
					return child;
				}
			}

			return null;
		}
	}
}
