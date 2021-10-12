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
package xworker.lang.context;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import xworker.lang.executor.Executor;

public class PerformanceContext {
	private static final String TAG =PerformanceContext.class.getName();
	
	public static void init(ActionContext actionContext){
		Thing context = (Thing) actionContext.get("self");
		
		//脚本实际所在的变量范围
		Bindings bindings = actionContext.getScope(0);
		bindings.put(context.getMetadata().getPath(), System.currentTimeMillis());
	}
	
	public static Object inherit(ActionContext actionContext){
		return null;
	}
	
	public static void success(ActionContext actionContext){
		Thing context = (Thing) actionContext.get("self");
		
		if(Executor.isLogLevelEnabled(TAG, Executor.INFO)){
			Bindings bindings = actionContext.getScope(0);
			
			long end = System.currentTimeMillis();			
			long start = (Long) bindings.get(context.getMetadata().getPath());
		    boolean showSecond = context.getBoolean("showSecond");
		    String label = context.getString("label");
		    StringBuffer info = new StringBuffer();
		    info.append(label);
		    info.append("耗时：");
		    if(showSecond){
		    	info.append((((1.0 + end) - start) / 1000));
		    	info.append("秒");
		    }else{
		    	info.append((end - start));
		    	info.append("毫秒");
		    }
		    Executor.info(TAG, info.toString());
		    System.out.println(info.toString());
		}
	}
	
	public static void exception(ActionContext actionContext){
		success(actionContext);
	}
}