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

import org.eclipse.swt.widgets.Display;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.swt.design.Designer;

/**
 * 函数请求事物。
 * 
 * @author Administrator
 *
 */
public class FunctionRequestThing {
	
	public static Object run(ActionContext actionContext) throws OgnlException, InterruptedException{
		Thing self = (Thing) actionContext.get("self");
		
		//函数请求
		FunctionRequest request = getFunctionReqeust(self, actionContext);
		
		if(request == null){
			throw new ActionException("FunctionRequest: cann't find FunctionRequest, please set a funcitonRequest, thing=" + self.getMetadata().getPath());
		}
		
		boolean sync = self.getBoolean("sync");
		Object lockObj = null;
		if(sync){
			//同步时不执行默认的回调
			lockObj = new Object();			
			request.setCallback(new FunctionCallback(lockObj));
		}
		
		//执行函数		
		request.run(actionContext);			
		if(!request.executed && sync){
			//如果没有执行完毕，可能是进入UI了，SWT是单线程的，所以需要处理SWT的事件			
			Display display = Designer.getExplorerDisplay();
			if(display.getThread() != Thread.currentThread()){
				
				synchronized(lockObj){
					if(!request.executed){
						lockObj.wait();
					}
				}
				
				FunctionManager.finishRequest(request, request.value);
			}else{
				while(!request.executed && display != null){				
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				}
				
				FunctionManager.finishRequest(request, request.value);
			}
		}
		
		System.out.println("FunctionRequestThing: finished");
		if(request.executed){
			return request.value;
		}else{
			return null;
		}
	}
	
	public static FunctionRequest getFunctionReqeust(Thing self, ActionContext actionContext) throws OgnlException{
		//直接获取FunctionRequest变量
		String functionRequestVarName = self.getStringBlankAsNull("functionRequestVarName");
		if(functionRequestVarName != null){
			return (FunctionRequest) OgnlUtil.getValue(self, "functionRequestVarName", functionRequestVarName, actionContext);
		}
		
		//取函数实例
		Thing functionIns = null;
		String functionInstanceVarName = self.getStringBlankAsNull("functionInstanceVarName");
		if(functionInstanceVarName != null){
			functionIns = (Thing) OgnlUtil.getValue(self, "functionInstanceVarName", functionInstanceVarName, actionContext); 
		}
		
		if(functionIns == null){
			String functionInstancePath = self.getStringBlankAsNull("functionInstancePath");
			if(functionInstancePath != null){
				functionIns = World.getInstance().getThing(functionInstancePath);
			}
		}
		
		if(functionIns == null){
			String functionDescriptorVarName = self.getStringBlankAsNull("functionDescriptorVarName");
			if(functionDescriptorVarName != null){
				Thing desc = (Thing) OgnlUtil.getValue(self, "functionDescriptorVarName", functionDescriptorVarName, actionContext);
				if(desc != null){
					functionIns = new Thing(desc.getMetadata().getPath());
				}
			}
		}
		
		if(functionIns == null){
			String functionDescriptor = self.getStringBlankAsNull("functionDescriptor");
			if(functionDescriptor != null){
				functionIns = new Thing(functionDescriptor);
			}
		}
		
		if(functionIns == null){
			Thing function = self.getThing("Function@0");
			if(function != null && function.getChilds().size() > 0){
				functionIns = function.getChilds().get(0);
			}
		}
		
		if(functionIns == null){
			throw new ActionException("FunctionRequest: cann't find FunctionRequest, please set a funcitonRequest, thing=" + self.getMetadata().getPath());
		}else{
			return new FunctionRequest(functionIns, actionContext);
		}
	}
}