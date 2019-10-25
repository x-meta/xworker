package xworker.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用Map存放多级数据。
 * 
 * @author zyx
 *
 */
public class MultiKeyMap {
	Map<Object, Object> data = new HashMap<Object, Object>();
	
	@SuppressWarnings("unchecked")
	public void put(Object value, Object ... keys) {
		Map<Object, Object> m = data;
		for(int i=0; i<keys.length - 1; i++) {
			Map<Object, Object> m1 = (Map<Object, Object>) m.get(keys[i]);
			if(m1 == null) {
				m1 = new HashMap<Object, Object>();
				m.put(keys[i], m1);
			}
			
			m = m1;
		}
		
		m.put(keys[keys.length - 1], value);
	}
	
	@SuppressWarnings("unchecked")
	public Object get(Object ... keys) {
		Map<Object, Object> m = data;
		for(int i=0; i<keys.length - 1; i++) {
			Map<Object, Object> m1 = (Map<Object, Object>) m.get(keys[i]);
			if(m1 == null) {
				return null;
			}else {			
				m = m1;
			}
		}
		
		return m.get(keys[keys.length -1]);
	}
	
	@SuppressWarnings("unchecked")
	public Object remove(Object ... keys) {
		Map<Object, Object> m = data;
		for(int i=0; i<keys.length - 1; i++) {
			Map<Object, Object> m1 = (Map<Object, Object>) m.get(keys[i]);
			if(m1 == null) {
				return null;
			}else {			
				m = m1;
			}
		}
		
		return m.remove(keys[keys.length -1]);
	}
	
	public Map<Object, Object> getMap(){
		return data;
	}
}
