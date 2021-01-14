package xworker.lang.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.util.XWorkerUtils;

public class CommandDomain {
	/** 命令域模型 */
	Thing thing;
	
	/** 命令域所在的或所使用的变量上下文 */ 
	ActionContext actionContext;
	
	CommandExecutor executor;
	
	/** 参数 */
	Map<String, Object> params = new HashMap<String, Object>();
	
	/** 父域 */
	CommandDomain parentDomain = null;
	
	public CommandDomain(CommandExecutor executor, Thing thing, ActionContext actionContext) {
		this.executor = executor;
		this.thing = thing;
		this.actionContext = actionContext;
		params.put("commandDomain", this);
		
		thing.doAction("init", actionContext, "executor", executor, "commandDomain", this);
	}
		
	public List<Thing> getCommands(){
		try {
			List<Thing> commands = new ArrayList<Thing>();
			Bindings bindings = actionContext.push();
			bindings.putAll(params);
			bindings.put("executor", executor);
			bindings.put("commandExecutor", executor);
			bindings.put("commandDomain", this);
			List<Thing> list = thing.doAction("getCommands", actionContext);
			
			for(Thing thing : list) {
				if(thing.isThingByName("Command") || thing.isThingByName("ReferenceCommand")) {
					commands.add(thing);
				}else if(thing.isThingByName("CommandDomain")) {
					//是子域，包装成CallCommandDomain
					Thing callCommandDomain = new Thing("xworker.lang.command.CommandDomain/@CallCommand");
					callCommandDomain.set("name", thing.getMetadata().getName());
					callCommandDomain.set("label", thing.getMetadata().getLabel());
					callCommandDomain.set("commandDomain", thing.getMetadata().getPath());
					commands.add(callCommandDomain);
				}
			}			
			
			return commands;
		}finally {
			actionContext.pop();
		}
		
	}
	public String getLabel() {
		String label = thing.doAction("getLabel", actionContext, params);
		if(label == null || "".equals(label)) {
			return thing.getMetadata().getLabel();
		}else {
			return label;
		}
	}
	
	/**
	 * 设置新的域变量上下文。
	 * 
	 * @param actionContext
	 */
	public void setActionContext(ActionContext actionContext) {
		this.actionContext = actionContext;
	}
	
	protected void setParentDomain(CommandDomain parentDomain) {
		this.parentDomain = parentDomain;
	}
	
	/**
	 * 获取上级域，如果存在。
	 * 
	 * 上级域是在CommandExecutor.pushDomain()时设置的。
	 * 
	 * @return
	 */
	public CommandDomain getParentDomain() {
		return this.parentDomain;
	}
	
	public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public void setParams(Map<String, Object> params) {
		if(params != null) {
			this.params.putAll(params);
			this.params.put("commandDomain", this);
		}
	}
	
	/**
	 * 设置参数。
	 * 
	 * @param key
	 * @param value
	 */
	public void put(String key , Object value) {
		params.put(key, value);
	}
	
	/**
	 * 获取一个参数。
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		return params.get(key);
	}
	
	public void putAll(Map<String, Object> params) {
		if(params != null) {
			this.params.putAll(params);
		}
	}
	
	public Map<String, Object> getParams(){
		return this.params;
	}
	
	public static List<Thing> getCommands(ActionContext actionContext){
		Thing domain = actionContext.getObject("self");
		
		List<Thing> list = new ArrayList<Thing>();
		/*		
		for(Thing child : domain.getAllChilds("Command")){			
			list.add(child);
		}
		
		for(Thing child : domain.getAllChilds("ReferenceCommand")){			
			list.add(child);
		}*/
		
		//从注册中获取
		List<Thing> childs = XWorkerUtils.searchRegistThings(domain, "child", Collections.emptyList(), actionContext);
		for(Thing child : childs) {
			if(child.isThingByName("Command") || child.isThingByName("ReferenceCommand")) {
				list.add(child);
			}
			
			if(child.isThingByName("CommandDomain")) {
				//是子域，包装成CallCommandDomain
				Thing callCommandDomain = new Thing("xworker.lang.command.CommandDomain/@CallCommand");
				callCommandDomain.set("name", child.getMetadata().getName());
				callCommandDomain.set("label", child.getMetadata().getLabel());
				callCommandDomain.set("commandDomain", child.getMetadata().getPath());
				list.add(callCommandDomain);
			}
		}
		
		return list;
	}
}
