package xworker.util;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.xmeta.*;
import org.xmeta.util.JarThingManagerIniter;
import xworker.lang.executor.Executor;
import xworker.task.TaskManager;

/**
 * 事物注册的工具类。
 * 
 * @author zyx
 *
 */
public class ThingRegistUtils {
	private static final String TAG = ThingRegistUtils.class.getName();

	/**
	 * 目前是对于临时项目下的事物的索引缓存，如用户在XWorker之外创建的项目。
	 *
	 */
	private static Map<String, List<Thing>> registThingCache = null;

	/**
	 * 事物注册
	 */
	private static final ThreadLocal<List<String>> searchRegistLocal = new ThreadLocal<>();


	private static final ThreadLocal<Boolean> disableRegistMyChilds = new ThreadLocal<>();

	private static final Object lockObj = new Object();
	private static boolean cacheTaskStarted = false;
	private static boolean firstCacheFinised = false;

	/**
	 * try{disableRegistMyChilds()} finally {enableRegistMyChilds()};
	 */
	public static void disableRegistMyChilds() {
		disableRegistMyChilds.set(true);
	}

	public static void enableRegistMyChilds() {
		disableRegistMyChilds.remove();
	}

	public static boolean isFirstCacheFinised(){
		return firstCacheFinised;
	}

	public static boolean isDisableRegistMyChilds() {
		Boolean b = disableRegistMyChilds.get();
		if(b != null) {
			return b;
		}else {
			return false;
		}
	}


	/**
	 * 启动注册事物缓存，只针对XWorker之外项目下的事物注册缓存。
	 */
	public static void startRegistThingCache(){
		synchronized (lockObj){
			if(!cacheTaskStarted){
				cacheTaskStarted = true;

				//应该不用频繁刷新的，当使用了ThingManagerListener后是否可以停止定时执行？
				TaskManager.getScheduledExecutorService().scheduleAtFixedRate(() -> {
					try{
						//logger.info("start refresh regist thing cache");
						while(!JarThingManagerIniter.isInited()){
							Thread.sleep(100);
						}

						refreshRegistThingCache();
					}catch(Throwable e){
						Executor.error(TAG, "refreshRegistThingCache error", e);
					}finally {
						if(!firstCacheFinised) {
							firstCacheFinised = true;
						}
					}
				}, 0, 600000, TimeUnit.MILLISECONDS);

				//监控模型的改变，如果是非内部管理的项目的事物，更新索引
				World.getInstance().registThingManagerListener("*", new ThingManagerListener() {

					@Override
					public void loaded(ThingManager thingManager, Thing thing) {
					}

					@Override
					public void saved(ThingManager thingManager, Thing thing) {
						if(thingManager != null && !isInnerThingManager(thingManager.getName())) {
							initRegistThingCache(thing, ThingRegistUtils.registThingCache);
						}
					}

					@Override
					public void removed(ThingManager thingManager, Thing thing) {
					}
				});
			}
		}
	}

	/**
	 * 返回是否是XWorker的内部事物管理器，内部事物管理器的索引是保存到数据库中的。
	 */
	public static boolean isInnerThingManager(String name) {
		//innerThingManagers是XWorker在创建索引时加入的，创建索引时能够访问到的事务管理器为内部事物管理器
		Thing innerThingManagers = World.getInstance().getThing("_local.xworker.config.InnerThingManagers");
		if(innerThingManagers != null) {
			for(Thing child : innerThingManagers.getChilds()) {
				if(child.getMetadata().getName().equals(name)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 刷新注册缓存，这些注册缓存是保存到内存中的。
	 */
	public static void refreshRegistThingCache(ThingManager thingManager, Map<String, List<Thing>> cache) {
		thingManager.getCategory(null).refresh(true);
		Iterator<Thing> iter = thingManager.iterator(null, true);
		while(iter.hasNext()){
			Thing thing = iter.next();

			initRegistThingCache(thing, cache);
		}

		//排除无效的
		for(String key : cache.keySet()){
			List<Thing> things = cache.get(key);
			for(int i=0; i<things.size(); i++){
				Thing thing = things.get(i);
				if((thing.getParent() != null && !thing.getBoolean("th_createIndex"))
						|| thing.getMetadata().isRemoved()){
					//logger.info("removed " + thing.getMetadata().getPath());
					things.remove(i);
					i--;
				}
			}
		}
	}


	/**
	 * 下一次searchRegistThings的额外加入的事物列表，只对当前线程起效，只作用一次。
	 */
	public static void setSearchRegistLocal(List<String> things) {
		searchRegistLocal.set(things);
	}

	public static List<String> getSearchRegistLocal(){
		return searchRegistLocal.get();
	}

	/**
	 * 刷新注册缓存，把非XWorker内部管理的事物管理刷新到内存缓存中。
	 * 刷新时会先清空已有的缓存。
	 */
	private static void refreshRegistThingCache(){
		Map<String, List<Thing>> cache = new HashMap<>();
		//ClasstThingManager是否包含在其中，如果不包含，那么应该初始化一次

		for(ThingManager thingManager : World.getInstance().getThingManagers()) {
			if(!isInnerThingManager(thingManager.getName())) {
				refreshRegistThingCache(thingManager, cache);
			}
		}

		ThingRegistUtils.registThingCache = cache;
	}

	/**
	 * 刷新一个事物的注册缓存。
	 */
	public static void initRegistThingCache(Thing thing, Map<String, List<Thing>> cache){
		if(thing == null || cache == null){
			return;
		}
		if(thing.getBoolean("th_createIndex") || thing.getParent() == null){
			String th_registThing = thing.getStringBlankAsNull("th_registThing");
			//String th_registThing = thing.getStringBlankAsNull("th_registThing");
			//logger.info("registThing:" + th_registThing + ", tihng=" + thing.getMetadata().getPath());
			if(th_registThing != null){
				for(String regist : th_registThing.split("[,]")){
					String[] rs = regist.split("[|]");
					if(rs.length == 2){
						Thing registThing = World.getInstance().getThing(rs[1]);
						if(registThing != null){
							String key = rs[0] + "_" + rs[1];
							List<Thing> things = cache.computeIfAbsent(key, k -> new ArrayList<>());

							things.add(thing);
						}
					}
				}
			}
		}//logger.info("not checked : " + thing.getMetadata().getPath());


		for(Thing child : thing.getChilds()){
			initRegistThingCache(child, cache);
		}
	}

	public static void initFromRegistCache(List<Thing> thingList, Map<String, Thing> thingContext, Thing registorThing, String registType){
		if(registThingCache == null){
			return;
		}

		Map<String, String > context = new HashMap<>();

		initFromRegistCache(thingList, thingContext, registorThing, registType, context);

		Thing thingIndex = World.getInstance().getThing("xworker.lang.util.ThingIndex");
		for(Thing desc : registorThing.getAllDescriptors()){
			if(thingIndex == desc) {
				continue;
			}

			initFromRegistCache(thingList, thingContext, desc, registType, context);
		}

		for(Thing ext : registorThing.getAllExtends()){
			initFromRegistCache(thingList, thingContext, ext, registType, context);
		}
	}

	public static void initFromRegistCache(List<Thing> thingList, Map<String, Thing> thingContext, Thing thing, String registType, Map<String, String> context){
		String path = thing.getMetadata().getPath();
		if(context.get(path) != null){
			return;
		}else{
			context.put(path, path);
		}

		if(registThingCache == null){
			return;
		}

		List<Thing> things = registThingCache.get(registType + "_" +  path);
		if(things != null){
			for(Thing th : things){
				String thpath = th.getMetadata().getPath();
				if(thingContext.get(thpath) != null){
					continue;
				}else{
					thingContext.put(thpath, th);
				}

				if(!isDisableRegistMyChilds() && th.getBoolean("th_registMyChilds")){
					addThingsToRegistList(th.getChilds(), thingContext, thingList);
				}else{
					thingList.add(th);
				}
			}
		}
	}

	public static void addThingsToRegistList(List<Thing> things, Map<String, Thing> context, List<Thing> thingList) {
		if(things == null) {
			return;
		}

		for(Thing child : things){
			if(child.getBoolean("th_registDisabled")){
				continue;
			}

			String childPath = child.getMetadata().getPath();
			if(context.get(childPath) != null){
			}else{
				context.put(childPath, child);
				thingList.add(child);
			}
		}
	}

	public static Map<String, List<Thing>> getRegistThingCache(){
		return registThingCache;
	}

    public static List<Thing> searchRegistThings(Thing registorThing, String registType, List<String> keywords, ActionContext actionContext){
    	return XWorkerUtils.searchRegistThings(registorThing, registType, keywords, actionContext);
    }
	
	public static List<Thing> searchRegistThings(Thing registorThing, String registType, List<String> keywords, boolean parent,  ActionContext actionContext){
		return XWorkerUtils.searchRegistThings(registorThing, registType, keywords, parent, actionContext);
	}
	
	public static List<Thing> searchRegistThings(Thing registorThing, String registType, List<String> keywords, boolean parent,  boolean noDescriptor, ActionContext actionContext){
		return XWorkerUtils.searchRegistThings(registorThing, registType, keywords, parent, noDescriptor, actionContext);
	}
}
