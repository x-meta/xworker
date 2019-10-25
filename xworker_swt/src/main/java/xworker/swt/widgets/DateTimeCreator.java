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
package xworker.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;

public class DateTimeCreator {
    public static Object create(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
		
		Action styleAction = World.getInstance().getAction("xworker.swt.widgets.Composite/@scripts/@getStyles");
		int style = (Integer) styleAction.run(actionContext);
		
		String selfStyle = self.getString("style");
		if("DATE".equals(selfStyle)){
			style |= SWT.DATE;
		}else if("TIME".equals(selfStyle)){
			style |= SWT.TIME;
		}else if("CALENDAR".equals(selfStyle)){
			style |= SWT.CALENDAR;
		}		
		
		String dateStyle = self.getString("dateStyle");
		if("SHORT".equals(dateStyle)){
			style |= SWT.SHORT;
		}else if("MEDIUM".equals(dateStyle)){
			style |= SWT.MEDIUM;
		}else if("LONG".equals(dateStyle)){
			style |= SWT.LONG;
		}
		    
		Composite parent = (Composite) actionContext.get("parent");
		DateTime dateTime = new DateTime(parent, style);
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", dateTime);
		bindings.put("self", self);
		try{
		    Action initAction = World.getInstance().getAction("xworker.swt.widgets.Control/@actions/@init");
		    initAction.run(actionContext);
		}finally{
		    actionContext.pop();
		}
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), dateTime);
		actionContext.peek().put("parent", dateTime);
		try{
			for(Thing child : self.getAllChilds()){
			    child.doAction("create", actionContext);
			}
		}finally{
			actionContext.peek().remove("parent");
		}
		
		Designer.attach(dateTime, self.getMetadata().getPath(), actionContext);
		return dateTime;        
	}

}