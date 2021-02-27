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
package xworker.lang.actions;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;

public class ThingCallCreator {
	private static final String TAG = ThingCallCreator.class.getName();
	
	@SuppressWarnings("unchecked")
	public static Object run(ActionContext actionContext) throws Exception{
		//获取数据对象调用的本身
		Thing self = (Thing) actionContext.get("self");
		
		Object result = "success";
		//获取需要调用的数据对象
		List<Thing> dataObjectForCall = null;
		Thing dataObjectCollector = self.getThing("DataObjectCollector@0");		
		if(dataObjectCollector != null){
			dataObjectForCall = (List<Thing>) dataObjectCollector.doAction("run", actionContext);
		}		
		if(dataObjectCollector == null || dataObjectForCall.size() == 0){
            //没有数据对象可以调用
			return result;
		}
		
		//获取要执行的方法
		String methodNames = self.getString("methodName");
		String[] methods = null;
		if(methodNames == null || "".equals(methodNames)){
			methods = new String[]{"run"};
		}else{
			methods = methodNames.split("[,]");
		}
	
		//执行数据对象的方法
		String runType = self.getString("type");
		String dataName = self.getString("dataName");
		
		Random random = new Random(System.currentTimeMillis());
		if(runType != null && runType.startsWith(ActionContext.RUNTYPE_RANDOM)){
			Collections.shuffle(dataObjectForCall, random);
		}
		
		int runCount = random.nextInt(dataObjectForCall.size());				
		int count = 0;
		while(count < dataObjectForCall.size()){
			Exception aexception = null;
			boolean successed = false;
			try{
				Thing data = (Thing) dataObjectForCall.get(count);
				//UtilData.changeValueTemplate(data, actionContext, true);
				
				result = data.doAction(methods[0], actionContext);
				if(dataName != null){
					actionContext.put(dataName + "_" + data.getMetadata().getName(), result);
				}
				
				if(result instanceof String){
					if("success".equals(result)){
						successed = true;
					}
				}else{
					successed = true;
				}
			}catch(Exception ee){
				aexception = ee;
			}
						
			if(successed){
				if((ActionContext.RUNTYPE_SUCCESS.equals(runType) || ActionContext.RUNTYPE_RANDOM_SUCCESS.equals(runType))){
					break;
				}
			}else{
				if((ActionContext.RUNTYPE_SUCCESS.equals(runType) || ActionContext.RUNTYPE_RANDOM_SUCCESS.equals(runType))){
					if(aexception != null){
						Executor.error(TAG, "run dataobject method", aexception);
					}
				}else if(aexception != null){
					throw aexception;
				}
			}
			if(ActionContext.RUNTYPE_RANDOM_ONE.equals(runType)){
				break;
			}
			if(ActionContext.RUNTYPE_RANDOM_RANDOM.equals(runType) && runCount == count){
				break;
			}
			count ++;
			runCount ++;
		}				
		
		if(dataName != null){
			actionContext.put(dataName, result);
		}	
		return result;
	}

}