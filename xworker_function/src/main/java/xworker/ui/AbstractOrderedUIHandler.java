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

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public abstract class AbstractOrderedUIHandler extends AbstractUIHandler{
	/** UI请求列表 */
	protected List<UIRequest> requests = new ArrayList<UIRequest>();
	
	/** 当前正在处理的UI请求 */
	protected UIRequest currentRequest = null;
	
	public AbstractOrderedUIHandler(Thing thing, String uiHandlerId, ActionContext actionContext){
		super(thing, uiHandlerId, actionContext);

	}
	
	@Override
	public synchronized void requestUI(UIRequest request, ActionContext actionContext) {
		//插入到请求队列中
		boolean inserted = false;
		for(int i=0; i<requests.size(); i++){
			UIRequest r = requests.get(i);
			
			if(request.equals(r)){
				//请求已经存在了
				this.repeatRequst(r, actionContext);
			}else if(request.getPriority() > r.getPriority()){
				//在前面插入请求
				requests.add(i, request);
				inserted = true;
				
				inserted(request, i, actionContext);
				break;
			}
		}
		if(!inserted){
			//插入到最后
			requests.add(request);
			
			inserted(request, requests.size() -1 , actionContext);
		}
		
		//检查是否可以执行UI
		checkDoUI(actionContext);
	}

	@Override
	public synchronized void finishUI(UIRequest request, ActionContext actionContext) {
		for(int i=0; i<requests.size(); i++){
			UIRequest request_ = requests.get(i);
			if(request_.equals(request)){
				if(request_ != request){
					request_.setFinishedMessage(request.getFinishedMessage());
				}
				
				requests.remove(i);
				finished(request, i, actionContext);
				
				if(currentRequest == request_){
					currentRequest = null;
					currentFinished(request_, actionContext);
				}
				
				break;
			}
		}
		
		checkDoUI(actionContext);
	}

	private void checkDoUI(ActionContext actionContext){
		//查看是否可以马上处理，如果当前没有请求处理并且它已经是最前面的了
		if(currentRequest == null && requests.size() > 0){
			currentRequest = requests.get(0);
			doUI(currentRequest, actionContext);
		}
	}
	
	/**
	 * 返回当前所有的UI请求。
	 * 
	 * @return
	 */
	public List<UIRequest> getUIRequests(){
		return requests;
	}
	
	/**
	 * 插入了新的UI请求。
	 * 
	 * @param request
	 * @param actionContext
	 */
	public abstract void inserted(UIRequest request, int index, ActionContext actionContext);
	
	/**
	 * 执行UI请求。
	 * 
	 * @param request
	 */
	protected abstract void doUI(UIRequest request, ActionContext actionContext);
	
	/**
	 * 一个请求结束或者取消了。
	 * 
	 * @param request
	 */
	protected abstract void finished(UIRequest request, int index, ActionContext actionContext);
	
	/**
	 * 当前的请求结束或取消了。
	 * 
	 * @param request
	 */
	protected abstract void currentFinished(UIRequest request, ActionContext actionContext);
	
	/**
	 * 重复的请求。
	 * 
	 * @param request
	 * @param actionContext
	 */
	protected abstract void repeatRequst(UIRequest request, ActionContext actionContext);
}