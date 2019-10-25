package xworker.system.service;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.Thing;

public class ServiceGroup {
	/** 服务事物 */
	private Thing thing;
	
	/** 用于保存服务数据的缓存 */
	private Map<String, Object> datas = new HashMap<String, Object>();
	
	public ServiceGroup(Thing thing){
		update(thing);
	}
			
	public void setData(String key, Object value){
		datas.put(key, value);
	}
	
	public Object getData(String key){
		return datas.get(key);
	}
	
	public Object removeData(String key){
		return datas.remove(key);
	}
	
	public void update(Thing thing){
		this.thing = thing;
	}
		

	public Thing getThing(){
		return thing;
	}
		
}
