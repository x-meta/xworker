package xworker.lang.actions.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.MultiKeyMap;

public class TypesActions {
	public static List<Object> createList(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String type = self.doAction("getType", actionContext);
		List<Object> list = null;
		if("CopyOnWriteArrayList".equals(type)) {
			list = new CopyOnWriteArrayList<Object>();
		}else if("LinkedList".equals(type)) {
			list = new LinkedList<Object>();
		}else {
			list = new ArrayList<Object>();
		}
		
		for(Thing child : self.getChilds()) {
			list.add(child.getAction().run(actionContext));
		}
		
		return list;
	}
	
	public static Map<String, Object> createMap(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String type = self.doAction("getType", actionContext);
		Map<String, Object> map = null;
		if("ConcurrentHashMap".equals(type)) {
			map = new ConcurrentHashMap<String, Object>();
		}else if("ConcurrentSkipListMap".equals(type)) {
			map = new ConcurrentSkipListMap<String, Object>();
		}else if("LinkedHashMap".equals(type)) {
			map = new LinkedHashMap<String, Object>();
		}else if("TreeMap".equals(type)) {
			map = new TreeMap<String, Object>();
		}else {
			map = new HashMap<String, Object>();
		}
		
		for(Thing child : self.getChilds()) {
			String key = child.getMetadata().getName();
			Object value = child.getAction().run(actionContext);
			map.put(key, value);
		}
		
		return map;
	}
	
	public static MultiKeyMap createMultiKeyMap(ActionContext actionContext) {
		return new MultiKeyMap();
	}
}
