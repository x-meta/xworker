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
package xworker.swt.layout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import ognl.OgnlException;
import xworker.swt.util.UtilSwt;

public class FormDataCreator {
    public static Object create(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
    	
		FormData formData = new FormData();
		int width = self.getInt("width", -1);
		if(width != -1)
		    formData.width = UtilSwt.getInt(width);
		int height = self.getInt("height", -1);
		if(height != -1)
		    formData.height = UtilSwt.getInt(height);
		    
		Thing leftObj = (Thing) self.get("left@0");
		if(leftObj != null){
			formData.left = createFormAttachment(leftObj, actionContext);
		}
		
		leftObj = (Thing) self.get("right@0");
		if(leftObj != null){
			formData.right = createFormAttachment(leftObj, actionContext);		    
		}
		
		leftObj = (Thing) self.get("top@0");
		if(leftObj != null){
			formData.top = createFormAttachment(leftObj, actionContext);			    
		}
		
		leftObj = (Thing) self.get("bottom@0");
		if(leftObj != null){
			formData.bottom = createFormAttachment(leftObj, actionContext);			   
		}
		
		Control parent = (Control) actionContext.get("parent");
		if(parent != null){
			parent.setLayoutData(formData);
		}
		    
		actionContext.getScope(0).put(self.getString("name"), formData);
		return formData;       
    }
    
    public static FormAttachment createFormAttachment(Thing self, ActionContext actionContext) throws OgnlException{
    	FormAttachment f = new FormAttachment();
    	int alignment = SWT.DEFAULT;
        String objAlignment = self.getString("alignment");
        if("SWT.LEFT".equals(objAlignment)){
        	alignment = SWT.TOP;
        }else if("SWT.RIGHT".equals(objAlignment)){
        	alignment = SWT.RIGHT;
        }else if("SWT.CENTER".equals(objAlignment)){
        	alignment = SWT.CENTER;
        }else if("SWT.DEFAULT".equals(objAlignment)){
        	alignment = SWT.DEFAULT;
        }
        
        f.alignment = alignment;
        Control control = UtilData.getObjectByType(self, "control", Control.class, actionContext);
        if(control != null){
        	f.control = control;
        }
        
        if(self.getStringBlankAsNull("denominator") != null){
        	f.denominator = self.getInt("denominator");
        }
        if(self.getStringBlankAsNull("numerator") != null){
        	f.numerator = self.getInt("numerator");
        }
        if(self.getStringBlankAsNull("") != null){
        	f.offset = self.getInt("offset");
        }
        
        return f;
    }

}