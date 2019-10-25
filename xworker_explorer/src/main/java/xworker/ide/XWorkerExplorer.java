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

import org.xmeta.Thing;
import org.xmeta.World;

import xworker.io.SystemIoRedirector;

public class XWorkerExplorer {
	public static void main(String[] args){
		try{
			SystemIoRedirector.init();
			
			//初始化引擎
			World world = World.getInstance();			
			world.init("./xworker/");
			
			//启动编辑器			
			Thing worldExplorer = World.getInstance().getThing("xworker.ide.worldExplorer.swt.SimpleExplorerRunner");		
			worldExplorer.doAction("run");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}