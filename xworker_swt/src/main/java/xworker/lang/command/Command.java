package xworker.lang.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ExceptionUtil;
import org.xmeta.util.UtilMap;

public class Command {
	/** 命令事物 */
	private Thing commandThing;
	
	/** 参数事物，当命令作为参数时，通常有参数事物。*/
	private Thing paramThing;
	
	/** 参数的值 */
	private Object result;
	
	/** 当命令是选择器器，选中的内容。 */
	private Object selectedContnet;
	
	/** 当前命令的参数列表 */
	private List<Command> params = new ArrayList<Command>();
	
	/** 是否已经执行 */
	private boolean executed = false;
	
	/** 命令执行器 */
	private CommandExecutor executor;
	
	/** 父命令 */
	private Command parent;
	
	public Command(CommandExecutor executor, Thing commandThing, Thing paramThing){
		this.paramThing = paramThing;
		this.executor = executor; 
		
		setCommandThing(commandThing);
	}
	
	public Command(CommandExecutor executor, Thing commandThing, Thing paramThing, Command parent){
		this.paramThing = paramThing;
		this.executor = executor; 
		this.parent = parent;
		
		setCommandThing(commandThing);
	}
	
	public boolean isReady(){
		if(executed) {
			return true;
		}
		
		if(commandThing == null){
			return false;
		}else{
			if(result == null && commandThing.getBoolean("hasContents")){				
				return false;
			}else{
				return true;
			}
		}
	}
	
	/**
	 * 获取参数的结果。
	 * 
	 * @param actionContext
	 * @return
	 */
	public Object getResult(ActionContext actionContext){
		if(executed){
			return result;
		}else{
			result = run(actionContext);
			return result;
		}
	}
	
	/**
	 * 执行命令获取值。
	 * 
	 * @param actionContext
	 * @return
	 */
	public Object run(ActionContext actionContext){
		//参数
		for(Command param : params) {
			if(param.isExecuted() == false && param.isReady()) {
				param.run(actionContext);
			}
			
			if(param.isExecuted() == false || param.isReady() == false) {
				return null;
			}
		}
		
		Bindings bindings = actionContext.push();
		bindings.putAll(executor.getDomain().getParams());
		bindings.put("command", this);
		bindings.put("commandExecutor", executor);
		bindings.put("executor", executor);
		bindings.put("executorContext", executor.getActionContext());
		try{
			for(int i=0; i<params.size(); i++){
				Command param = params.get(i);
				String name = param.paramThing.getMetadata().getName();
				bindings.put(name, param.getResult(actionContext));
			}
			
			//常量
			for(Thing cons : commandThing.getAllChilds("Constant")){
				bindings.put(cons.getMetadata().getName(), cons.doAction("getValue", actionContext));
			}
			
			executed = true; //不能放到后面
			try {
				result = commandThing.doAction("run", actionContext);
			}catch(Exception e) {
				executed = false;
				String html = "<pre>" + ExceptionUtil.toString(e) + "</pre>";
				executor.setHtml(html);
			}
		}finally{
			actionContext.pop();
		}
		return result;
	}
	
	public CommandDomain getDomain() {
		return executor.getDomain();
	}

	public Object doAction(String actionName, Map<String, Object> params1) {
		CommandDomain domain = executor.getDomain();
		ActionContext actionContext = domain.getActionContext();
		Bindings bindings = actionContext.push();
		try {
			bindings.putAll(domain.getParams());
			bindings.put("command", this);
			bindings.put("commandExecutor", executor);
			bindings.put("executor", executor);
			if(params != null) {
				bindings.putAll(params1);
			}
			for(int i=0; i<params.size(); i++){
				Command param = params.get(i);				
				String name = param.paramThing.getMetadata().getName();
				bindings.put(name, param.getResult(actionContext));
			}
			
			return commandThing.doAction(actionName, actionContext);
		}finally {
			actionContext.pop();
		}
	}
	
	public Object run(String actionName, ActionContext actionContext, Map<String, Object> params1){
		Bindings bindings = actionContext.push();
		bindings.putAll(executor.getDomain().getParams());
		bindings.put("command", this);
		bindings.put("commandExecutor", executor);
		try{
			for(int i=0; i<params.size(); i++){
				Command param = params.get(i);
				String name = param.paramThing.getMetadata().getName();
				bindings.put(name, param.getResult(actionContext));
			}
			
			return commandThing.doAction(actionName, actionContext, params1);
		}finally{
			actionContext.pop();
		}		
	}
	
	public Thing getCommandThing() {
		return commandThing;
	}

	public void setCommandThing(Thing commandThing) {
		this.commandThing = commandThing;
		
		params.clear();
		if(commandThing == null){
			return;
		}
		
		//由于获取被继承事物的参数，而本事物可以重新定义参数，所以过滤被继承的同名参数
		Map<String, Thing> context = new HashMap<String, Thing>();
		for(Thing cons : commandThing.getChilds("Constant")){
			context.put(cons.getMetadata().getName(), cons);
		}		
		
		//使用getAllChilds，这样命令可以继承命令
		for(Thing param : commandThing.getAllChilds("Parameter")){
			String name = param.getMetadata().getName();
			if(context.get(name) != null) {
				continue;
			}else {
				context.put(name, param);
			}
			
			Thing childCommandThing = World.getInstance().getThing(param.getStringBlankAsNull("command"));
			params.add(new Command(executor, childCommandThing, param, this));
		}
		
		this.result = null;
		this.executed = false;
		
		//执行初始化，使用domainContext
		commandThing.doAction("init", executor.getDomainContext(), "command", this, "commandExecutor", executor, 
				"executor", executor, 
				"executorContext", executor.getActionContext());
	}

	public Object getResult() {
		return result;
	}

	public CommandExecutor getExecutor() {
		return executor;
	}
	
	/**
	 * 如果命令没有子参数，那么选择的结果会被当作该命令的结果。
	 * 
	 * @param result
	 */
	public void setResult(Object result) {
		this.result = result;
		
		executed = true;
		
		if(commandThing != null) {
			doAction("onResult", UtilMap.toMap("result", result));
		}
	}
	
	public void setContent(Object content) {
		this.selectedContnet = content;
		
		if(commandThing != null) {
			doAction("onContent", UtilMap.toMap("content", content));
		}
	}

	public Thing getParamThing() {
		return paramThing;
	}

	public List<Command> getParams() {
		return params;
	}
	
	public List<Command> getParameters(){
		return params;
	}
	
	/**
	 * 返回参数。
	 * 
	 * @param name
	 * @return
	 */
	public Command getParameter(String name) {
		for(Command param :params) {
			if(param.getName().equals(name)) {
				return param;
			}
		}
		
		return null;
	}

	public boolean isExecuted() {
		for(Command command : params) {
			if(!command.isExecuted()) {
				return false;
			}
		}
		
		return executed;
	}
	
	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

	public Command getParent() {
		return parent;
	}

	public void setParent(Command parent) {
		this.parent = parent;
	}
	
	public String getName(){
		if(this.paramThing != null){
			return this.paramThing.getMetadata().getName();					
		}else{
			return null;
		}
	}
		
	public Object getSelectedContnet() {
		return selectedContnet;
	}

	public Shell getShell(){
		return executor.getShell();
	}
	
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		if(commandThing != null) {
			sb.append(commandThing.getMetadata().getName());
		}
		sb.append("(");
		boolean first = true;
		for(Command param : params) {
			if(!first) {
				sb.append(", ");				
			}else {
				first = false;
			}
			
			sb.append(param.paramThing.getMetadata().getName());
			sb.append(": ");
			if(param.executed) {
				sb.append(String.valueOf(param.result));
			}else {
				sb.append(param.toString());
			}
		}
		sb.append(")");
		return sb.toString();
	}
}
