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
package xworker.swt.xworker.attributeEditor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.ActionContainer;
import xworker.swt.util.PoolableControlFactory;
import xworker.swt.util.UtilSwt;

public class ImageEditorCreator {
	public static void editButtonAction(ActionContext actionContext){
		Combo colorText = (Combo) actionContext.get("colorText");

		Shell shell =  (Shell) PoolableControlFactory.borrowControl(colorText.getShell(), 
							"xworker.swt.widgets.ColorDialog", actionContext);
					
		//shell.setLocation(text.getLocation().x, text.getLocation().y);
		ActionContainer ac = (ActionContainer) shell.getData("actions");			
		ColorDialog dialog = (ColorDialog) ac.doAction("getColorDialog");
		//dialog.setl
		int[] rgb = UtilSwt.parseRGB(colorText.getText());
		if(rgb != null){
			dialog.setRGB(new RGB(rgb[0], rgb[1], rgb[2]));
		}
		//dialog.
		RGB colorRgb = dialog.open();
		if(colorRgb != null){
			String rgbStr = UtilSwt.RGBToString(colorRgb);
			colorText.setText("\"" + rgbStr + "\"");
			//UtilSwt.setBackground(text, text.getText(), null);
		}
					
		colorText.setFocus();
	}
	
	@SuppressWarnings("unchecked")
	public static void okButonAction(ActionContext actionContext){
		Thing model = (Thing) actionContext.get("model");
		
		if(!(Boolean) model.doAction("validate", actionContext)) return;

		Map<String, Object> data = (HashMap<String, Object>) model.doAction("getValue", actionContext);

		Shell shell = (Shell) actionContext.get("shell");
		Text pointText = (Text) actionContext.get("pointText");
		shell.dispose();
		if(data.get("x") != null && data.get("y") != null){
		    pointText.setText("" + data.get("x") + "," + data.get("y"));
		    pointText.setFocus();
		}
	}
	
	public static void cancelButtonAction(ActionContext actionContext){
		Shell shell = (Shell) actionContext.get("shell");
		Text pointText = (Text) actionContext.get("pointText");
	
		shell.dispose();
		pointText.setFocus();
	}
}