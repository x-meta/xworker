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
import org.eclipse.swt.widgets.Link;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class LinkCreator {
    public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		
		if(self.getBoolean("BORDER"))
		    style |= SWT.BORDER;
		    
		Composite parent = (Composite) actionContext.get("parent");
		Link link = new Link(parent, style);
		link.setText(UtilString.getString(self.getString("text"), actionContext));
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), link);
		actionContext.peek().put("parent", link);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		Designer.attach(link, self.getMetadata().getPath(), actionContext);
		return link;        
	}

}