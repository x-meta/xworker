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
package xworker.swt.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class CComboCreator {
    public static Object create(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		if(UtilString.eq(self, "READ_ONLY", "true")) style = SWT.READ_ONLY;
		if(UtilString.eq(self, "BORDER", "true")) style = style | SWT.BORDER;
		if(UtilString.eq(self, "FLAT", "true")) style = style | SWT.FLAT;
		
		Composite parent = (Composite) actionContext.get("parent");
		CCombo combo  = new CCombo(parent, style);
		for(Thing v : self.getChilds("value")){
		    combo.add(UtilString.getString(v.getString("value"), actionContext));
		}
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", combo);
		bindings.put("self", self);
		try{
		    Action initAction = world.getAction("xworker.swt.widgets.Control/@actions/@init");
		    initAction.run(actionContext);
		}finally{
		    actionContext.pop();
		}
		
		int textLimit = self.getInt("textLimit", -1);
		if(textLimit != -1) combo.setTextLimit(textLimit);
		
		int visibleItemCount = self.getInt("visibleItemCount", -1);
		if(visibleItemCount != -1) combo.setVisibleItemCount(visibleItemCount);
		
		combo.setListVisible(self.getBoolean("listVisible"));
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), combo);
		actionContext.peek().put("parent", combo);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		Designer.attach(combo, self.getMetadata().getPath(), actionContext);
		return combo;        
	}

}