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
package xworker.swt.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;

import xworker.swt.ActionContainer;
import xworker.swt.util.PoolableControlFactory;

public class LabelToolTipOpenThread extends Thread{
	int x, y;
	Shell parent;
	Control control;
	boolean cancel = false;
	LabelToolTipListener listsner;
	
	public void run(){
		try{
			Thread.sleep(1000);
			
			if(!cancel){
				parent.getDisplay().asyncExec(new Runnable(){
					public void run(){
						if(control == null || control.isDisposed()) {
							return;
						}
						
						ActionContext actionContext = new ActionContext();
						actionContext.put("parent", control.getShell());
						actionContext.put("message", control.getData("toolTip"));		

						Shell toolTipShell = (Shell) PoolableControlFactory.borrowControl(control.getShell(), "xworker.ide.worldexplorer.swt.util.HtmlToolTip/@shell", actionContext);
						if(toolTipShell == null){
							return;
						}
						
						control.setData("toolTipShell", toolTipShell);
						toolTipShell.setData("listsner", listsner);
						Point point = new Point(x, y);
						toolTipShell.setLocation(point);						
						
						ActionContainer actions = (ActionContainer) toolTipShell.getData("actions");
						Map<String, Object> param = new HashMap<String, Object>();
						param.put("message", control.getData("toolTip"));
						actions.doAction("setContent", param);									
						//toolTipShell.setVisible(true);
						//toolTipShell.dispose();
						
						//加载网页后(setContent)后触发ToolTipStatusListener的completed方法
						//ToolTipShell是在UtilBrowserCreator的html_edit_content1方法中打开显示的
					}
				});
			}
		}catch(Exception e){	
			e.printStackTrace();
		}
	}
}