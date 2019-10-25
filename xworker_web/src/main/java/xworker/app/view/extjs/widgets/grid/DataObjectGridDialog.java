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
package xworker.app.view.extjs.widgets.grid;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

/**
 * 数据对象表格弹出对话框的相关动作集合。
 * 
 * @author Administrator
 *
 */
public class DataObjectGridDialog {
	public static void newDialogHttpDo(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		Thing window = createWindow(actionContext);
		
		if(window != null){		
			window.doAction("httpDo", actionContext);
		}else{
			HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
	        response.setContentType("text/plain; charset=utf-8");
	        response.getWriter().println("alert('DataObject not setted, thing=" + self.getMetadata().getPath() + "')");
	        return;
		}
	}
	
	public static Object toJavaScript(ActionContext actionContext){
		Thing window = createWindow(actionContext);
		if(window != null){		
			return window.doAction("toJavaScriptCode", actionContext);
		}else{
			return null;
		}
	}
	
	public static Thing createWindow(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//Window远程控件是通过模板生成的
		Thing window = (Thing) self.getData("windowTemp");
		Long lastWindowTime = (Long) self.getData("lastWindowTime");
		
		//如果window没有生成或者self已变更，重新生成
		if(window == null || lastWindowTime != self.getMetadata().getLastModified()){
			Object dataObject = self.doAction("getDataObject", actionContext);
			if(dataObject == null){				
		        return null;
			}
			
			actionContext.peek().put("dataObject", dataObject);
			actionContext.peek().put("data", self);
			
			Thing template = World.getInstance().getThing("xworker.app.view.extjs.widgets.grid.DataObjectGridNewDialog/@ThingTemplate");			
			window = ((Thing) template.doAction("process", actionContext)).getChilds().get(0);
			
			//使用自己的路径，这样临时生成的js有固定目录，比较产生垃圾文件
			window.getMetadata().setPath(self.getMetadata().getPath());
			self.setData("windowTemp", window);
			self.setData("lastWindowTime", self.getMetadata().getLastModified());
		}
		
		return window;
	}
}