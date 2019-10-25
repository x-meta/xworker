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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.swt.util.SwtUtils;

public class FileDialogCreator {
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		String selfModel = self.getString("model");
		if("PRIMARY_MODAL".equals(selfModel)){
			style |= SWT.PRIMARY_MODAL;
		}else if("APPLICATION_MODAL".equals(selfModel)){
			style |= SWT.APPLICATION_MODAL;
		}else if("SYSTEM_MODAL".equals(selfModel)){
			style |= SWT.SYSTEM_MODAL;
		}
		
		String fileStyle = self.getString("fileStyle");
		if("OPEN".equals(fileStyle)){
			style |= SWT.OPEN;
		}else if("SAVE".equals(fileStyle)){
			style |= SWT.SAVE;
		}
		
		if(self.getBoolean("MULTI"))
		    style |= SWT.MULTI;
		    
		Shell parent = (Shell) actionContext.get("parent");
		FileDialog dialog = new FileDialog(parent, style);
		dialog.setText(UtilString.getString(self.getString("title"), actionContext));
		actionContext.getScope(0).put(self.getString("name"), dialog);
		
		dialog.setFileName(UtilString.getString(self.getString("ileName"), actionContext));
		dialog.setFilterNames(UtilString.getStringArray(self.getString("filterNames")));
		dialog.setFilterExtensions(UtilString.getStringArray(self.getString("filterExtensions")));
		return dialog;       
	}

}