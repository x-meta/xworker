package xworker.swt.reacts;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class DataReactors {
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		//先创建子节点
		actionContext.peek().put("parent", null);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		//解析规则
		String rules = self.doAction("getRules", actionContext);
		if(rules != null) {
			for(String line : rules.split("[\n]")) {
				line = line.trim();
				if("".equals(line) || line.startsWith("#")) {
					//过滤空行和注解
					continue;
				}
				
				//目前只接收a,b,c,d或a>b,c,d这样的两种格式
				int index = line.indexOf(">");
				String first, second = null;
				if(index != -1) {
					first = line.substring(0, index);
					second = line.substring(index + 1, line.length());
				}else {
					first = line;
				}
				
				List<DataReactor> firstReactors = getOrCreateDataReactors(first, actionContext);
				List<DataReactor> secondReactors = getOrCreateDataReactors(second, actionContext);
				for(DataReactor f : firstReactors) {
					for(DataReactor s : secondReactors) {
						f.addNextReator(s);
					}
				}
			}
		}
		
		//加载指定的
		List<String> fireLoadReactors = self.doAction("getFireLoadReactors", actionContext);
		if(fireLoadReactors != null) {
			for(String name : fireLoadReactors) {
				if(name == null) {
					continue;
				}
				name = name.trim();
				
				Object reactor = actionContext.get(name);
				if(reactor instanceof DataReactor) {
					((DataReactor) reactor).fireLoaded();
				}else if(reactor == null) {
					reactor = actionContext.get(name + "DataReactor");
					if(reactor instanceof DataReactor) {
						((DataReactor) reactor).fireLoaded();
					}
				}
			}
		}
	}
	
	public static List<DataReactor> getOrCreateDataReactors(String str, ActionContext actionContext){
		List<DataReactor> reactors = new ArrayList<DataReactor>();
		if(str != null) {
			String strs[] = str.split("[,]");
			for(String s : strs) {
				s = s.trim();
				if("".equals(s)) {
					continue;
				}
				
				String[] ss = s.split("[|]"); 				
				String[] ns = ss[0].split("[?]");
				String name = ns[0].trim();
				String action = null;
				if(ns.length > 1) {
					action = ns[1].trim();
				}
				
				if("".equals(name)) {
					continue;
				}
				
				Object control = actionContext.get(name);
				if(control instanceof DataReactor) {
					reactors.add((DataReactor) control);
					continue;
				}
				
				Object reac = actionContext.get(name + "DataReactor");
				if(reac instanceof DataReactor) {
					reactors.add((DataReactor) reac);
					continue;
				}
				
				if(reac == null) {
					DataReactor reactor = DataReactorFactory.create(action, ss, control, actionContext);
					if(reactor != null) {
						actionContext.g().put(name + "DataReactor", reactor);
						reactors.add(reactor);
						continue;
					}
				}
			}
		}
		
		return reactors;
	}
}
