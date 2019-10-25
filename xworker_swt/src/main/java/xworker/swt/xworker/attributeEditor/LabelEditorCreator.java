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

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilString;

import xworker.swt.util.ResourceManager;
import xworker.swt.xworker.AttributeEditor;
public class LabelEditorCreator {
    public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//创建标签
		int style = SWT.NONE;
		
		//Thing attribute = (Thing) actionContext.get("attribute");
		Map<String, String> params = null;
		String inputattrs = self.getString("inputattrs");
		if(inputattrs != null && !"".equals(inputattrs)){
		    params = UtilString.getParams(inputattrs, ",");
		    
		    String pstyle = params.get("style");
		    if("WRAP".equals(pstyle)){
		    	style |= SWT.WRAP;
		    }else if("SEPARATOR".equals(pstyle)){
		    	style |= SWT.SEPARATOR;
		    }
		   
		    String ptype = params.get("type");
		    if("HORIZONTAL".equals(ptype)){
		    	style |= SWT.HORIZONTAL;
		    }else if("VERTICAL".equals(ptype)){
		    	style |= SWT.VERTICAL;
		    }
		    
		    String shadow = params.get("shadow");
		    if("SHADOW_IN".equals(shadow)){
		    	style |= SWT.SHADOW_IN;
		    }else if("SHADOW_OUT".equals(shadow)){
		    	style |= SWT.SHADOW_OUT;
		    }else if("SHADOW_NONE".equals(shadow)){
		    	style |= SWT.SHADOW_NONE;
		    }		    
		        
		    if("true".equals(params.get("BORDER")))
		        style |= SWT.BORDER;
		        
		    String alignment = params.get("alignment");
		    if("LEFT".equals(alignment)){
		    	style |= SWT.LEFT;
		    }else if("CENTER".equals(alignment)){
		    	style |= SWT.CENTER;
		    }else if("RIGHT".equals(alignment)){
		    	style |= SWT.RIGHT;
		    }		  
		}
		
		Composite parent = (Composite) actionContext.get("parent");
		Label label = new Label(parent, style);
		
		String fontstr = params != null ? params.get("font") : null;
		if(fontstr != null && !"".equals(fontstr)){
		    //字体
		    Font font = ResourceManager.createFont(label, fontstr, actionContext);
		    if(font != null){
		        label.setFont(font);
		    }
		}
		
		//设置布局数据
		label.setLayoutData((GridData) actionContext.get("layoutData"));
		
		//创建并返回ActionContainer
		ActionContext ac = new ActionContext();
		ac.put("control", label);		
		Thing actionThing = World.getInstance().getThing("xworker.swt.xworker.attributeEditor.LabelEditor/@actions1");		
		ActionContainer actionContainer =  actionThing.doAction("create", ac);
		
		label.setData(AttributeEditor.ACTIONCONTAINER, actionContainer);
		
		return label;
    } 
    
    public static void setValue(ActionContext actionContext){
    	Label control = (Label) actionContext.get("control");
    	Object value = actionContext.get("value");
    	control.setData(value);
    	if(value != null){
    	    control.setText(value.toString());
    	}else{
    	    control.setText("");
    	}
    }
    
    public static Object getValue(ActionContext actionContext){
    	Label control = (Label) actionContext.get("control");
    	return control.getData();
    }
}