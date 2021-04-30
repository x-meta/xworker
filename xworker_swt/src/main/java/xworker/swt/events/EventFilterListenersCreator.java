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
package xworker.swt.events;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class EventFilterListenersCreator {
	public static final int TYPE_FILTER = 2;
	public static final int TYPE_WIDGET = 1;
	
    public static void create(ActionContext actionContext){
    	createFilterListener(actionContext);
    }
    
    public static void createFilterListener(ActionContext actionContext){
		createListener1(actionContext, TYPE_FILTER);
	}
	
	public static void createListener(ActionContext actionContext){
		createListener1(actionContext, TYPE_WIDGET);
	}
	
	/**
	 * 创建事件，如果事件类型为2，那么是注册到Display的事件。
	 * 
	 * @param actionContext
	 * @param acttionType
	 */
	private static void createListener1(ActionContext actionContext, int acttionType){
		Thing self = (Thing) actionContext.get("self");		

		for(Iterator<Thing> iter = self.getChilds("Listener").iterator(); iter.hasNext();){
			Thing listener = iter.next();
			String sstype = listener.getString("type");
			createAndAddListener(listener, sstype, acttionType, true, actionContext);
		}
	}
	
	public static void createEvent(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String name = self.getStringBlankAsNull("name");
		if(name == null){
			return;
		}else{
			String sstype = name.substring(2, name.length());
			
			createAndAddListener(self, sstype, TYPE_WIDGET, false, actionContext);
		}
	}
	
	public static int getListenerType(String sstype) {
		int type = 0;
	    
	    
	    if("Selection".equals(sstype)){
	    	type = SWT.Selection;
	    }else if("Activate".equals(sstype)){
	    	type = SWT.Activate;
	    }else if("Arm".equals(sstype)){
	    	type = SWT.Arm;
	    }else if("Close".equals(sstype)){
	    	type = SWT.Close;
	    }else if("Collapse".equals(sstype)){
	    	type = SWT.Collapse;
	    }else if("Deactivate".equals(sstype)){
	    	type = SWT.Deactivate;
	    }else if("DefaultSelection".equals(sstype)){
	    	type = SWT.DefaultSelection;
	    }else if("Deiconify".equals(sstype)){
	    	type = SWT.Deiconify;
	    }else if("Dispose".equals(sstype)){
	    	type = SWT.Dispose;
	    }else if("DragDetect".equals(sstype)){
	    	type = SWT.DragDetect;
	    }else if("EraseItem".equals(sstype)){
	    	type = SWT.EraseItem;
	    }else if("Expand".equals(sstype)){
	    	type = SWT.Expand;
	    }else if("FocusIn".equals(sstype)){
	    	type = SWT.FocusIn;
	    }else if("FocusOut".equals(sstype)){
	    	type = SWT.FocusOut;
	    }else if("HardKeyDown".equals(sstype)){
	    	type = SWT.HardKeyDown;
	    }else if("HardKeyUp".equals(sstype)){
	    	type = SWT.HardKeyUp;
	    }else if("Help".equals(sstype)){
	    	type = SWT.Help;
	    }else if("Hide".equals(sstype)){
	    	type = SWT.Hide;
	    }else if("Iconify".equals(sstype)){
	    	type = SWT.Iconify;
	    }else if("KeyDown".equals(sstype)){
	    	type = SWT.KeyDown;
	    }else if("KeyUp".equals(sstype)){
	    	type = SWT.KeyUp;
	    }else if("MeasureItem".equals(sstype)){
	    	type = SWT.MeasureItem;
	    }else if("MenuDetect".equals(sstype)){
	    	type = SWT.MenuDetect;
	    }else if("Modify".equals(sstype)){
	    	type = SWT.Modify;
	    }else if("MouseDoubleClick".equals(sstype)){
	    	type = SWT.MouseDoubleClick;
	    }else if("MouseDown".equals(sstype)){
	    	type = SWT.MouseDown;
	    }else if("MouseEnter".equals(sstype)){
	    	type = SWT.MouseEnter;
	    }else if("MouseExit".equals(sstype)){
	    	type = SWT.MouseExit;
	    }else if("MouseHover".equals(sstype)){
	    	type = SWT.MouseHover;
	    }else if("MouseMove".equals(sstype)){
	    	type = SWT.MouseMove;
	    }else if("MouseUp".equals(sstype)){
	    	type = SWT.MouseUp;
	    }else if("MouseWheel".equals(sstype)){
	    	type = SWT.MouseWheel;
	    }else if("Move".equals(sstype)){
	    	type = SWT.Move;
	    }else if("Paint".equals(sstype)){
	    	type = SWT.Paint;
	    }else if("PaintItem".equals(sstype)){
	    	type = SWT.PaintItem;
	    }else if("Resize".equals(sstype)){
	    	type = SWT.Resize;
	    }else if("SetData".equals(sstype)){
	    	type = SWT.SetData;
	    }else if("Show".equals(sstype)){
	    	type = SWT.Show;
	    }else if("Traverse".equals(sstype)){
	    	type = SWT.Traverse;
	    }else if("Verify".equals(sstype)){
	    	type = SWT.Verify;
	    }
	    
	    return type;
	}
	
	public static void createAndAddListener(Thing listener, String sstype, int acttionType, boolean saveGlobalValue, ActionContext actionContext){
		
		SwtListener lis = new SwtListener(listener, actionContext);
		int type = getListenerType(sstype);

		if (acttionType != TYPE_FILTER) {
			try {
				List<String> bindList = listener.doAction("getBindTo", actionContext);
				if(bindList == null || bindList.size() == 0) {
					//默认绑定到父控件
					Object parentObj = actionContext.get("parent");
					if (parentObj instanceof Widget) {
						Widget parent = (Widget) parentObj;
						parent.addListener(type, lis);
					}
				}else {
					//绑定到指定的控件上
					for(String bindName : bindList) {
						Object parentObj = actionContext.get(bindName);
						if (parentObj instanceof Widget) {
							Widget parent = (Widget) parentObj;
							parent.addListener(type, lis);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				// Log log = (Log) actionContext.get("log");
				// log.error("swt creating listener", e);
			}
		} else {
			Display display = Display.getCurrent();
			if (display != null) {
				display.addFilter(type, lis);

				// 删除过滤当父控件销毁时
				try {
					Object parentObj = actionContext.get("parent");
					if (parentObj instanceof Widget) {
						Widget parent = (Widget) parentObj;
						EventFilterDisposeListener l = new EventFilterDisposeListener();
						l.type = type;
						l.listener = lis;
						l.display = display;

						parent.addListener(SWT.Dispose, l);
					}
				} catch (Exception e) {
					e.printStackTrace();
					// Log log = (Log) binding.getVariable("log");
					// log.error("swt creating filter listener", e);
				}
			}
		}

		// System.out.println(listener.getString("name"));
		if (saveGlobalValue) {
			actionContext.getScope(0).put(listener.getMetadata().getName(), lis);
		}

	}
}