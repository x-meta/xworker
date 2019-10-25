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
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class TextCreator {
    public static Object create(ActionContext actionContext){
       Thing self = (Thing) actionContext.get("self");
		
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		String selfStyle = self.getString("style");
		if("SINGLE".equals(selfStyle)){
			style |= SWT.SINGLE;
		}else if("MULTI".equals(selfStyle)){
			style |= SWT.MULTI;
		}
				
		if(self.getBoolean("H_SCROLL"))
		    style |= SWT.H_SCROLL;
		if(self.getBoolean("V_SCROLL"))
		    style |= SWT.V_SCROLL;
		if(self.getBoolean("H_SCROLL"))
		    style |= SWT.H_SCROLL;
		if(self.getBoolean("WRAP"))
		    style |= SWT.WRAP;
		if(self.getBoolean("READ_ONLY"))
		    style |= SWT.READ_ONLY;
		if(self.getBoolean("PASSWORD"))
		    style |= SWT.PASSWORD;
		if(self.getBoolean("SEARCH"))
		    style |= SWT.SEARCH;
		if(self.getBoolean("CANCEL"))
		    style |= SWT.CANCEL;
		if(self.getBoolean("BORDER"))
		    style |= SWT.BORDER;
		    
		Composite parent = (Composite) actionContext.get("parent");
		Text text = new Text(parent, style);
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", text);
		try{
			if(SwtUtils.isRWT()) {
				ControlCreator.init(actionContext);
			}else {
				self.doAction("super.init", actionContext);
			}
		}finally{
		    actionContext.pop();
		}
		
		String stext = self.getString("text");
		if(stext != null && !"".equals(stext))
		    text.setText(UtilString.getString(stext, actionContext));
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), text);
		actionContext.peek().put("parent", text);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		Designer.attach(text, self.getMetadata().getPath(), actionContext);
		return text;        
	}

}