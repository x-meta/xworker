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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.ui.UIHandler;
import xworker.ui.UIRequest;

public class FunctionRequestUtil {
	private static Logger logger = LoggerFactory.getLogger(FunctionRequestUtil.class);
	
	/**
	 * 根据值的类型创建一个常量函数。
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public static Thing getValueThing(String name, Object value){
		Thing valueTh = null;
		if(value instanceof String){
			valueTh = new Thing("xworker.lang.function.values.StringValue");
			valueTh.put("value", value);
		}else if(value instanceof Integer){
			valueTh = new Thing("xworker.lang.function.values.IntValue");
			valueTh.put("value", String.valueOf(value));
		}else if(value instanceof Long){
			valueTh = new Thing("xworker.lang.function.values.LongValue");
			valueTh.put("value", String.valueOf(value));
		} else if(value instanceof Character){
			valueTh = new Thing("xworker.lang.function.values.CharValue");
			valueTh.put("value", String.valueOf(value));
		} else if(value instanceof Float){
			valueTh = new Thing("xworker.lang.function.values.FloatValue");
			valueTh.put("value", String.valueOf(value));
		} else if(value instanceof Byte){
			valueTh = new Thing("xworker.lang.function.values.ByteValue");
			valueTh.put("value", String.valueOf(value));
		} else if(value instanceof BigInteger){
			valueTh = new Thing("xworker.lang.function.values.BigIntegerValue");
			valueTh.put("value", String.valueOf(value));
		} else if(value instanceof BigDecimal){
			valueTh = new Thing("xworker.lang.function.values.BigDecimalValue");
			valueTh.put("value", String.valueOf(value));
		} else if(value instanceof Double){
			valueTh = new Thing("xworker.lang.function.values.DoubleValue");
			valueTh.put("value", String.valueOf(value));
		} else if(value instanceof Thing){
			valueTh = new Thing("xworker.lang.function.xworker.GetThing");
			Thing pathThing = new Thing("xworker.lang.function.values.StringValue");
			pathThing.put("name", "thingPath");
			pathThing.put("value", ((Thing) value).getMetadata().getPath());
			valueTh.addChild(pathThing);			
		} else if(value instanceof Boolean){
			valueTh = new Thing("xworker.lang.function.values.BooleanValue");
			valueTh.put("value", String.valueOf(value));
		}else {
			//valueTh = new Thing("xworker.lang.function.values.NullValue");
			//valueTh.put("value", String.valueOf(value));
			return null;
		}  
		
		valueTh.put("name", name);
		return valueTh;
	}
		
	
	/**
	 * 获取在UI请求显示请求的文档。
	 * 
	 * @param message
	 * @return
	 */
	public static Thing getUIDescriptionThing(Object message){
		if(message instanceof FunctionCallback){
			FunctionCallback callback = (FunctionCallback) message;
			if(callback.getParam() != null){
				return callback.getParam().getDescriptionThing();
			}else{
				return callback.getRequest().getDescriptionThing();
			}
		}else if(message instanceof FunctionRequest){
			return ((FunctionRequest) message).getDescriptionThing();
		}else if(message instanceof UIRequest){
			return ((UIRequest) message).getThing();
		}else{
			return World.getInstance().getThing("xworker.ui.function.swt.HelpDocs/@NoDescription");
		}
	}
	
	/**
	 * 初始化函数请求的文档。除了在browserUIHandler上显示当前文档，还会在outline显示函数和参数的文档。
	 * 
	 * @param functionRequest
	 * @param browserUIHandler
	 * @param actionContext
	 */
	public static void initFunctionRequestHtml(FunctionRequest functionRequest, UIHandler browserUIHandler, ActionContext actionContext){
		return;
		/*
		FunctionCallback callback = functionRequest.getCallback();
		FunctionParameter param = callback != null ? callback.getParam() : null;
		
		//当前的文档
		if(browserUIHandler != null){
			Thing thing = param != null ? param.getDescriptionThing() : functionRequest.getThing();
			browserUIHandler.requestUI(new UIRequest(thing, actionContext), actionContext);
		}
		
	    Thing requestThing = functionRequest.getThing();
	    if(requestThing != null){
	        UIManager.requestUI("xworker_session_function_swt_outline", requestThing, actionContext);
	    }else{
	        UIManager.requestUI("xworker_session_function_swt_outline", World.getInstance().getThing("xworker.ui.function.swt.FunctionHelpDescs/@functioNotSet"), actionContext);
	    }

		
		if(param != null){
			FunctionRequestUIFactory.requestUI(param.getRequest(), "xworker_session_function_param_swt_outline", param.getDescriptor(), actionContext);
			    
		    //设置按钮的状态
		    if(param.getValueThing() == null){
		    	FunctionRequestUIFactory.requestUI(param.getRequest(), "xworker_session_function_function_swt_outline", World.getInstance().getThing("xworker.ui.function.xworker.OutlineMessage/@paramFunctioNotSeted"), actionContext);
		    }else{
		    	FunctionRequestUIFactory.requestUI(param.getRequest(), "xworker_session_function_function_swt_outline", param.getValueRequest().getDescriptor(), actionContext);
		    }
		}else{
			if(callback != null){
				FunctionRequestUIFactory.requestUI(callback.getRequest(), "xworker_session_swt_outline", functionRequest.getDescriptor(), actionContext);
			}else{
				FunctionRequestUIFactory.requestUI(functionRequest, "xworker_session_swt_outline", functionRequest.getDescriptor(), actionContext);
			}
		}
	       */
	}
	
	public static void sendEditFunctionRequest(FunctionRequest functionRequest, ActionContext actionContext){
		//构造一个编辑函数的请求事物，该事物的run方法会在结束时重新在对话上显示函数
		Thing thing = new Thing("xworker.ui.function.xworker.EditFunctionRequest");
		thing.put("request", functionRequest);
		
		//UI请求
		UIRequest uiRequest = new UIRequest(thing, actionContext);
		uiRequest.setRequestMessage(functionRequest.getThing());
		
		FunctionRequestUIFactory.requestUI(functionRequest, "xworker_session_swt_dialogThingEditor", uiRequest, actionContext);
	}
		
	/**
	 * 函数自己执行回调。
	 * 
	 * @param functionRequest
	 * @param value
	 * @param actionContext
	 */
	public static void callbakMyselfOk(FunctionRequest functionRequest, Object value, ActionContext actionContext){
		FunctionCallback callback = functionRequest.getCallback();
		if(callback != null){
			callback.ok(value, actionContext);
		}else{
			FunctionManager.finishRequest(functionRequest, value);
		}
	}
	
	/**
	 * 函数自己执行回调。
	 * 
	 * @param functionRequest
	 * @param value
	 * @param actionContext
	 */
	public static void callbakMyselfOk(FunctionRequest functionRequest, FunctionStatus status, Object value, ActionContext actionContext){
		FunctionCallback callback = functionRequest.getCallback();
		if(callback != null){
			callback.ok(value, status, actionContext);
		}else{
			FunctionManager.finishRequest(functionRequest, value);
		}
	}
	
	/**
	 * 函数自己执行回调取消。
	 * 
	 * @param functionRequest
	 * @param value
	 * @param actionContext
	 */
	public static void callbakMyselfCancel(FunctionRequest functionRequest, Object value, ActionContext actionContext){
		FunctionCallback callback = functionRequest.getCallback();
		if(callback != null){
			callback.cancel(actionContext);
		}else{
			FunctionManager.finishRequest(functionRequest, value);
		}
	}
	
	/**
	 * 函数自己执行回调取消。
	 * 
	 * @param functionRequest
	 * @param value
	 * @param actionContext
	 */
	public static void callbakMyselfCancel(FunctionRequest functionRequest, FunctionStatus status, Object value, ActionContext actionContext){
		FunctionCallback callback = functionRequest.getCallback();
		if(callback != null){
			callback.cancel(status, actionContext);
		}else{
			FunctionManager.finishRequest(functionRequest, value);
		}
	}
	
	/**
	 * 使用新的函数实例替代当前正在实行的函数，采用调用函数实例的方法。
	 * 
	 * @param functionRequest
	 * @param functionInstance
	 * @param actionContext
	 */
	public static void replaceFunctionRequestWithIntance(FunctionRequest functionRequest, Thing functionInstance, ActionContext actionContext){
		Thing valueThing = new Thing("xworker.ui.function.common.CallFunction");            
        valueThing.put("name", functionRequest.getThing().getMetadata().getName());
        Thing functionPath = new Thing("xworker.lang.function.values.StringValue");
        functionPath.put("name", "functionPath");
        functionPath.put("value", functionInstance.getMetadata().getPath());
        valueThing.addChild(functionPath);        
        
        replaceFunctionRequest(functionRequest, valueThing, actionContext);
	}
	
	/**
	 * 使用新的函数原型替代当前正在实行的函数。
	 * 
	 * @param functionRequest
	 * @param functionPrototype
	 * @param actionContext
	 */
	public static void replaceFunctionRequestWithPrototype(FunctionRequest functionRequest, Thing functionPrototype, ActionContext actionContext){
		Thing valueThing = new Thing(functionPrototype.getMetadata().getPath());
        valueThing.set("name", functionRequest.getThing().getMetadata().getName());
        
        replaceFunctionRequest(functionRequest, valueThing, actionContext);
	}
		
	/**
	 * 使用新的函数替代当前正在实行的函数。
	 * 
	 * @param functionRequest
	 * @param newFunctionThing
	 */
	public static void replaceFunctionRequest(FunctionRequest functionRequest, Thing newFunctionThing, ActionContext actionContext){
		FunctionCallback functionCallback = functionRequest.getCallback();
		if(functionCallback != null && functionCallback.getRequest() != null){
			FunctionRequest parentRequest = functionCallback.getRequest();
			FunctionParameter parameter = functionCallback.getParam();
			parameter.setValueThing(newFunctionThing, null);
			parentRequest.setFocusedParam(parameter);
			FunctionParameter unReadyParam = parameter.getValueRequest().getFirstUnReadyParam();
			if(unReadyParam != null){
				//如果新的参数的函数还有参数需要设置，那么设置参数
				FunctionManager.sendParameterRequest(unReadyParam, actionContext);
			}else{
				//先刷新函数树
				FunctionManager.sendRequest(parentRequest, actionContext);
				//再执行新的函数
				parameter.run();
			}
		}else{
			logger.info("replaceFunctionRequest cann't set root function request");
		}
	}
}