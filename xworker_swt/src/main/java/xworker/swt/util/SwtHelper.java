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
package xworker.swt.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

public class SwtHelper {
	public static DisposeListener swtContextDisposer = new DisposeListener(){
	@SuppressWarnings("unchecked")
	public void widgetDisposed(DisposeEvent e) {
		try{
			String name = (String) e.widget.getData("swtContextName");
			Map<String, ActionContext> context = (Map<String, ActionContext>) e.widget.getData("swtContext");
			
			if(name != null && context != null){
				ActionContext actionContext = context.remove(name);
				if(actionContext != null){
					for(Iterator iter = actionContext.keySet().iterator(); iter.hasNext();){
						String key = (String) iter.next();
						Object obj = actionContext.get(key);
						if(obj instanceof Widget){
							((Widget) obj).dispose();
						}
					}
				}
			}
		}catch(Exception ee){
			ee.printStackTrace();
		}
	}		
};

public static Color focusColor = new Color(null, 232, 250, 255);

public static FocusListener focusListener = new FocusListener(){
	public void focusGained(FocusEvent event) {
		Control control = (Control) event.widget;
		if(control.getData("_focusBakColor") == null){
			control.setData("_focusBakColor", control.getBackground());
		}
		
		//设置背景色
		control.setBackground(focusColor);
	}

	public void focusLost(FocusEvent event){
		Control control = (Control) event.widget;
		if(control.getData("_focusBakColor") != null){
			control.setBackground((Color) control.getData("_focusBakColor"));
			control.setData("_focusBakColor", null);
		}
	}
	
};

/**
 * 通过数据对象创建一个SWT实例。<p>
 * 
 * SWT实例创建后会使用给定的名称保存变量范围到swt上下文中，swt上下文在变量范围里使用
 * swtContext保存。
 * @param name
 * @param parent
 * @param swtThing
 */
public static Object create(String name, Map<String, ActionContext> swtContext, Widget parent, Thing swtThing){
	ActionContext context = new ActionContext();
	context.put("parent", parent);
	if(swtContext == null){
		swtContext = new HashMap<String, ActionContext>();			
	}
	swtContext.put(name, context);
	context.put("swtContext", swtContext);
	
	Object obj = swtThing.doAction("create", context);
	if(obj != null && obj instanceof Widget){
		Widget created = (Widget) obj;
		created.setData("swtContextName", name);
		created.setData("swtContext", name);
		created.addDisposeListener(swtContextDisposer);
	}
	
	return obj;
}

/**
 * 从数据对象创建swt组件。
 * 
 * @param swtComponent 需要创建swt的数据对象
 * @param parentName 父节点的名称
 * @param parentContext 父节点的binding上下文
 *  
 */
public static Object create(Thing swtComponent, String parentName, ActionContext parentContext){
	//设置自己
	ActionContext context = new ActionContext();
	context.put("world", World.getInstance());
	context.put("parentContext", parentContext);
	
	try{
		Object parent = OgnlUtil.getValue(parentName, parentContext);
		context.put("parent", parent);
	}catch(Exception e){
		e.printStackTrace();
	}
	
    swtComponent.doAction("create", context);		
	return context;
	//script.
}
}