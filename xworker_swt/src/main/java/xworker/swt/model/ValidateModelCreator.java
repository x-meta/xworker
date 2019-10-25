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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ValidateModelCreator {
    public static boolean validate(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
		if(!(Boolean) self.doAction("doValidate", actionContext)){        
		    Shell tempShell = new Shell();
		    MessageBox box = new MessageBox(tempShell, SWT.OK);
		    box.setText("输入提示：");
		    String errorMessage = self.getString("errorMessage");
		    if(errorMessage != null){
		        if(errorMessage.startsWith("\"")){
		            String mes = errorMessage.substring(1, errorMessage.length() - 1);
		            box.setMessage(mes);
		        }else{
		            box.setMessage((String) actionContext.get(errorMessage));
		        }
		    }else{
		        box.setMessage("数据校验失败，请输入正确的数据！");
		    }
		      
		    box.open();
		    tempShell.dispose();
		
		    return false;
		}else{
		   return true;
		}        
	}

    public static boolean doValidate(ActionContext actionContext){
    	return true;       
	}

}