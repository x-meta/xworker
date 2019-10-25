package xworker.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceManager {
	static Map<String, ServiceList> serviceLists = new HashMap<String, ServiceList>();
	/** 通过类名注册的服务 */
	private static Map<String, Object> services = new HashMap<String, Object>();
	
	/**
	 * 注册一个服务，KEY是serviceClass的类名。
	 * 
	 * @param service
	 * @param serviceClass
	 */
	public static void regist(Object service, Class<?> serviceClass) {
		services.put(serviceClass.getName(), service);
	}
	
	/**
	 * 获取一个已注册的服务，KEY是serviceClass的类名。
	 * 
	 * @param serviceClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getService(Class<T> serviceClass){
		return (T) services.get(serviceClass.getName());
	}
	
	public static <T> T getJavaService(Class<T> serviceClass) {
		return getService(serviceClass);
	}
	
	public static Service getThingService(String name) {
		return getActiveService(name);
	}
	
	public static synchronized void regist(String name, Service service) {
		ServiceList serviceList = serviceLists.get(name);
		if(serviceList == null) {
			serviceList = new ServiceList();
			serviceLists.put(name, serviceList);			
		}
		
		serviceList.addService(service);
	}
	
	public static Service getActiveService(String name) {
		ServiceList serviceList = serviceLists.get(name);
		if(serviceList != null) {
			return serviceList.getActiveService();
		}else {
			return null;
		}
	}
	
	public static List<Service> getServices(String name){
		ServiceList serviceList = serviceLists.get(name);
		if(serviceList != null) {
			return serviceList.getServices();
		}else {
			return Collections.emptyList();
		}
	}
}
