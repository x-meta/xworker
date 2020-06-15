/*
 * Copyright 2007-2016 The xworker.org.
 *
 * Licensed to the X-Meta under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The X-Meta licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xworker.swt.actions;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.lang.actions.ActionUtils;

public class ShellActions {
    public static void okButtonSelection(ActionContext actionContext){
        Thing thing = actionContext.getObject("thing");
        Text text = actionContext.getObject("text");
        Shell shell = actionContext.getObject("shell");
        ActionContext context = actionContext.getObject("context");
        
        actionContext.getScope(0).put("result", text.getText());
        ActionUtils.executeActionAndChild(thing, "ok", context, "text", text.getText());
        //thing.doAction("ok", context, "text", text.getText());
        shell.dispose();
    }

    public static void cancelButtonSelection(ActionContext actionContext){
    	Thing thing = actionContext.getObject("thing");
        Text text = actionContext.getObject("text");
        Shell shell = actionContext.getObject("shell");
        ActionContext context = actionContext.getObject("context");
        
        ActionUtils.executeActionAndChild(thing, "cancel", context, "text", text.getText());
        //thing.doAction("cancel", context, "text", text.getText());
        shell.dispose();
    }
    
    public static void systemExitOnNoActiveWindow(ActionContext actionContext){
		int activeCount = Thread.currentThread().getThreadGroup().getParent().activeCount();
		Thread[] threads = new Thread[activeCount];
		Thread.currentThread().getThreadGroup().getParent().enumerate(threads);
		
		final Checker checker = new Checker();
		for(Thread thread : threads){
			if(thread != null){
				Display display = Display.findDisplay(thread);
				if(display != null && display != Display.getCurrent()){
					checker.addThread();
					display.asyncExec(new Runnable(){
						public void run(){
							try{
								if(Display.getCurrent().getActiveShell() != null){
									checker.have = true;
								}
							}finally{
								checker.removeThread();
							}
						}
					});
				}
			}
		}

		int count = 0;
		while(checker.getThreadCount() > 0){
			try{
				Thread.sleep(1000);
			}catch(Exception e){				
			}
			
			count ++;
			if(count > 3){
				//如果超出三秒还未判断完毕，直接返回，可能还有界面事物
				return;
			}
		}
				
		if(!checker.have){
			if(Display.getCurrent() == null || Display.getCurrent().getActiveShell() == null){
				System.exit(0);
			}
		}
	}
	
	public static class Checker{
		public boolean have = false;
		int threadCount = 0;
		
		public synchronized void addThread(){
			threadCount++;
		}
		
		public synchronized void removeThread(){
			threadCount--;
		}
		
		public int getThreadCount(){
			return threadCount;
		}
	}

}