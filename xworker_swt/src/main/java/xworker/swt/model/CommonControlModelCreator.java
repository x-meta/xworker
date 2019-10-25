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
package xworker.swt.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.util.UtilModel;
import xworker.swt.util.UtilSwt;

public class CommonControlModelCreator {
    public static void create(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        
		//初始化
		Control swtControl = (Control) actionContext.get(self.get("swtControl"));
		if(swtControl != null && swtControl instanceof Control){
		    //添加事件监听
		    UtilModel.addFocusListener(swtControl, self.getString("focusColor"), actionContext);
		    
		    //控件颜色
		    UtilSwt.setBackground(swtControl, self.getString("background"), actionContext);
		    UtilSwt.setForeground(swtControl, self.getString("foreground"), actionContext);
		    if(self.getBoolean("required")){
		        UtilSwt.setBackground(swtControl, self.getString("requiredColor"), actionContext);
		    }
		
		    //设置默认事件
		    Listener defaultListener = (Listener) actionContext.get(self.get("defaultSelection"));
		    if(defaultListener == null){
		        //查找父Model定义的默认事件
		        defaultListener = (Listener) actionContext.get("_parentModelDefaultSelection");
		    }
		    if(defaultListener != null){
		        swtControl.addListener(SWT.DefaultSelection, defaultListener);
		    }
		    Listener defaultModifiyListener = (Listener) actionContext.get(self.get("defaultModify"));
		    if(defaultModifiyListener == null){
		        defaultModifiyListener = (Listener) actionContext.get("_parentModelDefaultModify");
		    }
		    if(defaultModifiyListener != null){
		        swtControl.addListener(SWT.Modify, defaultModifiyListener);
		    }
		    
		    //设置焦点
		    if(self.getBoolean("focus")){
		        swtControl.setFocus();
		    }
		}        
	}

}