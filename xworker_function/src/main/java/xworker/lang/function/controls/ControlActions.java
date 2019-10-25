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
package xworker.lang.function.controls;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.ui.function.FunctionParameter;
import xworker.ui.function.FunctionQuietRunner;
import xworker.ui.function.FunctionRequest;
import xworker.ui.function.FunctionRequestUtil;
import xworker.ui.function.FunctionStatus;

public class ControlActions {
	public static FunctionParameter getLastParameter(ActionContext actionContext){
		return (FunctionParameter) actionContext.get("lastParameter");
	}
	
	/**
	 * 以安静模式执行一个参数。
	 * 
	 * @param functionThing
	 * @param paramName
	 * @param actionContext
	 * @return
	 */
	public static Object executeParamQuiet(Thing functionThing, String paramName, ActionContext actionContext){
		for(Thing child : functionThing.getChilds()){
			if(child.getMetadata().getName().equals(paramName)){
				return FunctionQuietRunner.runFunction(child, actionContext);
			}
		}
		
		return null;
	}
	
	/**
	 * 函数Switch的实现，函数已删除，Switch使用If代替可能更好一些。
	 * 
	 * @param actionContext
	 */
	public static void switchFunction(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");

		FunctionParameter lastParameter = getLastParameter(actionContext);
		//------------判断if的状态，通常状态是子函数设置的，比如Break,Contine，Return等函数---------
		if(request.getStatus() == FunctionStatus.BREAK || request.getStatus() == FunctionStatus.CONTINUE){
			request.setStatus(FunctionStatus.RUNNING);
			FunctionRequestUtil.callbakMyselfOk(request, request.getStatus(), null, actionContext);			
			return;
		}else if(request.getStatus() == FunctionStatus.RETURN){
			request.setStatus(FunctionStatus.RUNNING);
			Object value = lastParameter != null ? lastParameter.getValue() : null;
			FunctionRequestUtil.callbakMyselfOk(request, FunctionStatus.RETURN, value, actionContext);
			return;
		}
		
		if(request.getParameters().size() == 0){
			//没有参数直接返回
			FunctionRequestUtil.callbakMyselfOk(request, null, actionContext);
		}
		
		int paramExecuteIndex = lastParameter != null ? request.getParameters().indexOf(lastParameter) : 0;		
		if(paramExecuteIndex == 0){
			//执行参数
			request.executeNextParam(lastParameter);
			return;
		}else if(paramExecuteIndex == request.getParameters().size()){
			//执行完毕
			FunctionRequestUtil.callbakMyselfOk(request, null, actionContext);
			return;
		}else if(paramExecuteIndex == 1){
			String caseValue = String.valueOf(request.getParameters().get(0).getValue());
			
			int newIndex = -1;
			for(int i=1; i<request.getParameters().size(); i++){
				FunctionParameter param = request.getParameters().get(i);
				if(caseValue.equals(param.getName()) || (i == request.getParameters().size() - 1 && "default".equals(param.getName()))){
					newIndex = i;
					break;
				}
			}
			
			if(newIndex != -1){
				request.executeNextParam(null);
			}
		}
	}
	
	/**
	 * 函数Begin的实现。
	 * 
	 * @param actionContext
	 */
	public static Object beginFunction(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		
		if(request == null){
			//非UI安静执行
			Thing self = (Thing) actionContext.get("self");
			Object result = null;
			try{
				//Begin是一个局部变量标志
				actionContext.push().setVarScopeFlag();//.isVarScopeFlag = true;
				for(Thing action : self.getChilds()){
				    result = FunctionQuietRunner.runFunction(action, actionContext);
				    //参数的值放入到局部变量中
				    actionContext.getScope().put(action.getMetadata().getName(), result);
	                        
	                if(ActionContext.RETURN == actionContext.getStatus() || 
	                    ActionContext.CANCEL == actionContext.getStatus() || 
	                    ActionContext.BREAK == actionContext.getStatus() || 
	                    ActionContext.EXCEPTION == actionContext.getStatus()){
	                    break;
	                }
		        }
			}finally{
				actionContext.pop();
			}
			
			return result;
		}else{
			FunctionParameter lastParameter = getLastParameter(actionContext);
			
			//------------判断if的状态，通常状态是子函数设置的，比如Break,Contine，Return等函数---------
			if(request.getStatus() == FunctionStatus.BREAK || request.getStatus() == FunctionStatus.CONTINUE){
				request.setStatus(FunctionStatus.RUNNING);
				FunctionRequestUtil.callbakMyselfOk(request, request.getStatus(), null, actionContext);			
				return null;
			}else if(request.getStatus() == FunctionStatus.RETURN){
				request.setStatus(FunctionStatus.RUNNING);
				Object value = lastParameter != null ? lastParameter.getValue() : null;
				FunctionRequestUtil.callbakMyselfOk(request, FunctionStatus.RETURN, value, actionContext);
				return value;
			}
			
			int paramExecuteIndex = lastParameter != null ? request.getParameters().indexOf(lastParameter) : -1;
			if(paramExecuteIndex + 1 == request.getParameters().size()){
				//group执行完毕，返回最后一个参数的值
				Object value = null;
				if(lastParameter != null){
					value = lastParameter.getValue();
				}
				FunctionRequestUtil.callbakMyselfOk(request, value, actionContext);
				return null;
			}else{
				request.executeNextParam(lastParameter);
			}
			
			return null;
		}
	}
	
	/**
	 * IF函数的实现。
	 * 
	 * @param actionContext
	 */
	public static Object ifFunction(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");

		if(request == null){
			Thing self = (Thing) actionContext.get("self");
			
			//执行condition
			boolean doThen = true; 
			for(Thing child : self.getChilds()){
				if("condition".equals(child.getMetadata().getName())){
					Object result = FunctionQuietRunner.runFunction(child, actionContext);
					if(!isTrue(result)){
						doThen = false;
					}
				}
				break;
			}
			
			String nextName = "then";
			if(!doThen){
				nextName = "else";
			}
			
			for(Thing child : self.getChilds()){
				if(nextName.equals(child.getMetadata().getName())){
					return FunctionQuietRunner.runFunction(child, actionContext);
				}
			}
			
			return null;
		}else{
			FunctionParameter lastParameter = getLastParameter(actionContext);
			
			//------------判断if的状态，通常状态是子函数设置的，比如Break,Contine，Return等函数---------
			if(request.getStatus() == FunctionStatus.BREAK || request.getStatus() == FunctionStatus.CONTINUE){
				request.setStatus(FunctionStatus.RUNNING);
				FunctionRequestUtil.callbakMyselfOk(request, request.getStatus(), null, actionContext);			
				return null;
			}else if(request.getStatus() == FunctionStatus.RETURN){
				request.setStatus(FunctionStatus.RUNNING);
				Object value = lastParameter != null ? lastParameter.getValue() : null;
				FunctionRequestUtil.callbakMyselfOk(request, FunctionStatus.RETURN, value, actionContext);
				return null;
			}
			
			//--------------------执行if----------------------------
			if(lastParameter != null && lastParameter.getName().equals("condition")){
				//如果已执行condition那么根据condition的返回值执行then或else
				FunctionParameter next = null;
				if(getTrueFalse(lastParameter)){
					next = request.getParameter("then");				
				}else{
					next = request.getParameter("else");
				}
			
				if(next != null){
					request.executeParam(next);
				}else{
					//if执行完毕
					FunctionRequestUtil.callbakMyselfOk(request, null, actionContext);
				}
			}else if(lastParameter != null){
				//如果最后执行的参数不为空且不是condition，说明已执行then或else那么结束
				FunctionRequestUtil.callbakMyselfOk(request, null, actionContext);
			}else{
				//第一次执行参数取condition，如果condition没有默认为真执行then
				FunctionParameter condition = request.getParameter("condition");
				if(condition != null){
					request.executeParam(condition);
				}else{
					FunctionParameter then = request.getParameter("then");
					if(then != null){
						request.executeParam(then);
					}else{
						FunctionRequestUtil.callbakMyselfOk(request, null, actionContext);
					}
				}
			}
		}
		
		return null;
	}
	
	public static boolean getTrueFalse(FunctionParameter param){
		if(param == null){
			return false;
		}
		
		boolean ok = false;
		Object condition = param.getValue();
		if(condition != null){
			if(condition instanceof Boolean){
				ok = (Boolean) condition;
			}else if(condition instanceof String){
				ok = "true".equals(((String) condition).toLowerCase());
			}else{
				ok = true;
			}
		}else{
			ok = false;
		}
		
		return ok;
	}
	
	
	public static boolean isTrue(Object condition){
		boolean ok = false;
		if(condition != null){
			if(condition instanceof Boolean){
				ok = (Boolean) condition;
			}else if(condition instanceof String){
				ok = "true".equals(((String) condition).toLowerCase());
			}else{
				ok = true;
			}
		}else{
			ok = false;
		}
		
		return ok;
	}
	
	public static Object doReturn(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(actionContext.get("request") == null){
			actionContext.setStatus(ActionContext.RETURN);
			return actionContext.get("value");
		}else{
			FunctionRequestUtil.callbakMyselfOk(request.getRoot(), FunctionStatus.RETURN , actionContext.get("value"), actionContext);
			return null;
		}
	}
	
	public static void doContinue(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(actionContext.get("request") == null){
			actionContext.setStatus(ActionContext.CONTINUE);
		}else{
			FunctionRequestUtil.callbakMyselfOk(request, FunctionStatus.CONTINUE , null, actionContext);
		}
	}
	
	public static void dobreak(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(actionContext.get("request") == null){
			actionContext.setStatus(ActionContext.BREAK);
		}else{
			FunctionRequestUtil.callbakMyselfOk(request, FunctionStatus.BREAK , null, actionContext);
		}
	}
	
	/**
	 * 函数do的实现。
	 * 
	 * @param actionContext
	 */
	public static Object doFunction(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");

		if(request == null){
			Thing self = (Thing) actionContext.get("self");
			Object result = null;
			do{
				result = executeParamQuiet(self, "dosomething", actionContext);
				
				if(ActionContext.CONTINUE == actionContext.getStatus()){
					actionContext.setStatus(ActionContext.RUNNING);
				}else if(ActionContext.BREAK == actionContext.getStatus()){
					actionContext.setStatus(ActionContext.RUNNING);
					return result;
				}else if(ActionContext.RETURN == actionContext.getStatus() || 
		                    ActionContext.CANCEL == actionContext.getStatus() || 
		                    ActionContext.EXCEPTION == actionContext.getStatus()){
					return result;
		         }
			}while(isTrue(executeParamQuiet(self, "while", actionContext)));
			
			return result;
		}else{
			FunctionParameter lastParameter = getLastParameter(actionContext);
			
			if(lastParameter != null){
				if(lastParameter.getName().equals("dosomething")){
					if(request.getStatus() == FunctionStatus.BREAK){
						//while中断，返回到调用者是正常的
						request.setStatus(FunctionStatus.RUNNING);
						FunctionRequestUtil.callbakMyselfOk(request, null, actionContext);			
						return null;
					}else if(request.getStatus() == FunctionStatus.CONTINUE){
						//从while开始重复执行
						request.setStatus(FunctionStatus.RUNNING);
					}else if(request.getStatus() == FunctionStatus.RETURN){
						request.setStatus(FunctionStatus.RUNNING);
						Object value = lastParameter != null ? lastParameter.getValue() : null;
						FunctionRequestUtil.callbakMyselfOk(request, FunctionStatus.RETURN, value, actionContext);
						return null;
					}
					
					//执行while
					FunctionParameter condition = request.getParameter("while");					
					request.executeParam(condition);
				}else if(lastParameter.getName().equals("while")){
					if(isTrue(lastParameter.getValue())){
						//执行something
						FunctionParameter condition = request.getParameter("dosomething");					
						request.executeParam(condition);
					}
				}
			}else{
				//执行something
				FunctionParameter condition = request.getParameter("dosomething");					
				request.executeParam(condition);
			}
			
			return null;
		}
	}
	
	/**
	 * 函数while的实现。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object whileFunction(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");

		if(request == null){
			Thing self = (Thing) actionContext.get("self");
			while(isTrue(executeParamQuiet(self, "while", actionContext))){
				Object result = executeParamQuiet(self, "dosomething", actionContext);
				
				if(ActionContext.CONTINUE == actionContext.getStatus()){
					actionContext.setStatus(ActionContext.RUNNING);
				}else if(ActionContext.BREAK == actionContext.getStatus()){
					actionContext.setStatus(ActionContext.RUNNING);
					return null;
				}else if(ActionContext.RETURN == actionContext.getStatus() || 
		                    ActionContext.CANCEL == actionContext.getStatus() || 
		                    ActionContext.EXCEPTION == actionContext.getStatus()){
					return result;
		         }
			}
			
			return null;
		}else{
			FunctionParameter lastParameter = getLastParameter(actionContext);
						
			if(lastParameter != null){
				//如果有参数已经执行
				if(lastParameter.getName().equals("dosomething")){
					//------------判断while的状态，通常状态是子函数设置的，比如Break,Contine，Return等函数---------
					if(request.getStatus() == FunctionStatus.BREAK){
						//while中断，返回到调用者是正常的
						request.setStatus(FunctionStatus.RUNNING);
						FunctionRequestUtil.callbakMyselfOk(request, null, actionContext);			
						return null;
					}else if(request.getStatus() == FunctionStatus.CONTINUE){
						//从while开始重复执行
						request.setStatus(FunctionStatus.RUNNING);
					}else if(request.getStatus() == FunctionStatus.RETURN){
						request.setStatus(FunctionStatus.RUNNING);
						Object value = lastParameter != null ? lastParameter.getValue() : null;
						FunctionRequestUtil.callbakMyselfOk(request, FunctionStatus.RETURN, value, actionContext);
						return null;
					}
				}else if(lastParameter.getName().equals("while")){
					if(!isTrue(lastParameter.getValue())){
						FunctionRequestUtil.callbakMyselfOk(request, null, actionContext);
					}
					
					//执行then
					FunctionParameter then = request.getParameter("dosomething");
					request.executeParam(then);
				}
			}else{
				//没有参数执行第一次执行conditiion
				FunctionParameter then = request.getParameter("while");
				request.executeParam(then);
			}
						
			return null;
		}
	}
	
	public static void gotoAction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		String path = (String) actionContext.get("path");
		if(request != null){
			FunctionRequest root = request.getRoot();
			if(path == null || "".equals(path)){
				root.run(actionContext);
				return;
			}else{
				String[] ps = path.split("[.]");
				FunctionParameter currentParam = null;
				request = root;
				for(int i=0; i<ps.length; i++){
					String p = ps[i];
					for(FunctionParameter param : request.getParameters()){
						if(param.getName().equals(p)){
							currentParam = param;
							break;
						}
					}
					
					if(currentParam == null){
						throw new ActionException("Function not found by path, path=" + path);
					}
					request = currentParam.getValueRequest();
				}
				
				currentParam.run();
			}
		}else{
			Thing thing = self.getRoot();
			String[] ps = path.split("[.]");
			for(int i=0; i<ps.length; i++){
				String p = ps[i];
				
				for(Thing child : thing.getChilds()){
					if(p.equals(child.getMetadata().getName())){
						thing = child;
						break;
					}
				}
				
				if(thing == null){
					throw new ActionException("Function not found by path, path=" + path + ", thing=" + self.getMetadata().getPath());
				}
			}
			
			FunctionQuietRunner.runFunction(thing, actionContext);
		}
	}
}