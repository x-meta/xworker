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

import org.eclipse.swt.SWT;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleControlSite;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class OleControlSiteCreator {
    public static Object create(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        int style = SWT.NONE;
        if(self.getBoolean("BORDER")){
        	style = style | SWT.BORDER;
        }

        Composite parent = (Composite) actionContext.get("parent");
		OleControlSite client = new OleControlSite(parent, style, self.getString("progId"));
		client.doVerb(OLE.OLEIVERB_INPLACEACTIVATE);
		
		actionContext.getScope(0).put(self.getString("name"), client);
		actionContext.peek().put("parent", client);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		return client;        
	}

}