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
package xworker.swt.ole.win32;

import java.io.File;

import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

public class OleClientSiteCreator {
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
		Action styleAction = World.getInstance().getAction("xworker.swt.widgets.Composite/@scripts/@getStyles");
		int style = (Integer) styleAction.run(actionContext);
		
		File f = null;
		String file = self.getString("file");
		if(file != null && file.startsWith("\"") && file.endsWith("\"")){
		    f = new File(file.substring(1, file.length() - 1));
		}else if(file != null){
		    Object afile = actionContext.get(file);
		    if(afile != null && afile instanceof File){
		        f = (File) afile;
		    }else{
		    	f = new File(file);
		    }
		}
		
		OleClientSite client = null;
		Composite parent = (Composite) actionContext.get("parent");
		String progId = self.getString("progId");
		if(f == null && (progId == null || progId.equals(""))){
		    client = new OleClientSite(parent, style, f);
		}else if(f != null && (progId == null || progId.equals(""))){
		    client = new OleClientSite(parent, style, f);
		}else {
		    client = new OleClientSite(parent, style, progId, f);
		}

		client.doVerb(OLE.OLEIVERB_INPLACEACTIVATE);
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", client);
		bindings.put("self", self);
		try{
		    Action initAction = World.getInstance().getAction("xworker.swt.widgets.Control/@actions/@init");
		    initAction.run(actionContext);
		}finally{
		    actionContext.pop();
		}
		
		actionContext.getScope(0).put(self.getString("name"), client);
		actionContext.peek().put("parent", client);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		return client;        
	}

}