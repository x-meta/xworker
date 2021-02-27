package xworker.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 丢弃超时的数据的Map。
 * 
 * 有两个Map组成，到一定时间后切换两个Map，并把末尾的Map清空。
 * 
 * @author zyx
 *
 */
public class TimeoutMap<K, V> {
	long timeout;
	Object locker = new Object();
	
	long lastTime = System.currentTimeMillis();
	Map<K, V> map1 = new HashMap<K, V>();
	Map<K, V> map2 = new HashMap<K, V>();
	
	/**
	 * 构造函数。
	 * 
	 * @param timeout 超时时间（单位毫秒）
	 */
	public TimeoutMap(long timeout) {
		this.timeout = timeout;
		
		if(timeout <= 0) {
			timeout = 2000;
		}
	}
	
	public V get(K key) {
		checkMap();
		
		V value = map1.get(key);
		if(value == null) {
			value = map2.get(key);
			if(value != null) {
				map1.put(key, value);
			}
		}
		
		return value;
	}
	
	public void remove(K key) {
		map1.remove(key);
		map2.remove(key);
	}
	
	public void put(K key, V value) {
		checkMap();
		
		map1.put(key, value);
	}
	
	private void checkMap(){
		synchronized(locker) {
			if(System.currentTimeMillis() - lastTime > timeout) {
				map2.clear();
				Map<K, V> temp = map1;
				map1 = map2;
				map2 = temp;
				
				lastTime = System.currentTimeMillis();
			}			
		}
	}
}
