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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import xworker.swt.design.Designer;
import xworker.swt.style.StyleSetStyleCreator;
import xworker.swt.util.SwtUtils;

public class ButtonCreator {
    public static Object create(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
		
		//println binding;
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		String selfType = self.getString("type");
		if("SWT.ARROW".equals(selfType)){
			style |= SWT.ARROW;
		}else if("SWT.CHECK".equals(selfType)){
			style |= SWT.CHECK;
		}else if("SWT.PUSH".equals(selfType)){
			style |= SWT.PUSH;
		}else if("SWT.RADIO".equals(selfType)){
			style |= SWT.RADIO;
		}else if("SWT.TOGGLE".equals(selfType)){
			style |= SWT.TOGGLE;
		}
		String selfStyle = self.getString("style");
		if("UP".equals(selfStyle)){
			style |= SWT.UP;
		}else if("DOWN".equals(selfStyle)){
			style |= SWT.DOWN;
		}else if("LEFT".equals(selfStyle)){
			style |= SWT.LEFT;
		}else if("RIGHT".equals(selfStyle)){
			style |= SWT.RIGHT;
		}		
		
		if(self.getBoolean("border")){
		    style |= SWT.BORDER;
		}
		
		Composite parent = (Composite) actionContext.get("parent");
		Button button = new Button(parent, style);
		
		//初始化本事物所定义的属性
		button.setText(UtilString.getString(self.getString("text"), actionContext));
		if(self.getBoolean("enabled") == false){
		    button.setEnabled(false);
		}
		
		Image image = (Image) StyleSetStyleCreator.createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
        	button.setImage((Image) image);
        }
		
		if(self.getString("selected") != null) button.setSelection(self.getBoolean("selected"));
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", button);
		bindings.put("self", self);
		try{
		    Action initAction = World.getInstance().getAction("xworker.swt.widgets.Control/@actions/@init");
		    initAction.run(actionContext);
		}finally{
		    actionContext.pop();
		}
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), button);
		actionContext.peek().put("parent", button);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		Designer.attach(button, self.getMetadata().getPath(), actionContext);
		return button;        
	}

}