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
package xworker.java.lang;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;
import xworker.lang.system.IProcessManager;
import xworker.service.ServiceManager;
import xworker.util.UtilData;

public class RuntimeActionsActions {
    public static void run(ActionContext actionContext) throws IOException{
    	Thing self = actionContext.getObject("self");
    	
    	String fileName = (String) self.doAction("getFileName", actionContext);
    	File file = new File(fileName);
    	if(file.exists()){
    		Desktop.getDesktop().open(new File(fileName));
    	}else{
	    	URI uri = URI.create(fileName);
	    	Desktop.getDesktop().browse(uri);
    	}
    	
    	
        //return Program.launch(self.getString("fileName", "", actionContext));
    }

    public static String getCurrentJar(ActionContext actionContext) {
    	URL jarUrl = RuntimeActionsActions.class.getProtectionDomain().getCodeSource().getLocation();
		String fileName = jarUrl.getFile();
		//System.out.println(fileName);
		int index = fileName.indexOf("!");
		if(index != -1) {
			fileName = fileName.substring(0, index);
		}
		if(fileName.startsWith("file:")) {
			fileName = fileName.substring(5, fileName.length());
		}
						
		//System.out.println(fileName);
		return fileName;
    }
    
    /**
     * 启动一个进程。
     * 
     * @param actionContext
     * @return
     * @throws IOException 
     * @throws InterruptedException 
     */
    public static Object exec(ActionContext actionContext) throws IOException, InterruptedException{
    	Thing self = actionContext.getObject("self");
    	
    	String[] cmdarray = null;
    	Object obj = self.doAction("getCmdarray", actionContext);
    	if(obj instanceof String[]) {
    		cmdarray = (String[]) obj;
    	}else {
	    	String cmd = (String) obj; 
	    	if(cmd == null){
	    		throw new ActionException("cmdarray is null, action=" + self.getMetadata().getPath());
	    	}
	    	
	    	cmdarray = cmd.split("[\n]");
	    	for(int i=0; i<cmdarray.length; i++){
	    		cmdarray[i] = cmdarray[i].trim();
	    	}
    	}
    	
    	String envpStr = (String) self.doAction("getEnvp", actionContext);
    	String[] envp = null;
    	if(envpStr != null && !"".equals(envpStr.trim())){
    		envp = envpStr.split("[\n]");
    		for(int i=0; i<envp.length; i++){
    			envp[i] = envp[i].trim();
        	}
    	}
    	
    	File dir = (File) self.doAction("getDir", actionContext);
    	
    	Process process = Runtime.getRuntime().exec(cmdarray, envp, dir);
    	if(UtilData.isTrue(self.doAction("isProcessManager", actionContext))) {
    		//是否添加到进程管理器中
    		IProcessManager manager = ServiceManager.getService(IProcessManager.class);
    		if(manager != null && !manager.isDisposed()) {
    			String title = self.doAction("getTitle", actionContext);
    			if(title == null || "".equals(title)) {
    				title = self.getMetadata().getLabel();
    			}
    			manager.addProcess(title, process);
    		}
    	}
    	
    	ProcessExecThread pt = new ProcessExecThread(process, self, actionContext);
    	pt.start();
    	if((Boolean) self.doAction("isSync", actionContext)){
			pt.join();
			
    		return pt.buffer.toString();
    	}else{
    		return pt;
    	}
    }
    
    public static Process startProcess(ActionContext actionContext) throws IOException{
    	Thing self = actionContext.getObject("self");
    	
    	String[] cmdarray = self.doAction("getCmdarray", actionContext);
    	if(cmdarray == null){
    		throw new ActionException("cmdarray is null, action=" + self.getMetadata().getPath());
    	}
    	
    	for(int i=0; i<cmdarray.length; i++){
    		cmdarray[i] = cmdarray[i].trim();
    	}

    	String[] envp = self.doAction("getEnvp", actionContext);
    	if(envp != null){
    		for(int i=0; i<envp.length; i++){
    			envp[i] = envp[i].trim();
        	}
    	}
    	
    	File dir = (File) self.doAction("getDir", actionContext);
    	
    	Process process = Runtime.getRuntime().exec(cmdarray, envp, dir);
    	if(UtilData.isTrue(self.doAction("isProcessManager", actionContext))) {
    		//是否添加到进程管理器中
    		IProcessManager manager = ServiceManager.getService(IProcessManager.class);
    		if(manager != null && !manager.isDisposed()) {
    			manager.addProcess(self.getMetadata().getLabel(), process);
    		}
    	}
    	
    	return process;
    }
    
    public static void onOut(ActionContext actionContext) {
    	byte[] bytes = actionContext.getObject("bytes");
    	int length = actionContext.getObject("length");
    	
    	Executor.print(new String(bytes, 0, length));
    }
    
    public static void onErrOut(ActionContext actionContext) {
    	byte[] bytes = actionContext.getObject("bytes");
    	int length = actionContext.getObject("length");
    	
    	Executor.errPrint(new String(bytes, 0, length));
    }
}