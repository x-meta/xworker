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

import org.xmeta.ActionContext;
import org.xmeta.Thing;

/**
 * UI请求处理器，负责处理UI的请求。
 * 
 * @author Administrator
 *
 */
public interface UIHandler {
	/**
	 * 执行UI操作。
	 * 
	 * @param request
	 * @param actionContext
	 * 
	 */
	public void requestUI(UIRequest request, ActionContext actionContext);
	
	/**
	 * 结束UI请求。
	 * 
	 * @param request 请求标识
	 * @param actionContext 变量上下文
	 */
	public void finishUI(UIRequest request, ActionContext actionContext);
	
	/**
	 * 返回定义UIHandler的事物。
	 * 
	 * @return
	 */
	public Thing getThing();
}