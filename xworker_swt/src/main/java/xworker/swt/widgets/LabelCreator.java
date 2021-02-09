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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.swt.design.Designer;
import xworker.swt.style.StyleSetStyleCreator;
import xworker.swt.util.SwtUtils;

public class LabelCreator {
    public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		String selfStyle = self.getString("style");
		if("WRAP".equals(selfStyle)){
			style |= SWT.WRAP;
		}else if("SEPARATOR".equals(selfStyle)){
			style |= SWT.SEPARATOR;
		}
		
		String selfType = self.getString("type");
		if("HORIZONTAL".equals(selfType)){
			style |= SWT.HORIZONTAL;
		}else if("VERTICAL".equals(selfType)){
			style |= SWT.VERTICAL;
		}
		
		String shadow = self.getString("shadow");
		if("SHADOW_IN".equals(shadow)){
			style |= SWT.SHADOW_IN;
		}else if("SHADOW_OUT".equals(shadow)){
			style |= SWT.SHADOW_OUT;
		}else if("SHADOW_NONE".equals(shadow)){
			style |= SWT.SHADOW_NONE;
		}		
		    
		if(self.getBoolean("BORDER"))
		    style |= SWT.BORDER;
		    
		String alignment = self.getString("alignment");
		if("LEFT".equals(alignment)){
			style |= SWT.LEFT;
		}else if("CENTER".equals(alignment)){
			style |= SWT.CENTER;
		}else if("RIGHT".equals(alignment)){
			style |= SWT.RIGHT;
		}
		
		Composite parent = (Composite) actionContext.get("parent");    
		Label label = new Label(parent, style);
		
		//父类的初始化方法
		SwtUtils.initControl(self, label, actionContext);
				
		//Object image = actionContext.get(self.getString("image"));
		Image image = (Image) StyleSetStyleCreator.createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
		if(image != null && image instanceof Image)
		    label.setImage((Image) image);
		
		String text = UtilString.getString(self.getString("text"), actionContext);
		if(text != null) {
			label.setText(text);
		}
		String tipText = UtilString.getString(self.getString("toolTipText"), actionContext);
		if(tipText != null) {
			label.setToolTipText(tipText);
		}
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), label);
		actionContext.peek().put("parent", label);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		Designer.attach(label, self.getMetadata().getPath(), actionContext);
		return label;       
	}

}