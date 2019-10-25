package xworker.system.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.util.ThingUtils;

public class ServiceManager {
	/** 按照事物的路径为key的服务器注册列表 */
	private static Map<String, ServiceGroup> thingServiceCache = new HashMap<String, ServiceGroup>();
	
	@SuppressWarnings("unchecked")
	public static void reloadService(){
		//服务的注册总位置
		Thing serviceRegistor = World.getInstance().getThing("xworker.system.service.ServiceGroups");
		
		//所有以注册的事物
		List<Thing> serviceThings = ThingUtils.searchRegistThings(serviceRegistor, "child", Collections.EMPTY_LIST, new ActionContext());
		
		//移除已经删除或停用的服务
		removeUnusedService(serviceThings);
		
		//更新或创建服务
		updateService(serviceThings);
	}
	
	/**
	 * 请求服务。
	 * 
	 * @param service
	 * @param actionContext
	 */
	public static void request(Thing service, ActionContext actionContext){
		ServiceGroup group = getServiceGroupByService(service);
		service.doAction("doService", actionContext, "sgroup", group);
	}
	
	public static ServiceGroup getServiceGroupByService(Thing service){
		Thing serviceDesc = service.getDescriptor();
		Thing serviceGroup = serviceDesc.getParent();
		return getServiceGroupByGroup(serviceGroup);
	}
	
	public static ServiceGroup getServiceGroupByGroup(Thing serviceGroup){
		String path = serviceGroup.getMetadata().getPath();
		
		ServiceGroup group = thingServiceCache.get(path);
		if(group == null){
			group = new ServiceGroup(serviceGroup);
			thingServiceCache.put(path, group);
		}
		
		return group;
	}
	
	private static void updateService(List<Thing> serviceThings){
		for(Thing thing : serviceThings){
			updateService(thing);
		}
	}
	
	private static void updateService(Thing thing){
		String path = thing.getMetadata().getPath();
		
		ServiceGroup service = thingServiceCache.get(path);
		if(service == null){
			createService(thing);
		}else{
			updateService(thing, service);
		}
	}
	
	private static void updateService(Thing thing, ServiceGroup service){
		//更新
		service.update(thing);
	}
	
	private  static void createService(Thing thing){
		ServiceGroup service = new ServiceGroup(thing);
		
		thingServiceCache.put(thing.getMetadata().getPath(), service);

	}
	
	private static void removeUnusedService(List<Thing> serviceThings){
		//移除已经不存在的服务
		List<String> removedService = new ArrayList<String>();
		for(String key : thingServiceCache.keySet()){
			boolean have = false;
			for(Thing thing : serviceThings){
				if(key.equals(thing.getMetadata().getPath())){
					have = true;
					break;
				}
			}
			
			if(!have){
				removedService.add(key);
			}
		}
		
		//停用的服务
		for(Thing thing : serviceThings){
			if(thing.getBoolean("enabled") == false){
				removedService.add(thing.getMetadata().getPath());
			}
		}
		
		for(String key : removedService){
			thingServiceCache.remove(key);
		}
	}
}
