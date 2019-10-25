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

import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

/**
 * 函数参数。
 * 
 * @author Administrator
 *
 */
public class FunctionParameter implements Runnable{
	public static final int SAVETYPE_THING = 0;
	public static final int SAVETYPE_VALUE = 1;
	public static final int SAVETYPE_NONE = 2;
	
	/** 参数名称 */
	String name;
	
	/** 参数值 */
	Object value;
	
	/** 参数事物，执行后可以获取参数值 */
	Thing valueThing;
	
	/** 参数的描述者 */
	Thing descriptor;
	
	/** 函数请求 */
	FunctionRequest request;
	
	/** 参数的FunctionRequest，参数事物执行时也当做函数 */
	FunctionRequest valueRequest;
	
	/** 参数是否可选 */
	boolean optional;
	
	/** 是否已设置 */
	boolean ready;	
	
	/** 当执行函数请求时是否再执行参数 */
	boolean executeWhenRunRequest = true;
	
	/** 保存时是否只保存值 */
	boolean saveValue = false;
	
	/** 是否已执行过信任检查  */
	boolean trustChecked = false;
	
	/** 参数的注释，参数的注释是放到参数值事物（参数函数事物）上的，而参数事物是可变的，但参数的描述一般不变，所以缓存 */
	String description;
	
	int saveType = 0;
	
	boolean valueThingChanged = false;
	
	/** 临时的参数描述事物  */
	Thing descriptionThing;
	
	public FunctionParameter(FunctionRequest request, Thing descriptor, Thing valueThing, String name, boolean optional, Object value, boolean ready){
		this.name = name;
		this.request = request;
		this.optional = optional;
		this.ready = ready;		
		this.descriptor = descriptor;
				
		this.setValueThing(valueThing, value);
		valueThingChanged = false;
	}
	
	public boolean setBestFunction(){
		//最佳函数
		Thing bestFunction = descriptor.getThing("BestFunction@0");

		if(bestFunction != null){
			String functionPath = bestFunction.getStringBlankAsNull("functionPath");
			if(functionPath != null){
				if(bestFunction.getBoolean("isDescriptor")){
					bestFunction = new Thing(functionPath);
				}else{
					bestFunction = new Thing("xworker.ui.function.common.CallFunction");
					Thing bestValueThing = new Thing("xworker.lang.function.values.StringValue");
					bestFunction.addChild(bestValueThing);
					bestValueThing.put("name", "functionPath");
					bestValueThing.put("value", functionPath);
					//bestFunction = new Thing("xworker.lang.function.FunctionProxy");
					//bestFunction.put("extends", functionPath);
				}
			}else{
				Thing bestFn = bestFunction.getThing("Function@0");
				if(bestFn != null){
					bestFunction = new Thing("xworker.ui.function.common.CallFunction");
					Thing bestValueThing = new Thing("xworker.lang.function.values.StringValue");
					bestValueThing.put("name", "functionPath");
					bestValueThing.put("value", bestFn.getMetadata().getPath());
					bestFunction.addChild(bestValueThing);
					//bestFunction = new Thing("xworker.lang.function.FunctionProxy");
					//bestFunction.put("extends", bestFn.getMetadata().getPath());
				}
			}
			bestFunction.put("name", name);
			
			this.setValueThing(bestFunction, null);
			
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 是否是瞬态的事物。
	 * 
	 * @return
	 */
	public boolean isTransient(){
		return valueThing == null || valueThing.isTransient();
	}
	
	/**
	 * 函数是否已经变更。
	 * 
	 * @return
	 */
	public boolean isModified(){
		if(valueThingChanged){
			return true;
		}
		
		if(valueRequest != null){
			return valueRequest.isModified();
		}
		
		return false;
	}

	/**
	 * 获取要保存的事物。
	 * 
	 * @return
	 */
	public Thing getSavedThing(){
		switch(saveType){
		case FunctionParameter.SAVETYPE_NONE:
			return null;
		case FunctionParameter.SAVETYPE_THING:
			if(valueRequest != null){
				Thing thing = valueRequest.getSavedThing();
				if(thing != null){
					thing.put("name", name);
				}
				
				if(thing != null && description != null){
					thing.put("description", description);
				}
				return thing;
			}else{
				return null;
			}
		case FunctionParameter.SAVETYPE_VALUE:
			Thing thing = getSavedValueThing();
			if(thing != null && description != null){
				thing.put("description", description);
			}
			return thing;
		}
		
		return null;
	}
	
	/**
	 * 把value转成对应的值函数。
	 * 
	 * @return
	 */
	private Thing getSavedValueThing(){
		Thing thing = null;
		if(valueThing != null){
			thing = (Thing) valueThing.doAction("getSavedValueThing", valueRequest.getActionContext(), 
					UtilMap.toMap(new Object[]{"name", name, "value", value}));
			if(thing != null){
				return thing;
			}
		}
		
		return FunctionRequestUtil.getValueThing(name, value);
	}
	
	public String getType(){
		return descriptor.getString("type");
	}
	
	/** 
	 * 执行并获取参数值 
	 */
	public void execute(){
		if(valueThing != null){
			value = valueThing.doAction("run", request.getActionContext());
		}
	}
	
	public void run(){		
		//同样使用FunctionRequest		
		if(valueThing != null){			
			valueRequest.run(request.getActionContext());
		}else{
			//没有参数事物，直接回调
			FunctionCallback callback = new FunctionCallback(this);
			callback.ok(value, request.getActionContext());
		}
	}
	
	/**
	 * 回调。。
	 */
	public void callback(){
		FunctionCallback callback = new FunctionCallback(this);
		callback.ok(value, request.getActionContext());
	}
	
	/**
	 * 检查是否可信任。
	 * 
	 * @return
	 */
	public boolean checkTrust(){
		if(valueRequest != null){
			for(FunctionParameter param : valueRequest.getParameters()){
				trustChecked = trustChecked & param.checkTrust();
			}
			
			trustChecked = trustChecked & (!valueRequest.isUnTrustValue());
		}else{
			trustChecked = true;
		}
		
		return trustChecked;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getLabel(){
		Thing valueThing = getValueThing();
		if(valueThing != null){
			return valueThing.getMetadata().getLabel();
		}
		return name;
	}

	public Object getValue() {
		return value;
	}

	public Thing getValueThing() {
		if(valueRequest != null){
			return valueRequest.getThing();
		}
		return valueThing;
	}
	
	public String getValueThingPathLabel(){
		if(valueThing.isTransient()){
			return valueThing.getDescriptor().getMetadata().getLabel();
		}else{
			return valueThing.getMetadata().getLabel();
		}
	}
	
	/**
	 * 设置参数事物，同时重置参数事物的请求以及参数值，并设置ready=false。
	 * 
	 * @param valueThing
	 * @param value
	 */
	public void setValueThing(Thing valueThing, Object value) {
		if(valueThing != null){
			this.valueThing = valueThing;			
			if(valueRequest != null){
				//读取之前的描述
				this.valueThing.set("description", valueRequest.getDescription());
			}
			valueRequest = new FunctionRequest(this.valueThing, new FunctionCallback(this), false, request.getActionContext());
		}else{
			this.valueThing = null;
			valueRequest = null;
			trustChecked = false;
		}

		this.value = value;
		if(this.value == null && valueThing == null){
			this.ready = false;
		}else{
			this.ready  = true;
		}
		
		if(valueThing != null && (value == null || "".equals(String.valueOf(value)))){
			executeWhenRunRequest = true;
		}else{
			executeWhenRunRequest = false;
		}
		
		valueThingChanged = true;
	}

	public FunctionRequest getValueRequest(){
		return valueRequest;
	}
	
	public boolean isReady(){
		return ready;
	}
	
	public boolean isCallByFuntion(){
		return descriptor.getBoolean("callByFuntion");
	}
	
	public void setReady(boolean ready){
		this.ready = ready;
	}

	/**
	 * 设置参数值以及产生参数值的事物。
	 */
	public void setValue(Object value, Thing valueThing) {
		this.setValueThing(valueThing, value);
		
		this.value = value;
		this.ready = true;
		this.executeWhenRunRequest = false;
	}
	
	/**
	 * 设置参数的事物为变量。
	 * 
	 * @param name
	 * @param value
	 */
	public void setVariableValue(String name, Object value, boolean isLocal){
		Thing variable = new Thing(isLocal ? "xworker.ui.function.common.GetLocalVariable" : "xworker.ui.function.common.GetGlobalVariable");
		Thing param = new Thing("xworker.lang.function.values.StringValue");
		param.set("name", "variableName");
		param.set("value", name);
		variable.addChild(param);
		
		setValue(value, variable);
	}
	
	/**
	 * 设置执行Groovy脚本后产生的参数值。
	 * 
	 * @param value
	 * @param groovyScript
	 */
	public void setGroovyValue(Object value, String groovyScript){
		Thing scriptThing = new Thing("xworker.lang.function.values.GroovyValue");
		scriptThing.put("code", groovyScript);
		
		this.setValue(value, scriptThing);
	}
	

	public FunctionRequest getRequest() {
		return request;
	}

	public Thing getDescriptor() {
		return descriptor;
	}

	public boolean isOptional() {
		return optional;
	}

	public boolean isExecuteWhenRunRequest() {
		return executeWhenRunRequest;
	}

	public void setExecuteWhenRunRequest(boolean executeWhenRunRequest) {
		this.executeWhenRunRequest = executeWhenRunRequest;
	}

	public boolean isSaveValue() {
		return saveValue;
	}

	public void setSaveValue(boolean saveValue) {
		this.saveValue = saveValue;
	}

	public int getSaveType() {
		return saveType;
	}

	public void setSaveType(int saveType) {
		this.saveType = saveType;
	}

	public boolean isTrustChecked() {
		return trustChecked;
	}

	@Override
	public String toString() {
		return "FunctionParameter [name=" + name + ", value=" + value
				+ ", valueThing=" + valueThing + "]";
	}

	public void setTrustChecked(boolean trustChecked) {
		this.trustChecked = trustChecked;
	}

	public String getDescription() {
		if(this.description == null && this.valueThing != null){
			this.description = this.valueThing.getString("description");
		}
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 返回参数的描述文档的事物。
	 * 
	 * @return
	 */
	public Thing getDescriptionThing() {
		if(this.description != null && !"".equals(this.description)){
			if(descriptionThing == null){
				descriptionThing = new Thing();
				descriptionThing.put("description", description);
			}
			
			return descriptionThing;
		}else{
			if(valueThing != null){
				return valueThing;
			}else{
				return descriptor;
			}
		}		
	}
}