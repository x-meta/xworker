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
package xworker.ide;

import java.lang.reflect.Method;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class TestPerformance {
	//模型要调用的Java方法
	public static int inc(ActionContext actionContext){
		Integer x = actionContext.getObject("x");
		
		return x + 1;
	}
	
	//Java直接执行
	public static int inc(int x){
		return x + 1;
	}
	
	public static void main(String args[]){
		try{
			World world = World.getInstance();
			world.init("./xworker/");
			
			int count = 10000000;
			long start = System.currentTimeMillis();
			int value = 1;
	        for(int i=0; i<count; i++){
	        	value = inc(value);
	        }
	        System.out.println("java method execute time : " + (System.currentTimeMillis() - start) + ", count=" + count  + ", value=" + value);

	        Method method = TestPerformance.class.getMethod("inc", int.class);
	        start = System.currentTimeMillis();
			value = 1;
	        for(int i=0; i<count; i++){
	            value = (Integer) method.invoke(null, value);
	        }
	        System.out.println("Invoke method time : " + (System.currentTimeMillis() - start) + ", count=" + count  + ", value=" + value);
	        
	        ActionContext actionContext = new ActionContext();
			Action javaAction = world.getAction("xworker.example.core.performance.TestPerformance/@actions/@javaAdd");
			start = System.currentTimeMillis();
			value = 1;
	        for(int i=0; i<count; i++){
	            value = javaAction.run(actionContext, "x", value);
	        }
	        System.out.println("java action execute time : " + (System.currentTimeMillis() - start) + ", count=" + count  + ", value=" + value);   
	        
	        ActionContext thingContext = new ActionContext();
			Thing thing = world.getThing("xworker.example.core.performance.TestPerformance");
			start = System.currentTimeMillis();
			value = 1;
	        for(int i=0; i<count; i++){
	            value = thing.doAction("javaInc", thingContext, "x", value);
	        }
	        System.out.println("Thing do action execute time : " + (System.currentTimeMillis() - start) + ", count=" + count  + ", value=" + value);   

	        
	        Action groovyAction = world.getAction("xworker.example.core.performance.TestPerformance/@actions/@groovyAdd");
	        start = System.currentTimeMillis();
	        value = 1;
	        for(int i=0; i<count; i++){
	        	value = groovyAction.run(actionContext, "x", value);
	        }
	        System.out.println("groovy action execute time : " + (System.currentTimeMillis() - start) + ", count=" + count + ", value=" + value);	        
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}