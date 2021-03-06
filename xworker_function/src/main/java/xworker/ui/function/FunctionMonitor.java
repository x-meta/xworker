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

/**
 * 函数监控器，用于监控函数的执行。
 * 
 * @author Administrator
 *
 */
public interface FunctionMonitor {
	/**
	 * 执行
	 * 
	 * @param param
	 */
	public void beforeExecuteParameter(FunctionRequest request, FunctionParameter param);
	
	public void beforeExecuteFunction(FunctionRequest request);
	
	public void afterExecutedParameter(FunctionRequest request, FunctionParameter param);
	
	public void afterExecutedFunction(FunctionRequest request);
}