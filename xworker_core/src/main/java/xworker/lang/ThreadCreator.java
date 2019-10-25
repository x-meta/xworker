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
package xworker.lang;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class ThreadCreator {
    public static Object start(final ActionContext actionContext){
    	final Thing self = (Thing) actionContext.get("self");
        
        Runnable runnable = new Runnable(){
        	public void run(){
        		self.doAction("run", actionContext);
        	}
        };
        
        String name = self.getString("name");
        Thread thread = null;
        if(name == null || "".equals(name)){
            thread = new Thread(runnable);   
        }else{
            thread = new Thread(runnable, name);
        }
        thread.start();
        return thread;
    }

    public static Object start1(final ActionContext actionContext){
    	World world = World.getInstance();
    	
        Thing actionThing = null;
        String actionMethod = (String) actionContext.get("method");
        Runnable runnable = null;
        final Thing listener = (Thing) actionContext.get("listener");
        
        Object thing = actionContext.get("action");
        if(thing == null){
            return null;
        }else if(thing instanceof String){
            actionThing = world.getThing((String) thing);
        }else if(thing instanceof Thing){
            actionThing = (Thing) thing;
        }else if(thing instanceof Runnable){
            runnable = (Runnable) thing;
        }else{
            actionThing = (Thing) thing;
        }
        
        if(actionMethod == null || "".equals(actionMethod)){
            actionMethod = "run";
        }
        
        final Thing acThing = actionThing;
        final String acMethod = actionMethod;
        //doasync
        final ActionContext ac = new ActionContext(actionContext);
        if(runnable == null){
        	runnable = new Runnable(){
        		
        		public void run(){
        			try{
                        if(listener != null){
                            listener.doAction("onThreadStart", ac);
                        }
                        acThing.doAction(acMethod, ac);
                        if(listener != null){
                            listener.doAction("onThreadSuccess", ac);
                        }
                    }catch(Throwable e){
                        //log.error("run thread error", e);
                        if(listener != null){
                            ac.peek().put("exception", e);
                            listener.doAction("onThreadException", ac);
                        }
                    }finally{
                        if(listener != null){
                            listener.doAction("onThreadEnd", ac);
                        }
                    }
        		}
        	};

        }
        
        Thread thread = null;
        if(actionContext.get("name") == null){
            thread = new Thread(runnable);
        }else{
            thread = new Thread(runnable, (String) actionContext.get("name"));
        }
        thread.start();
        return thread;
    }

}