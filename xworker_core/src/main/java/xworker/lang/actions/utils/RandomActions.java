package xworker.lang.actions.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.util.UtilAction;

public class RandomActions {
	private static Random random = new Random();
	
	public static Object randomActions(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		int count = self.doAction("getCount", actionContext);
		if(count < 0) {
			count = 1;
		}
		
		List<Thing> actions = null;
		List<Thing> childs = self.getChilds();
		if(count >= childs.size()) {
			//使用全部子节点
			actions = childs;
		}else {
			//使用部分子节点
			actions = new ArrayList<Thing>();
			
			//拷贝到新的列表中，不能直接修改childs，否则会改变事物
			List<Thing> list = new ArrayList<Thing>();
			list.addAll(childs);
			
			while(count > 0) {
				int n = 0;
				synchronized(random) {
					n = random.nextInt(list.size());
				}
				
				actions.add(list.remove(n));
				count--;
			}
		}
		
		return UtilAction.runChildActions(actions, actionContext);
	}
	
	public static Object randomWeightActions(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		List<Thing> childs = self.getChilds("Weight");
		int totalWeight = 0;
		for(Thing child : childs) {
			int weight = child.doAction("getWeight", actionContext);
			totalWeight += weight;
		}
		
		int r = 0;
		synchronized(random) {
			r = random.nextInt(totalWeight);
		}
		
		totalWeight = 0;
		for(Thing child : childs) {
			int weight = child.doAction("getWeight", actionContext);
			totalWeight += weight;
			
			if(r < totalWeight) {
				return UtilAction.runChildActions(child.getChilds(), actionContext);
			}
		}
		
		return null;
	}

	public static Object randomList(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		int count = self.doAction("getCount", actionContext);
		if(count < 0) {
			count = 1;
		}
		
		List<Thing> childs = self.getChilds();
		List<Object> list = new ArrayList<Object>();
		if(count == 1) {
			int n = 0;
			synchronized(random) {
				n = random.nextInt(childs.size());
			}
			list.add(childs.get(n).getAction().run(actionContext));
		}else {
			List<Thing> listTemp = new ArrayList<Thing>();
			listTemp.addAll(childs);
			
			while(count > 0) {
				int n = 0;
				synchronized(random) {
					n = random.nextInt(listTemp.size());
				}
				
				list.add(listTemp.remove(n).getAction().run(actionContext));
				count--;
			}
		}
		
		return list;
	}
	
	public static Object randomWeightList(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		int count = self.doAction("getCount", actionContext);
		if(count < 0) {
			count = 1;
		}
		
		List<Thing> childs = self.getChilds("Weight");
		List<Object> list = new ArrayList<Object>();
		while(count > 0) {
			int totalWeight = 0;
			for(Thing child : childs) {
				int weight = child.doAction("getWeight", actionContext);
				totalWeight += weight;
			}
			
			int r = 0;
			synchronized(random) {
				r = random.nextInt(totalWeight);
			}
			
			totalWeight = 0;
			for(int i=0; i<childs.size(); i++) {
				int weight = childs.get(i).doAction("getWeight", actionContext);
				totalWeight += weight;
				
				if(r < totalWeight) {
					Thing child = childs.remove(i);
					list.add(UtilAction.runChildActions(child.getChilds(), actionContext, false));
					break;
				}
			}
			
			count--;
		}
		
		return list;
	}
	
	public static Object random(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String type = self.doAction("getType", actionContext);
		if("nextBoolean".equals(type)) {
			synchronized(random) {
				return random.nextBoolean();
			}
		}else if("nextBytes".equals(type)) {
			byte[] bytes = self.doAction("getBytes", actionContext);
			if(bytes == null) {
				throw new ActionException("Param bytes is null, action=" + self.getMetadata().getPath());
			}else {
				synchronized(random) {
					random.nextBytes(bytes);
				}
				
				return bytes;
			}			
		}else if("nextDouble".equals(type)) {
			synchronized(random) {
				return random.nextDouble();
			}
		}else if("nextFloat".equals(type)) {
			synchronized(random) {
				return random.nextFloat();
			}
		}else if("nextGaussian".equals(type)) {
			synchronized(random) {
				return random.nextGaussian();
			}
		}else if("nextLong".equals(type)) {
			synchronized(random) {
				return random.nextLong();
			}
		}else {
			Integer n = self.doAction("getIntn", actionContext);
			if(n == null || n < 0) {
				synchronized(random) {
					return random.nextInt();
				}
			}else {
				synchronized(random) {
					return random.nextInt(n);
				}
			}
		}
	}
}
