/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.ui.function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.cache.ThingEntry;

import xworker.ui.UIRequest;

/**
 * 函数请求。
 * 
 * @author Administrator
 *
 */
public class FunctionRequest {
	private static Logger logger = LoggerFactory.getLogger(FunctionRequest.class);
	
	//参数的自动执行是异步的
	//private static  ExecutorService executor = Executors.newCachedThreadPool();
	public static FunctionMonitor functionMonitor;
	
	/** 函数事物 */
	Thing thing;
	
	/** 函数原型 */
	Thing descriptor;
	
	/** 如果不为null，那么该函数是由其他方法调用的 */
	FunctionCallback callback;
	
	/** 焦点参数，打开函数编辑界面时默认选中的参数 */
	FunctionParameter focusedParam;
	
	/** 函数的参数值 */
	List<FunctionParameter> params = new ArrayList<FunctionParameter>();
	
	/** 创建函数时的变量上下文 */
	ActionContext actionContext;
	
	/** 事物是否是函数描述 */
	boolean isDescriptor;
	
	/** 是否已经执行过了 */
	boolean executed = false;
	
	/** 函数执行的结果值 */
	Object value;
	
	/** 函数运行的状态，一般控制函数会使用这个状态，普通函数不会使用 */
	FunctionStatus status;
	
	/** 是否是动态参数 */
	boolean executeParameterMySelf = false;
	
	/** 是否是局部变量的容器 */
	boolean isLocalVariableContainer = false;
	
	/** 是否是不信任的值函数 */
	boolean unTrustValue = false;
	
	long loastModified = 0;
	
	/** 父函数 */
	FunctionRequest parent;
	
	/** 临时的函数请求描述者 */
	String description;
	
	Thing descriptionThing;
	
	/** 函数请求辅助，如debug的参数等，请通过getFunctionAssist获取具体的实例 */
	private FunctionAssist functionAssist;
	
	/** 子函数 */
	FunctionRequest childFunctionRequest;
	
	/** 函数的UI的唯一key */
	FunctionRequest uiKey;
	
	/** 缓存的数据 */
	Object cachedData;
	
	/** 做缓存时的值事物 */
	ThingEntry catchedThingEntry;
	
	/** 缓存事物的最后修改日期 */
	long cachedThingLastModified;
	
	Map<String, Object> localVariables = null;
	
	/** 结束时是否关闭UI */
	boolean closeUIOnFinished = false;
	
	/**
	 * 函数请求的构造器，默认传入的事物不是描述者，而是函数实例。
	 * 
	 * @param thing
	 * @param actionContext
	 */
	public FunctionRequest(Thing thing, ActionContext actionContext){
		this(thing, null, actionContext);
	}
	
	public FunctionRequest(Thing thing, boolean isDescriptor, ActionContext actionContext){
		this(thing, null, isDescriptor, actionContext);
	}
	
	/**
	 * 函数请求的构造器，默认传入的事物不是描述者，而是函数实例。
	 * 
	 * @param thing
	 * @param callback
	 * @param actionContext
	 */
	public FunctionRequest(Thing thing, FunctionCallback callback, ActionContext actionContext){
		this(thing,  callback, false, actionContext);
	}
	
	/**
	 * 函数请求的构造器。
	 * 
	 * @param thing
	 * @param callback
	 * @param isDescriptor
	 * @param actionContext
	 */
	public FunctionRequest(Thing thing, FunctionCallback callback, boolean isDescriptor, ActionContext actionContext){
		this.callback = callback;
		this.actionContext = actionContext;
		
		init(thing, isDescriptor);
	}
	
	/**
	 * 获取缓存的
	 * 
	 * @return
	 */
	public Object getCachedData(){
		Thing cacheThing = null;
		if(catchedThingEntry != null){
			cacheThing = catchedThingEntry.getThing();
		}
		if(cacheThing == null || cacheThing != this.getThing() || cachedThingLastModified != this.getThing().getMetadata().getLastModified()){
			return null;
		}else{
			return cachedData;
		}		
	}
	
	/**
	 * 设置缓存数据。
	 * 
	 * @param data
	 */
	public void setCachedData(Object data){
		this.cachedData = data;
		this.cachedThingLastModified = getThing().getMetadata().getLastModified();
		this.catchedThingEntry = new ThingEntry(this.getThing());
	}
	
	/**
	 * 重新设置函数事物。
	 * 
	 * @param thing
	 */
	public void resetFunctionThing(Thing thing, boolean isDescriptor){		
		init(thing, isDescriptor);
		if(callback != null){
			FunctionParameter param = callback.getParam();
			if(param != null){
				param.valueThing = this.getThing();
				this.getThing().put("name", param.getName());
			}
		}
	}
	
	public void init(Thing thing, boolean isDescriptor){
		this.isDescriptor = isDescriptor;
		this.params.clear();
		this.value = null;
		
		this.descriptor = isDescriptor ? thing : thing.getDescriptor();
		this.descriptor = getRealDescriptor(descriptor);
		this.executeParameterMySelf = this.descriptor.getBoolean("executeParameterMySelf");
		this.thing = isDescriptor ? new Thing(descriptor.getMetadata().getPath()) : thing;
		if(isDescriptor){
			this.thing.set("description", this.getDescription());
		}
		this.loastModified = thing.getMetadata().getLastModified();
		Thing functionThing = this.thing;
		if(this.isProxy()){
			functionThing = this.getProxyedThing();
		}
		
		isLocalVariableContainer = this.descriptor.getBoolean("isLocalVariableContainer");
		unTrustValue = this.descriptor.getBoolean("unTrustValue");
		List<Thing> addedParamThing = new ArrayList<Thing>();
		for(Thing child : descriptor.getChilds("Parameter")){
			boolean have = false;
			for(Thing pchild : functionThing.getChilds()){					
				if(pchild.getMetadata().getName().equals(child.getMetadata().getName())){
					String name = child.getMetadata().getName();
					boolean optional = isOptionalParameter(child);
					
					params.add(new FunctionParameter(this, child, pchild, name, optional, null, true));
					
					addedParamThing.add(pchild);
					have = true;
					break;
				}
			}
			
			if(!have){
				String name = child.getMetadata().getName();
				boolean optional = isOptionalParameter(child);
				
				//最佳函数
				Thing bestFunction = child.getThing("BestFunction@0");

				if(bestFunction != null){
					boolean isFunction = false;
					String functionPath = bestFunction.getStringBlankAsNull("functionPath");
					if(functionPath == null){
						Thing bestFn = bestFunction.getThing("Function@0");
						if(bestFn != null){
							functionPath = bestFn.getMetadata().getPath();
							isFunction = true;
						}
					}
					if(functionPath != null){
						if(bestFunction.getBoolean("isDescriptor") && !isFunction){
							bestFunction = new Thing(functionPath);
						}else{
							if(bestFunction.getBoolean("asParameterFunction")){
								bestFunction = World.getInstance().getThing(functionPath);
								if(bestFunction != null){
									bestFunction = bestFunction.detach();
								}
							}else{
								bestFunction = new Thing("xworker.ui.function.common.CallFunction");
								Thing bestValueThing = new Thing("xworker.lang.function.values.StringValue");
								bestFunction.addChild(bestValueThing);
								bestValueThing.put("name", "functionPath");
								bestValueThing.put("value", functionPath);
							}
							//bestFunction = new Thing("xworker.lang.function.FunctionProxy");
							//bestFunction.put("extends", functionPath);
						}
					}
					
					bestFunction.put("name", name);
				}
				
				FunctionParameter param = new FunctionParameter(this, child, bestFunction, name, optional, null,  bestFunction != null);
				Object defautValue = child.get("defaultValue");
				if(defautValue != null && !"".equals(defautValue)){
					param.value = defautValue;
					//param.setValue(defautValue, bestFunction);
				}
				
				params.add(param);
			}
		}
		
		//函数自己的子节点没有加入的要加入
		for(Thing pchild : functionThing.getChilds()){			
			boolean have = false;
			for(Thing added : addedParamThing){
				if(pchild == added){
					have = true;
					break;
				}
			}
			
			if(!have){
				String name = pchild.getMetadata().getName();
				boolean optional = true;
				
				if(pchild != null && pchild.getMetadata().getPath().equals("xworker.lang.function.FunctionParameterNone")){
					this.addParameter(pchild.getMetadata().getName());
				}else{
					params.add(new FunctionParameter(this, pchild.getDescriptor(), pchild, name, optional, null, true));
				}
			}
		}
	}
	
	/**
	 * 设置一个本地变量。
	 * 
	 * @param name
	 * @param object
	 */
	public void putLocalVariable(String name, Object object, ActionContext actionContext){
		FunctionParameter param =  getParameter(name);
		if(param != null){
			this.setParameterValue(name, object);
		}
		
		if(localVariables == null){
			localVariables = new HashMap<String, Object>();
		}		
		localVariables.put(name, object);
				
	}

	
	/**
	 * 是否是瞬态的事物。
	 * 
	 * @return
	 */
	public boolean isTransient(){
		return thing.isTransient();
	}
	
	/**
	 * 函数是否已经变更。
	 * 
	 * @return
	 */
	public boolean isModified(){
		if(thing.getMetadata().getLastModified() != this.loastModified){
			return true;
		}else{
			//如果参数改动了也被认为是已改动。
			if(thing.getChilds().size()  != params.size()){
				return true;
			}
						
			for(Thing child : thing.getChilds()){
				String name = child.getString("name");
				FunctionParameter param = this.getParameter(name);
				if(param == null || param.getValueThing() != child){
					return true;
				}
			}
			
			return false;
		}		
	}
	
	/**
	 * 返回本地变量，如果当前请求时局部变量容器，那么返回参数param之前的参数名和参数name相同的参数值，
	 * 否则返回上级函数的本地变量，如果没有返回null。
	 * 
	 * @param param
	 * @param name
	 * @return
	 */
	private Object getLocalVariable(FunctionParameter param, String name){				
		if(this.isLocalVariableContainer){
			if(localVariables != null && localVariables.containsKey(name)){
				return localVariables.get(name);
			}
			
			FunctionParameter param1 = this.getParameter(name);
			if(param1 != null){
				return param1.getValue();
			}
		}
		
		if(this.callback != null && this.callback.getRequest() != null && this.callback.getParam() != null){
			return callback.getRequest().getLocalVariable(callback.getParam(), name);
		}
		
		return null;
	}	
	
	/**
	 * 从上级的函数中获取局部变量。
	 * 
	 * @param name
	 * @return
	 */
	public Object getLocalVariable(String name){	
		if(this.callback != null && this.callback.getRequest() != null){
			return callback.getRequest().getLocalVariable(callback.getParam(), name);
		}
		
		return actionContext.get(name);
	}
	
	
	/**
	 * 设置一个本地变量，如果当然函数不是本地变量的持有者，那么尝试从父函数设置。
	 * 
	 * @param name
	 * @param object
	 */
	public void setLocalVariable(String name, Object object){
		FunctionRequest variableRequest = getLocalVariableDefinedRequest(this, name);
		if(variableRequest == null){
			throw new ActionException("Local variable " + name + " is not defiend");
		}else{
			variableRequest.putLocalVariable(name, object, actionContext);
		}
	}
		
	
	/**
	 * 返回定义指定变量名的函数请求。
	 * 
	 * @param name
	 * @return
	 */
	private FunctionRequest getLocalVariableDefinedRequest(FunctionRequest request, String name){
		if(request.isLocalVariableContainer){
			if(request.localVariables != null && request.localVariables.containsKey(name)){
				return request;
			}

			//参数也是局部变量
			if(request.getParameter(name) != null){
				return request;
			}
		}
		
		if(request.callback != null){
			FunctionCallback cbak = request.callback;
			if(cbak.getParam() != null && cbak.getRequest() != null){
				return getLocalVariableDefinedRequest(cbak.getRequest(), name);
			}			
		}
		
		return null;
	}
		
	/**
	 * 同getLocalVariable。
	 * 
	 * @param name
	 * @return
	 */
	public Object getLocalValue(String name){
		return getLocalVariable(name);
	}
	
	/**
	 * 返回函数调用者的参数值，如果函数是被另一个函数调用的，那么返回调用函数的参数值。
	 * 
	 * @param name 参数名
	 * @return 参数值
	 */
	public Object getCallerParameterVariable(String name){
		//FunctionRequest current = this;
		FunctionRequest parentFunctionRequest = getParentCall();
		/*
		while(parentFunctionRequest == null && current != null){
			if(current.getCallback() == null){
				break;
			}else{
				current = current.getCallback().getRequest();
				if(current != null){
					parentFunctionRequest = current.getParent();
				}
			}
		}
		*/
		if(parentFunctionRequest != null){
			FunctionParameter param = parentFunctionRequest.getParameter(name);
			if(param != null){
				return param.getValue();
			}
		}
		
		return null;
	}
	
	/**
	 * 获取父函数的指定参数值，即当前函数如果是参数的话获取并列某个参数的值。
	 * 
	 * @param name 参数名
	 * @return 参数的值
	 */
	public Object getParentParameterVariable(String name){
		if(this.getCallback() != null){
			FunctionRequest parentFunctionRequest = this.getCallback().getRequest();
			if(parentFunctionRequest != null){
				FunctionParameter param = parentFunctionRequest.getParameter(name);
				if(param != null){
					return param.getValue();
				}
			}
		}
		/*
		while(parentFunctionRequest == null && current != null){
			if(current.getCallback() == null){
				break;
			}else{
				current = current.getCallback().getRequest();
				if(current != null){
					parentFunctionRequest = current.getParent();
				}
			}
		}
		*/
		
		
		return null;
	}
	
	
	/**
	 * 返回所有局部变量名数组，每一维的第一个值是参数名，第二个值是参数的值。
	 * 
	 * @return
	 */
	public Object[][] getLocalVariables(){
		Map<String, Object> nameContext = new HashMap<String, Object>();
		if(this.callback != null && this.callback.getRequest() != null){
			initLocalVariableNames(nameContext, this.callback.getParam());
		}
		
		List<String> nameList = new ArrayList<String>();
		for(String key : nameContext.keySet()){
			nameList.add(key);
		}
		Collections.sort(nameList);
		
		Object[][] objs = new Object[nameList.size()][2];
		for(int i=0; i<objs.length; i++){
			String name = nameList.get(i);
			objs[i][0] = name;
			objs[i][1] = nameContext.get(name);
		}
		
		return objs;
	}
	
	/**
	 * 返回全局变量的数组，每一维的第一个值是参数名，第二个值是参数的值。
	 * 
	 * @return
	 */
	public Object[][] getGlobalVariables(){
		List<String> nameList = new ArrayList<String>();
		for(String key : actionContext.keySet()){
			if(key != null){
				nameList.add(key);
			}
		}
		Collections.sort(nameList);
		
		Object[][] objs = new Object[nameList.size()][2];
		for(int i=0; i<objs.length; i++){
			String name = nameList.get(i);
			objs[i][0] = name;
			objs[i][1] = actionContext.get(name);
		}
		
		return objs;
	}
	
	private void initLocalVariableNames(Map<String, Object> nameContext, FunctionParameter param){
		if(this.callback != null && this.callback.getRequest() != null){
			FunctionRequest fr = this.callback.getRequest();
			if(fr.isLocalVariableContainer){
				for(FunctionParameter p : fr.getParameters()){
					if(p == param || p.getName().equals(param.getName())){
						break;
					}else{
						if(!nameContext.containsKey(p.getName())){
							nameContext.put(p.getName(), p.getValue());
						}
					}
				}				
			}
			
			if(fr.getCallback() != null){
				fr.initLocalVariableNames(nameContext, fr.getCallback().getParam());
			}
		}
	}
	
	public boolean isProxy(){
		return "FunctionProxy".equals(thing.getThingName());
	}
	
	public Thing getProxyedThing(){
		Thing current = thing;
		while("FunctionProxy".equals(current.getThingName())){
			current = World.getInstance().getThing(current.getString("extends"));
		}
		
		return current;
	}
	
	/**
	 * 返回当前函数的执行位置，最多返回5个位置。
	 * 
	 * @return
	 */
	public String getLocation(){
		int count = 0;
		String l = descriptor.getMetadata().getLabel();
		if(callback != null){
			FunctionRequest parent = callback.getRequest(); 
			while(count < 5 && parent != null){
				l = parent.getDescriptor().getMetadata().getLabel() + "->" + l;
				
				if(parent.getCallback() != null){
					parent = parent.getCallback().getRequest();
				}else{
					parent = null;
				}
			}
			
			if(parent != null){
				l = "...->" + l;
			}
		}
		
		return l;
	}
	
	public static Thing getRealDescriptor(Thing thing){
		//子事物通常使用继承，此时应该获取继承到的函数
		if("thing".equals(thing.getThingName()) && thing.getExtends().size() > 0){
			return thing.getExtends().get(0);
		}else{
			return thing;
		}
		
		/*
		for(Thing ext : desc.getExtends()){
			Thing d = getRealDescriptor(ext);
			if(d != null){
				return d;
			}
		}
		
		return null;
		*/
	}
	
	/**
	 * 返回函数的描述。
	 * 
	 * @return
	 */
	public Thing getDescriptor(){
		return descriptor;
	}
	
	/**
	 * 如果函数事物在外部已经被修改，那么重新初始化。
	 * 
	 */
	public void reinit(){
		for(FunctionParameter param : params){
			for(Thing pchild : thing.getChilds()){
				if(param.getName().equals(pchild.getThingName())){
					param.setValueThing(pchild, param.getValue());
					break;
				}
			}
		}
	}
	
	public Thing getThing() {
		return thing;
	}

	/**
	 * 返回最开始的根函数请求，仅是当前函数链的根，如果函数是被父函数调用的，那么不算。
	 * 
	 * @return
	 */
	public FunctionRequest getRoot(){
		FunctionRequest current = this;
		while(current.getCallback() != null){
			FunctionCallback callback = current.getCallback();
			if(callback.getRequest() != null && callback.getParam() != null){
				current = callback.getRequest();
			}else{
				return current;
			}
		}
		
		return current;
	}
	
	/**
	 * 设置参数的值。
	 * 
	 * @param name
	 * @param value
	 */
	public void setParameterValue(String name, Object value){
		for(FunctionParameter param : params){
			if(param.getName().equals(name)){
				param.value = value;
				param.setReady(true);
				param.setExecuteWhenRunRequest(false);
				param.setTrustChecked(true);
				if(param.getValueRequest() != null){
					param.getValueRequest().setValue(value);
				}
				break;
			}
		}
	}
	
	/**
	 * 界面点击取消，如果没有callback似乎不能做什么所以返回false，否则回调callback。
	 * 
	 * @return
	 */
	public boolean cancel(){
		if(this.callback != null){
			callback.cancel(actionContext);
			return true;
		}else{
			return false;
		}
	}
	
	private boolean isHaveUnReadyParam(){		
		FunctionParameter firstUnreadyParam = getFirstUnReadyParam();
		if(firstUnreadyParam != null){
			//发送设置参数的请求
			FunctionManager.sendRequest(this, actionContext);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 执行延迟执行的参数。
	 * 
	 * @return
	 */
	public boolean executeDelayParam(){
		for(FunctionParameter param : params){
			if(param.isExecuteWhenRunRequest() || param.isTrustChecked() == false){
				executeParam(param);
				return true;
			}
		}
		
		return false;
	}
		
	/**
	 * <p>执行下一个参数，一般控制函数调用，比如while, do, for等。</p>
	 * 
	 * <p>比如while有x1, x2, x3 ,x4四个参数，其中x1作为条件，x2, x3, x4是要分别执行的语句，因为每次只执行一个，
	 * 等前一个执行完毕后就调用此方法执行下一个。</p>
	 * 
	 * 成功返回执行的下一个参数，否则返回null。
	 */
	public FunctionParameter executeNextParam(FunctionParameter lastParam){
		if(lastParam != null){
			int index = params.indexOf(lastParam);
			if(index != -1 && params.size() > index + 1){
				FunctionParameter param = params.get(index + 1);
				if(param.isCallByFuntion()){					
					return executeNextParam(param);
				}else{
					executeParam(param);				
					return param;
				}
			}		
		}else{
			if(params.size() > 0){
				FunctionParameter param = params.get(0);
				if(param.isCallByFuntion()){					
					return executeNextParam(param);
				}else{
					executeParam(param);				
					return param;
				}
			}
		}
		
		return null;
	}
	
	
	/**
	 * 执行指定的参数，一般控制函数调用，比如if等。
	 * 
	 * @param param
	 */
	public void executeParam(FunctionParameter param){
		if(functionMonitor != null){
			functionMonitor.beforeExecuteParameter(this, param);
		}
		param.run();
		//executor.execute(param);
	}
	
	/**
	 * 添加一个新的参数。
	 * 
	 * @param name
	 */
	public FunctionParameter addParameter(String name){
		Thing p = new Thing();
		p.put("name", name);
		p.put("extends", "xworker.lang.function.controls.ControlParameter");
		p.put("description", "<p>这个参数不是固定参数，是动态加入的参数，没有相关文档。</p>");
		FunctionParameter param = new FunctionParameter(this, p, null, name, false, null, false);
		params.add(param);
		return param;
	}
	
	/**
	 * 添加一个参数事物。
	 * 
	 * @param name
	 * @param paramThing
	 * @return
	 */
	public FunctionParameter addParameter(String name, Thing paramThing){
		FunctionParameter param = new FunctionParameter(this, paramThing, null, name, false, null, false);
		params.add(param);
		return param;
	}
	
	public boolean checkFunction(FunctionParameter lastParameter){
		boolean checkOk = true;
		Map<String, Object> checkResults = new HashMap<String, Object>();

		//函数的校验
		try{
			Bindings bindings = actionContext.push();
			bindings.put("request", this);
			bindings.put("lastParameter", lastParameter);
			Thing functionThing =thing;
			if(this.isProxy()){
				functionThing = this.getProxyedThing();
			}
			for(FunctionParameter param : params){
				bindings.put(param.getName(), param.getValue());
			}
			
			Object value = functionThing.doAction("doCheck", actionContext);
			if(value != null){
				checkResults.put("result", value);
				checkOk = false;
				
				//for(FunctionParameter param : params){
				//	param.setReady(false);
				//}
			}
		}finally{
			actionContext.pop();
		}
		if(!checkOk){
			checkResults.put("request", this);
			
			//校验失败，发送校验失败的信息
			UIRequest uiRequest = new UIRequest(World.getInstance().getThing("xworker.ui.function.FunctionCheckEerrorShell/@composite"), actionContext);
			
			//消息设置成FunctionRequest
			uiRequest.setRequestMessage(this);
			uiRequest.setFinishedMessage(checkResults);
			
			//向界面发送请求
			FunctionRequestUIFactory.requestUI(this, "xworker_session_swt_dialogComposite", uiRequest, actionContext);
		}
		
		return checkOk;
	}
	
	/**
	 * 回调时的执行，与普通执行相比不会重置参数执行的位置索引。
	 * 
	 * @param actionContext_
	 */
	public void callbackRun(FunctionParameter lastParameter, ActionContext actionContext_){
		//如果参数由有函数调用的，那么在回调时不触发父函数的相关操作
		if(lastParameter != null && lastParameter.getDescriptor().getBoolean("callByFuntion")){
			return;
		}
		
		runRequest(lastParameter, actionContext_);
	}
	
	/**
	 * 执行函数，执行前先充值参数执行位置索引为0，并检查参数是否是可信任的。
	 * 
	 * @param actionContext_
	 */
	public void run(ActionContext actionContext_){
		for(FunctionParameter param : params){
			param.checkTrust();
		}
		
		runRequest(null, actionContext_);
	}
	
	/**
	 * 执行函数，如果参数未准备好会抛出异常。
	 * 
	 * @param actionContext
	 * @return
	 */
	private void runRequest(FunctionParameter lastParameter, ActionContext actionContext_){
		//是否是断点或者单步执行等情况
		FunctionAssist assist = getFunctionAssist();
		if(assist.isStop(this, lastParameter, actionContext_)){									
			return;
		}
		
		//是否有未准备的参数
		if(isHaveUnReadyParam()){
			return;
		}
		
		if(!executeParameterMySelf && assist.getDebugStatus() != FunctionAssist.DEBUG_STEP_INTO){ 
			//如果不是自己执行参数，也不是调试状态，那么依次执行子参数
			if(this.executeNextParam(lastParameter) != null){
				return;
			}
		}
		
		//执行参数和函数校验，没有错误信息才继续执行，2015-01-22取消函数的校验，校验可以在执行时一起
		//if(!checkFunction(lastParameter)){
		//	return;
		//}
		
		//真正的执行函数
		Bindings bindings = actionContext.push();
		try{			
			if(functionMonitor != null){
				functionMonitor.beforeExecuteFunction(this);
			}
			
			bindings.put("envActionContext", actionContext_);
			bindings.put("request", this);
			bindings.put("lastParameter", lastParameter);
			
			for(FunctionParameter param : params){
				bindings.put(param.getName(), param.getValue());
			}
			
			Object result = thing.doAction("doFunction", actionContext);	
			boolean selfCallback = descriptor.getBoolean("selfCallback");
			if(!selfCallback){
				value = result;
			}
			
			if(callback != null && !selfCallback){
				callback.ok(value, actionContext_);
			}else{
				if(!selfCallback){
					FunctionManager.finishRequest(this, result);
				}
			}
			
			executed = true;
		}catch(Exception e){
			logger.error("Run function request error", e);
			
			FunctionManager.sendErrorRequest(this, e);
		}finally{
			actionContext.pop();
		}
	}
	
	@Override
	public String toString() {		
		
		String valueString = null;
		try{
			valueString = String.valueOf(value);
		}catch(Exception e){
			valueString = value.getClass().getName() + "&" + value.hashCode();
		}
		return "FunctionRequest [thing=" + thing + ", value=" + valueString + "]";
	}

	public FunctionParameter getFirstUnReadyParam(){
		for(FunctionParameter param : params){
			if(!param.isReady()){
				return param;
			}
		}
		
		return null;
	}
	
	/**
	 * 获取当前的参数列表。
	 * 
	 * @return
	 */
	public List<FunctionParameter> getParameters(){
		return params;
	}
	
	/**
	 * 根据参数名获取参数。
	 * 
	 * @param name
	 * @return
	 */
	public FunctionParameter getParameter(String name){
		for(FunctionParameter param : params){
			if(param.getName().equals(name)){
				return param;
			}
		}
		
		return null;
	}
	
	private boolean isOptionalParameter(Thing paramDescThing){
		return paramDescThing.getBoolean("optional");
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public FunctionCallback getCallback() {
		return callback;
	}
	
	/**
	 * 把执行的过程转变为要保存的事物。
	 * 
	 * @return
	 */
	public Thing getSavedThing(){
		//重新创建一个事物
		Thing newThing = new Thing(null, null, descriptor.getMetadata().getPath(), false);
		//也复制属性，相当于detach
		newThing.getAttributes().putAll(thing.getAttributes());
				
		//转化参数事物
		for(FunctionParameter param : params){
			Thing child = param.getSavedThing();
			if(child != null){
				newThing.addChild(child);
			}
		}

		String descriptors = newThing.getString("descriptors");
		if(descriptors != null && !"".equals(descriptors)){
			if(descriptors.indexOf("xworker.lang.function.FunctionImpl") == -1){				
				descriptors = descriptors + ",xworker.lang.function.FunctionImpl";
			}
		}else{
			descriptors = "xworker.lang.function.FunctionImpl";
		}
		newThing.put("descriptors", descriptors);
		
		if(description != null && !"".equals(description)){
			newThing.put("description", description);
		}
		
		this.thing = newThing;
		return newThing;
	}
	
	/**
	 * 保存请求事物，只有非瞬态的事物才能直接保存。
	 * 
	 */
	public void saveThing(){
		if(this.isTransient()){
			throw new ActionException("Only none transient request thing can be saved directly");
		}
		
		List<Thing> newChilds = new ArrayList<Thing>();
		for(FunctionParameter param : params){
			Thing child = param.getSavedThing();
			if(child != null){
				newChilds.add(child);
			}
		}
		
		List<Thing> childs = thing.getChilds();
		for(int i=0; i<childs.size(); i++){
			Thing child = childs.get(i);
			thing.removeChild(child);
			i--;
		}
		
		for(Thing child : newChilds){
			thing.addChild(child);
		}
		
		if(description != null && !"".equals(description)){
			thing.put("description", description);
		}
		thing.save();		
	}

	/**
	 * 按照名称删除参数
	 * 
	 * @param param
	 */
	public void removeParam(FunctionParameter param){
		for(FunctionParameter p : params){
			if(p == param){
				params.remove(p);
				break;
			}
		}
	}
	
	public boolean moveParamUp(FunctionParameter param){
		for(int i=0; i<params.size();i++){
			FunctionParameter p = params.get(i);
			if(p == param){
				if(i > 0){
					params.set(i, params.get(i - 1));
					params.set(i - 1, param);
					return true;
				}
				
				return false;				
			}
		}
		
		return false;
	}
	
	public boolean moveParamDown(FunctionParameter param){
		for(int i=0; i<params.size();i++){
			FunctionParameter p = params.get(i);
			if(p == param){
				if(i < params.size() - 1){
					params.set(i, params.get(i + 1));
					params.set(i + 1, param);
					return true;
				}
				return false;
			}
		}
		
		return false;
	}

	public FunctionParameter getFocusedParam() {
		return focusedParam;
	}

	public void setFocusedParam(FunctionParameter focusedParam) {
		this.focusedParam = focusedParam;
	}

	/**
	 * 返回函数执行的结果，如果函数还没有执行返回null。
	 * 
	 * @return
	 */
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value){
		this.value = value;
	}

	public FunctionStatus getStatus() {
		return status;
	}

	/**
	 * 设置函数请求的运行状态，一般是由参数子函数请求设置的，比如for的循环中的函数设置for的运行状态。
	 * 
	 * @param status
	 */
	public void setStatus(FunctionStatus status) {
		this.status = status;
	}

	public boolean isDynamicParameter() {
		return executeParameterMySelf;
	}

	public boolean isUnTrustValue() {
		return unTrustValue;
	}

	public void setUnTrustValue(boolean unTrustValue) {
		this.unTrustValue = unTrustValue;
	}

	/**
	 * 返回函数调用的父函数，即当前根函数的调用者。
	 * 
	 * @return 父函数，不存在则返回null
	 */
	public FunctionRequest getParentCall() {
		FunctionRequest root = this.getRoot();
		if(root != null){
			return root.parent;
		}else{
			return null;
		}
		/*
		if(parent == null){
			if(callback != null && callback.getRequest() != null){
				//return callback.getRequest().getParent();
				return callback.getRequest();
			}else{
				return null;
			}
		}else{
			return parent;
		}*/
	}
	
	/**
	 * 获取当前函数的父函数，如果是参数函数返回包含参数的函数，否则返回自己父函数。
	 */
	public FunctionRequest getParent() {
		if(parent == null){
			if(callback != null && callback.getRequest() != null){
				//return callback.getRequest().getParent();
				return callback.getRequest();
			}else{
				return null;
			}
		}else{
			return parent;
		}
	}


	public void setParent(FunctionRequest parent) {
		this.parent = parent;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Thing getDescriptionThing() {
		if(description != null && !"".equals(description)){
			if(descriptionThing == null){
				descriptionThing = new Thing();
				descriptionThing.put("description", description);
			}
						
			return descriptionThing;
		}else{
			return getThing();
		}
	}

	public FunctionAssist getFunctionAssist() {
		if(this.functionAssist == null){
			this.functionAssist = getRootFunctionAssist();
		}
		
		return this.functionAssist;
	}
	
	public void setFunctionAssist(FunctionAssist functionAssist) {
		this.functionAssist = functionAssist;
	}

	public boolean isExecuted() {
		return executed;
	}

	private FunctionAssist getRootFunctionAssist(){
		FunctionRequest root = this.getRoot();
		if(root.functionAssist == null){
			root.functionAssist = new FunctionAssist();
		}
		return root.functionAssist;
	}

	public FunctionRequest getChildFunctionRequest() {
		return childFunctionRequest;
	}

	public void setChildFunctionRequest(FunctionRequest childFunctionRequest) {
		this.childFunctionRequest = childFunctionRequest;
	}

	public FunctionRequest getUiKey() {
		if(uiKey == null){
			FunctionRequest root = this.getRoot();
			if(root.uiKey == null){
				root.uiKey = root;
			}
			
			return root.uiKey;
		}else{
			return uiKey;
		}
	}

	/**
	 * 返回函数的请求栈，函数调用函数时才会产生新的栈。
	 * 
	 * @return
	 */
	public List<FunctionRequest> getStacks(){
		List<FunctionRequest> stacks = new ArrayList<FunctionRequest>();
		FunctionRequest root = getRoot();
		stacks.add(root);
		
		FunctionRequest parent = root.getParentCall();
		while(parent != null){
			root = parent.getRoot();
			stacks.add(root);
			
			parent = root.getParentCall();
		}
		
		return stacks;
	}
	
	public void setUiKey(FunctionRequest uiKey) {
		this.uiKey = uiKey;
	}

	public void setCallback(FunctionCallback callback) {
		this.callback = callback;
	}

	public boolean isCloseUIOnFinished() {
		return closeUIOnFinished;
	}

	public void setCloseUIOnFinished(boolean closeUIOnFinished) {
		this.closeUIOnFinished = closeUIOnFinished;
	}

	public boolean isLocalVariableContainer() {
		return isLocalVariableContainer;
	}

	/**
	 * 是否定义了指定名称的局部变量。
	 * 
	 * @param name
	 * @return
	 */
	public boolean isDefinedLocalVariable(String name){
		return isLocalVariableContainer() && localVariables != null && localVariables.containsKey(name);
	}
	
	/**
	 * 定义局部变量。
	 * 
	 * @param name
	 * @param value
	 */
	public void definedLocalVariable(String name, Object value){
		if(isLocalVariableContainer()){
			//上级函数是否定义的此变量
			if(isLocalVariableExists(this, name)){
				throw new ActionException("Local variable " + name + " already exists");
			}else{
				this.putLocalVariable(name, value, actionContext);
			}
		}else{
			throw new ActionException("Current function is not a variable container");
		}
	}
	
	public static boolean isLocalVariableExists(FunctionRequest request, String name){
		FunctionRequest current = request;
		while(current.getCallback() != null){
			FunctionCallback callback = current.getCallback();
			if(callback.getRequest() != null && callback.getParam() != null){
				FunctionRequest nextRequest = callback.getRequest();

				if(nextRequest.isDefinedLocalVariable(name)){
					return true;
				}
				
				current = callback.getRequest();
			}else{
				return false;
			}
		}
		
		return false;
	}
}