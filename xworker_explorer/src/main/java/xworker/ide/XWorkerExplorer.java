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

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectList;
import xworker.dataObject.DataObjectListListener;
import xworker.io.SystemIoRedirector;
import xworker.lang.executor.DefaultRequestService;

import java.io.File;
import java.util.Collection;

public class XWorkerExplorer {
	org.eclipse.swt.widgets.ToolItem systemMessageItem;
	public XWorkerExplorer(ActionContext actionContext){
		systemMessageItem = actionContext.getObject("systemMessageItem");

	}

	public void setSystemMessageItemLabel(){
		systemMessageItem.getParent().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				int count = DefaultRequestService.getInstance().getUnreadCount();

				World world = World.getInstance();
				Thing  labelThing = world.getThing("xworker.ide.worldexplorer.swt.i18n.I18nResource/@SystemInfo/@systemMessageItem");

				String label = labelThing.getMetadata().getLabel();//UtilString.getString("lang:d=系统消息&en=System Notification", actionContext);
				if(count > 0){
					systemMessageItem.setText(label + "（" + count + "）");
				}else{
					systemMessageItem.setText(label);
				}

			}
		});

	}

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

			File dir = new File("..");
			for(File child : dir.listFiles()){
				File src = new File(child, "/src/main/resources");
				if(src.exists() && world.getThingManager(child.getName()) == null){
					world.addFileThingManager(child.getName(), src, false, false);
				}
			}

			//启动编辑器			
			Thing worldExplorer = World.getInstance().getThing("xworker.ide.worldexplorer.swt.SimpleExplorerRunner");		
			worldExplorer.doAction("run");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}