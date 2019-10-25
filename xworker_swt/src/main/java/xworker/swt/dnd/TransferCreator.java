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
package xworker.swt.dnd;

import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.dnd.URLTransfer;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

public class TransferCreator {
    public static Object create(final ActionContext actionContext){
        final Thing self = (Thing) actionContext.get("self");
        String selfType = (String) self.doAction("getType", actionContext);
        if("File".equals(selfType)){
        	return FileTransfer.getInstance();
        }else if("HTML".equals(selfType)){
        	return HTMLTransfer.getInstance();
        }else if("Image".equals(selfType)){
        	return ImageTransfer.getInstance();
        }else if("RTF".equals(selfType)){
        	return RTFTransfer.getInstance();
        }else if("Text".equals(selfType)){
        	return TextTransfer.getInstance();
        }else if("URL".equals(selfType)){
        	return URLTransfer.getInstance();
        }else if("Thing".equals(selfType)){
        	return ThingTransfer.getInstance();
        }else{
        	return new Transfer(){
        		Thing thing = self;
    			ActionContext ac = actionContext;
    			
				@Override
				public TransferData[] getSupportedTypes() {
                    return (TransferData[]) thing.doAction("getSupportedTypes", ac);
				}

				@Override
				protected int[] getTypeIds() {
	                return (int[]) thing.doAction("getTypeIds", ac);	                
				}

				@Override
				protected String[] getTypeNames() {
                    return (String[]) thing.doAction("getTypeNames", ac);
				}

				@Override
				public boolean isSupportedType(TransferData arg0) {
					Bindings bindings = ac.push(null);
	                try{
	                    bindings.put("data", arg0);
	                    return (Boolean) thing.doAction("isSupportedType", ac);
	                }finally{
	                    ac.pop();
	                }
				}

				@Override
				protected void javaToNative(Object arg0, TransferData arg1) {
					Bindings bindings = ac.push(null);
	                try{
	                    bindings.put("object", arg0);
	                    bindings.put("data", arg1);
	                    thing.doAction("javaToNative", ac);
	                }finally{
	                    ac.pop();
	                }					
				}

				@Override
				protected Object nativeToJava(TransferData arg0) {
					Bindings bindings = ac.push(null);
	                try{
	                    bindings.put("data", arg0);
	                    return thing.doAction("nativeToJava", ac);
	                }finally{
	                    ac.pop();
	                }
				}
        	};
        }     
	}
}