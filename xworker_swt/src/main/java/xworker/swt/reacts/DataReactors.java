package xworker.swt.reacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.VariableDesc;
import xworker.lang.executor.Executor;

public class DataReactors {
	private static final String TAG = DataReactors.class.getName();
	
	private static void createRules_(String rules, ActionContext actionContext) {
		//解析规则
		if(rules != null) {
			for(String line : rules.split("[\n]")) {
				line = line.trim();
				if("".equals(line) || line.startsWith("#")) {
					//过滤空行和注解
					continue;
				}
				
				//目前只接收a,b,c,d或a>b,c,d这样的两种格式
				int index = line.indexOf(">");
				if(index == -1) {
					//只是响应器的定义
					getOrCreateDataReactors(line, actionContext);
				} else {
					String[] reactors = line.split("[>]");
					
					List<DataReactor> lastReactors = null;
					for(int i=0; i<reactors.length; i++) {
						if(i == 0) {
							lastReactors = getOrCreateDataReactors(reactors[i], actionContext);
						} else {
							List<DataReactor> nextReactors = getOrCreateDataReactors(reactors[i], actionContext);
							for(DataReactor f : lastReactors) {
								for(DataReactor s : nextReactors) {
									f.addNextReator(s);
								}
							}
							
							lastReactors = nextReactors;
						}
					}
				}				
			}
		}
	}
	
	public static void createRules(String rules, ActionContext actionContext) {
		List<DataReactor> loadList = new ArrayList<DataReactor>();
		DataReactorFactory.setAutoLoadLocal(loadList);
		try {
			createRules_(rules, actionContext);
		}finally {
			DataReactorFactory.setAutoLoadLocal(null);
		}
	}
	
	public static void fireLoadReactors(List<String> fireLoadReactors, ActionContext actionContext) {
		List<DataReactor> loadList = new ArrayList<DataReactor>();
		DataReactorFactory.setAutoLoadLocal(loadList);
		try {
			fireLoadReactors_(fireLoadReactors, loadList, actionContext);
		}finally {
			DataReactorFactory.setAutoLoadLocal(null);
		}
	}
	
	private static void fireLoadReactors_(List<String> fireLoadReactors, List<DataReactor> loadList, ActionContext actionContext) {
		if(fireLoadReactors != null) {
			for(String name : fireLoadReactors) {
				if(name == null) {
					continue;
				}
				name = name.trim();
				
				Object reactor = actionContext.get(name);
				if(reactor instanceof DataReactor) {
					((DataReactor) reactor).fireLoaded(null);
				}else {
					reactor = actionContext.get(name + "DataReactor");
					if(reactor instanceof DataReactor) {
						((DataReactor) reactor).fireLoaded(null);
					}else {
						Executor.info(TAG, "Can not load data reactor, data reactor '" 
									+ name + "' or '" + name + "DataReactor' not found.");
					}
				}
			}
		}
		
		for(DataReactor dataReactor : loadList) {
			dataReactor.fireLoaded(null);
		}
	}
	
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		List<DataReactor> loadList = new ArrayList<DataReactor>();
		DataReactorFactory.setAutoLoadLocal(loadList);
		try {
			//先创建子节点
			actionContext.peek().put("parent", null);
			for(Thing child : self.getChilds()) {
				child.doAction("create", actionContext);
			}
			
			//解析规则
			String rules = self.doAction("getRules", actionContext);
			createRules_(rules, actionContext);
			
			//加载指定的
			List<String> fireLoadReactors = self.doAction("getFireLoadReactors", actionContext);
			fireLoadReactors_(fireLoadReactors, loadList, actionContext);
		}finally {
			DataReactorFactory.setAutoLoadLocal(null);
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
					DataReactorFactory.setDataReactorName(name);
					DataReactor reactor = DataReactorFactory.create(action, ss, control, actionContext);
					if(reactor != null) {
						actionContext.g().put(name + "DataReactor", reactor);
						reactors.add(reactor);
						continue;
					} else {
						Executor.info(TAG,  "Cant not create DataReactor, name=" + name + ", contorl=" + control);
					}
				}
			}
		}
		
		return reactors;
	}
	
	
	public static List<VariableDesc> createVariablesDescs(ActionContext actionContext){
		List<VariableDesc> vars = new ArrayList<VariableDesc>();
		Thing thing = actionContext.getObject("thing");
		//过滤自己的子节点创建的DataReactor
		Map<String, String> context = new HashMap<String, String>();
		initDataReactorDescs(thing, context);
		
		String rules = thing.doAction("getRules", actionContext);
		if(rules != null) {
			for(String line : rules.split("[\n]")) {
				line = line.trim();
				if("".equals(line) || line.startsWith("#")) {
					//过滤空行和注解
					continue;
				}
				
				for(String str : line.split("[>]")) {
					initDataReactorDescs(str, vars, context);
				}
				
				/*
				//目前只接收a,b,c,d或a>b,c,d这样的两种格式
				int index = line.indexOf(">");
				String first, second = null;
				if(index != -1) {
					first = line.substring(0, index);
					second = line.substring(index + 1, line.length());
				}else {
					first = line;
				}
				
				initDataReactorDescs(first, vars, context);
				initDataReactorDescs(second, vars, context);
				*/
			}
		}
		
		return vars;
	}
	
	private static void initDataReactorDescs(Thing thing, Map<String, String> context) {
		for(Thing child : thing.getChilds()) {
			String name = child.getMetadata().getName();
			context.put(name, name);
			
			initDataReactorDescs(child, context);
		}
	}
	
	private static void initDataReactorDescs(String str, List<VariableDesc> vars, Map<String, String> context){
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
				
				if("".equals(name)) {
					continue;
				}
				
				if(context.get(name) == null) {
					name = name + "DataReactor";
					vars.add(VariableDesc.create(name, DataReactor.class));
					context.put(name, name);					
				}
			}
		}
		
	}
}
