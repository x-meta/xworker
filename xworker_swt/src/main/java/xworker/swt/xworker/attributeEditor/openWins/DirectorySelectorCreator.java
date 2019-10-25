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

import java.util.Map;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.util.UtilFile;

public class DirectorySelectorCreator {
    @SuppressWarnings("unchecked")
	public static Object create(ActionContext actionContext){
        Shell parent = (Shell) actionContext.get("parent");
        
        DirectoryDialog dialog = new DirectoryDialog(parent);
        Map<String, String> params = (Map<String, String>) actionContext.get("params");
        String message = params.get("message");
        if(message != null){
            dialog.setMessage(message);
        }
        
        Text text = (Text) actionContext.get("text");
        dialog.setFilterPath(text.getText());
        String fileName = dialog.open();
        if(fileName != null){
            text.setText(UtilFile.toXWorkerFilePath(fileName));
        }
        
        return null;
    }

}