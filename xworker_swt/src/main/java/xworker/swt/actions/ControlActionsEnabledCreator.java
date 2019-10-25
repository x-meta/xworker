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
package xworker.swt.actions;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class ControlActionsEnabledCreator {
    public static void run(ActionContext actionContext) throws OgnlException{
        Thing self = (Thing) actionContext.get("self");
        
        String method = self.getString("method");
        if(method == null || "".equals(method)){
            method = "enable";
        }
        
        String controlList = self.getString("controlList");
        if(controlList == null || "".equals(controlList)){
            return;
        }
        
        for(String controlName : controlList.split("[,]")){
            Object c = OgnlUtil.getValue(controlName, actionContext);
			if(c instanceof Control){
				final Control control = (Control) c;
				if (control != null && !control.isDisposed()) {
					final String m = method;
					control.getDisplay().asyncExec(new Runnable() {
						public void run() {
							if("enable".equals(m)){
	                            control.setEnabled(true);
	                        }else if("disable".equals(m)){
	                            control.setEnabled(false);
	                        }else if("reverse".equals(m)){
	                            control.setEnabled(!control.getEnabled());
	                        }
						}
					});
				}
			}else if(c instanceof Menu){
				final Menu item = (Menu) c;
				final String m = method;
				item.getDisplay().asyncExec(new Runnable() {
					public void run() {
						if("enable".equals(m)){
							item.setEnabled(true);
                        }else if("disable".equals(m)){
                        	item.setEnabled(false);
                        }else if("reverse".equals(m)){
                        	item.setEnabled(!item.getEnabled());
                        }
					}
				});
			}else if(c instanceof MenuItem){
				final MenuItem item = (MenuItem) c;
				final String m = method;
				item.getDisplay().asyncExec(new Runnable() {
					public void run() {
						if("enable".equals(m)){
							item.setEnabled(true);
                        }else if("disable".equals(m)){
                        	item.setEnabled(false);
                        }else if("reverse".equals(m)){
                        	item.setEnabled(!item.getEnabled());
                        }
					}
				});
			}else if(c instanceof ToolItem){
				final ToolItem item = (ToolItem) c;
				final String m = method;
				item.getDisplay().asyncExec(new Runnable() {
					public void run() {
						if("enable".equals(m)){
							item.setEnabled(true);
                        }else if("disable".equals(m)){
                        	item.setEnabled(false);
                        }else if("reverse".equals(m)){
                        	item.setEnabled(!item.getEnabled());
                        }
					}
				});
			}
        }
    }

}