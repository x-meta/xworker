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

import java.io.File;

import org.xmeta.Thing;
import org.xmeta.World;

import xworker.io.SystemIoRedirector;

public class XWorkerExplorer {
	public static void main(String[] args){
		try{
			//首次从git上拉下来没有databases目录，会报数据库的错误
			File dbDir = new File("./xworker/databases/");
			if(dbDir.exists() == false) {
				dbDir.mkdirs();
			}

			//初始化引擎，如果代码着色有问题，请设置color的dll的路径
			World world = World.getInstance();
			world.init("./xworker/");
			Thread.currentThread().setContextClassLoader(world.getClassLoader());
			SystemIoRedirector.init();
			
			System.out.println(world.getThingManager("_local"));
			//启动编辑器			
			Thing worldExplorer = World.getInstance().getThing("xworker.ide.worldexplorer.swt.SimpleExplorerRunner");		
			worldExplorer.doAction("run");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}