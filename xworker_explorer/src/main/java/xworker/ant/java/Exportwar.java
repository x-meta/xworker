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
package xworker.ant.java;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.XMetaException;

import xworker.ant.AntUtils;

public class Exportwar {
	/**
	 * ExportWar任务的转化为ant的方法。
	 * 
	 */
	public static void toString(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String warDirPath = self.getString("warDir");		
		String warFilePath = self.getString("warFile");
		if(warDirPath == null || "".equals(warDirPath)){
			throw new XMetaException("Java ant: please set warDir, " + self);
			
		}
		if(warDirPath.endsWith("/") || warDirPath.endsWith("\\")){			
		}else{
			warDirPath = warDirPath + "/";
		}
				
		List<Thing> tasks = new ArrayList<Thing>();
		
		//删除war临时目录
		if(self.getBoolean("clean")){
			tasks.add(AntUtils.createDeleteFileTask(warDirPath, true));
		}
		
		//创建war临时目录
		tasks.add(AntUtils.createMkdirTask(warDirPath));		
		
		//拷贝额外定义的根目录文件
		AntUtils.createCopyTask(self, tasks, "RootFiles", warDirPath);
		//拷贝额外的WEB-INFO目录文件
		AntUtils.createCopyTask(self, tasks, "WEBINF", warDirPath + "WEB-INF/");
		//拷贝额外定义的classes文件
		AntUtils.createCopyTask(self, tasks, "Classes", warDirPath + "WEB-INF/classes/");
		//拷贝额外的lib文件
		AntUtils.createCopyTask(self, tasks, "Lib", warDirPath + "WEB-INF/lib/");
		
		//自定义的其他任务
		Thing otherTask = self.getThing("OtherTasks@0");
		if(otherTask != null){
			for(Thing child : otherTask.getChilds()){
				tasks.add(child);
			}
		}
		
		//生成 WAR文件
		if(warFilePath != null && !"".equals(warFilePath)){
			tasks.add(AntUtils.createJarTask(warDirPath, warFilePath));
		}
		
		//生成xml		
		for(Thing task : tasks){
			task.doAction("toString", actionContext);			
		}		
	}
}