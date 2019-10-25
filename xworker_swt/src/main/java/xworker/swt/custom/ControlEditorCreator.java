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
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;
import org.xmeta.util.UtilString;

public class ControlEditorCreator {
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
		//创建editor
    	Composite parent = (Composite) actionContext.get("parent");
    	ControlEditor controlEditor = new ControlEditor(parent);
		self.doAction("init", actionContext, UtilMap.toParams(new Object[]{"editor", controlEditor}));
		
		if(!UtilString.isNull(self, "editor")){
		    Control editor = (Control) actionContext.get(self.getString("editor"));
		    if(editor != null){
		        controlEditor.setEditor(editor);
		    }
		}
		
		for(Thing child : self.getAllChilds()){
		    Control control = (Control) child.doAction("create", actionContext);
		    if(control != null){
		        controlEditor.setEditor(control);
		        break;
		    }
		}
		actionContext.getScope(0).put(self.getString("name"), controlEditor);
		return controlEditor;        
	}

    public static void init(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		ControlEditor editor = (ControlEditor) actionContext.get("editor");
		
		String horizontalAlignment = self.getString("horizontalAlignment");
		if("LEFT".equals(horizontalAlignment)){
			editor.horizontalAlignment = SWT.LEFT;
		}else if("RIGHT".equals(horizontalAlignment)){
			editor.horizontalAlignment = SWT.RIGHT;
		}else if("CENTER".equals(horizontalAlignment)){
			editor.horizontalAlignment = SWT.CENTER;
		}

		editor.grabHorizontal = self.getBoolean("grabHorizontal");
		editor.minimumWidth = self.getInt("minimumWidth");
		String verticalAlignment = self.getString("verticalAlignment");
		if("TOP".equals(verticalAlignment)){
			editor.verticalAlignment = SWT.TOP;
		}else if("BOTTOM".equals(verticalAlignment)){
			editor.verticalAlignment = SWT.BOTTOM;
		}else if("CENTER".equals(verticalAlignment)){
			editor.verticalAlignment = SWT.CENTER;
		}

		editor.grabVertical = self.getBoolean("grabVertical");
		editor.minimumHeight = self.getInt("minimumHeight");        
	}

}