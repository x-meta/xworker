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
import org.eclipse.swt.widgets.Tree;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class TreeCreator {
    public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		String selectStyle = self.getString("selectStyle");
		if("SINGLE".equals(selectStyle)){
			style |= SWT.SINGLE;
		}else if("MULTI".equals(selectStyle)){
			style |= SWT.MULTI;
		}		
		
		if(self.getBoolean("V_SCROLL"))
		    style |= SWT.V_SCROLL;
		if(self.getBoolean("H_SCROLL"))
		    style |= SWT.H_SCROLL;
		if(self.getBoolean("CHECK"))
		    style |= SWT.CHECK;
		if(self.getBoolean("FULL_SELECTION"))
		    style |= SWT.FULL_SELECTION;
		if(self.getBoolean("HIDE_SELECTION"))
		    style |= SWT.HIDE_SELECTION;
		if(self.getBoolean("BORDER"))
		    style |= SWT.BORDER;
		    
		Composite parent = (Composite) actionContext.get("parent");
		Tree tree = new Tree(parent, style);
		
		if(self.getBoolean("lineVisible"))
		    tree.setLinesVisible(true);
		
		if(self.getBoolean("headerVisible"))
		    tree.setHeaderVisible(true);    
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), tree);
		actionContext.peek().put("parent", tree);
		for(Thing child : self.getChilds()){
		    child.doAction("create", actionContext);
		}
		if(self.getBoolean("checkSelection")){
		    Thing checkSelectionThing = World.getInstance().getThing("xworker.swt.xworker.TreeStyles/@listeners");
		    checkSelectionThing.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		Action action = World.getInstance().getAction("xworker.swt.widgets.Control/@actions/@init");
		actionContext.peek().put("control", tree);
		action.run(actionContext);  
		
		Designer.attach(tree, self.getMetadata().getPath(), actionContext);
		return tree;       
	}
}