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
package xworker.ui;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

public class UIRequest {
	/** 请求UI的事物 */
	private Thing thing;
	
	/** 请求UI的事物自己设定的标识 */
	private String id;
	
	/** 优先级，总是先处理优先级高的UI请求 */
	private int priority;
	
	/** 请求时附带的消息，请求者可以利用此消息做一些  */
	private Object requestMessage;
	
	/** 终止时附带的消息 */
	private Object finishedMessage;
	
	/** UI请求时的变量上下文 */
	private ActionContext actionContext;
	
	/** 接受请求并处理的uiHandlerId，有可能为空，如果直接调用uiHandler而不经过UIMannager的话 */
	private String uiHandlerId;
	
	/** 是否UIHandler处理了请求后回调请求事物的requestCallback方法，UIRequest的请求和结束都是自己主动发起的 */
	private boolean requestCallback = false;
	
	/** 是否UIHandler结束了请求后回调事物的finishCallback方法 ，UIRequest的请求和结束都是自己主动发起的 */
	private boolean finishCallback = false;
	
	/** 用户自定义参数 */
	private Map<String, Object> datas = null;
	
	public UIRequest(Thing thing, ActionContext actionContext){
		this(thing, null, 0, actionContext);
	}
	
	public UIRequest(Thing thing, String id, int priority, ActionContext actionContext){
		this.thing = thing;
		this.id = id;
		if(this.id == null){
			this.id = "";
		}
		this.priority = priority;
		this.actionContext = actionContext;
	}

	/**
	 * 设置用户自定义的数据。
	 * 
	 * @param key
	 * @param value
	 */
	public void setData(String key , Object value){
		if(datas == null){
			datas = new HashMap<String, Object>();
		}
		
		datas.put(key, value);
	}
	
	/**
	 * 获取用户自定的数据。
	 * 
	 * @param key
	 * @return
	 */
	public Object getData(String key){
		if(datas != null){
			return datas.get(key);
		}else{
			return null;
		}
	}
	
	public String getThingPath(){
		return thing.getMetadata().getPath();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof UIRequest){
			UIRequest r = (UIRequest) obj;
			if(r.thing.getMetadata().getPath().equals(thing.getMetadata().getPath()) && id.equals(r.id)){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public boolean equals(Thing thing, String id) {
		if(thing.getMetadata().getPath().equals(this.thing.getMetadata().getPath()) && this.id.equals(id)){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public String toString() {
		return "UIRequest [thing=" + thing.getMetadata().getPath() + ", id=" + id
				+ ", priority=" + priority + ", requestMessage="
				+ requestMessage + ", finishedMessage=" + finishedMessage + "]";
	}
	
	public Thing getThing(){
		return thing;
	}

	/**
	 * 执行UIRequest，通常是由UIHandler发起的操作。通常UIHandler会传入参数，具体参数参考UIHandler，
	 * 通常UIHandler有一个message参数，值一般为ok,cancel等，为用户点击的对应的按钮。
	 * 
	 * @param params
	 * @return
	 */
	public Object run(Map<String, Object> params){
		try{
			Bindings bindings = actionContext.push();
			bindings.put("request", this);
			if(params != null){
				bindings.putAll(params);
			}
			return thing.doAction("run", actionContext);
		}finally{
			actionContext.pop();
		}		
	}
	
	public Object run(){
		return thing.doAction("run", actionContext, UtilMap.toMap(new Object[]{"request", this}));
	}
	
	public Object finish(){
		return thing.doAction("finish", actionContext, UtilMap.toMap(new Object[]{"request", this}));
	}
	
	public Object getRequestMessage() {
		return requestMessage;
	}

	public void setRequestMessage(Object requestMessage) {
		this.requestMessage = requestMessage;
	}

	public Object getFinishedMessage() {
		return finishedMessage;
	}

	public void setFinishedMessage(Object finishedMessage) {
		this.finishedMessage = finishedMessage;
	}

	public String getId() {
		return id;
	}

	public int getPriority() {
		return priority;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public String getUiHandlerId() {
		return uiHandlerId;
	}

	public void setUiHandlerId(String uiHandlerId) {
		this.uiHandlerId = uiHandlerId;
	}

	public boolean isRequestCallback() {
		return requestCallback;
	}

	public boolean isFinishCallback() {
		return finishCallback;
	}

	public void setRequestCallback(boolean requestCallback) {
		this.requestCallback = requestCallback;
	}

	public void setFinishCallback(boolean finishCallback) {
		this.finishCallback = finishCallback;
	}
}