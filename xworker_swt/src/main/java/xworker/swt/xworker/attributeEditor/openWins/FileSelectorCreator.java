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
package xworker.swt.xworker.attributeEditor.openWins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.util.UtilFile;

public class FileSelectorCreator {
    @SuppressWarnings("unchecked")
	public static Object create(ActionContext actionContext){
    	Shell parent = (Shell) actionContext.get("parent");
        
    	FileDialog dialog = new FileDialog(parent, SWT.OPEN);
        Map<String, String> params = (Map<String, String>) actionContext.get("params");
        String names = params.get("names");
        if(names != null && !"".equals(names)){
            List<String> ns = new ArrayList<String>();
            for(String name : names.split("[,]")){
                ns.add(name);
            }
            dialog.setFilterNames(ns.toArray(new String[ns.size()]));
        }
        String extensions = params.get("extensions");
        if(extensions != null && !"".equals(extensions)){
        	List<String> ns = new ArrayList<String>();
            for(String name : extensions.split("[,]")){
                ns.add(name);
            }
            dialog.setFilterExtensions(ns.toArray(new String[ns.size()]));
        }
        
        Text text = (Text) actionContext.get("text");
        String fileName = UtilFile.getFilePath(text.getText());
        dialog.setFileName(text.getText());
        fileName = dialog.open();
        if(fileName != null){
            text.setText(UtilFile.toXWorkerFilePath(fileName));
        }
        
        return null;
    }

}