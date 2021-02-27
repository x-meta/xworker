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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.lang.executor.Executor;
import xworker.swt.util.ResourceManager;

public class TrayItemCreator {
	private static final String TAG = TrayItemCreator.class.getName();
	
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Display display = null;
    	Object parent = actionContext.get("parent");
    	if(parent instanceof Display){
    		display = (Display) parent;
    	}if(parent instanceof Widget){
    		display = ((Widget) parent).getDisplay();
    	}else{
    		display = Display.getCurrent();
    		if(display == null){
    			display = new Display();
    		}
    	}
    	
    	TrayItem trayItem = new TrayItem(display.getSystemTray(), SWT.NONE);
    	String toolTipText = UtilString.getString(self, "toolTipText", actionContext);
    	if(toolTipText != null){
    		trayItem.setToolTipText(toolTipText);
    	}
    	String text = UtilString.getString(self, "text", actionContext);
    	if(text != null){
    		trayItem.setText(text);
    	}
    	boolean visible = self.getBoolean("visible");
    	trayItem.setVisible(visible);
    	String imageStr = self.getStringBlankAsNull("image");
    	if(imageStr != null){    		
    		Image image = null;
    		try{
    			image = (Image) actionContext.get(imageStr);
    		}catch(Exception e){    			
    		}
    		if(image == null){
    			try{
    				actionContext.push().put("parent", trayItem);
    				image = (Image) ResourceManager.createIamge(imageStr,  actionContext);
    			}catch(Exception e){    		
    				Executor.error(TAG, "create image failed, imageStr=" + imageStr + ",path=" + self.getMetadata().getPath());
    			}finally{
    				actionContext.pop();
    			}
    		}
    		if(image != null){
    			trayItem.setImage(image);
    		}
    	}
    	try{
    		actionContext.push().put("parent", trayItem);
    		for(Thing child : self.getChilds()){
    			child.doAction("create", actionContext);
    		}
    	}finally{
    		actionContext.pop();
    	}
    	
    	actionContext.getScope(0).put(self.getMetadata().getName(), trayItem);
    	return trayItem;
	}
    
    public static void runMenu(ActionContext actionContext){
 		ActionContext context = new ActionContext();
 		context.put("parent", Display.getCurrent().getActiveShell());
 		context.put("explorerActions", actionContext.get("explorerActions"));
 		context.put("explorerContext", actionContext.get("explorerContext"));
 		context.put("parentContext", actionContext);
 		
 		((Thing) actionContext.get("currentThing")).doAction("create", context);    
 	}

     public static void runThreadMenu(ActionContext actionContext){
         Thing self = (Thing) actionContext.get("self");
         Thing currentThing = (Thing) actionContext.get("currentThing");
 		
 		if(currentThing == null){
 		    currentThing = self;
 		}
 		
 		final ActionContext acContext = new ActionContext(actionContext);
 		final Thing cThing = currentThing;
 		new Thread(new Runnable(){
 			public void run(){
 				Display display = new Display ();
 		        ActionContext context = new ActionContext();        
 		        context.put("explorerActions", acContext.get("explorerActions"));
 		        context.put("explorerContext", acContext.get("explorerContext"));
 		        context.put("parentContext", acContext);
 		        context.put("parent", display);
 		        
 		        TrayItem trayItem = (TrayItem) cThing.doAction("create", context);
 		        
 			    while (!trayItem.isDisposed ()) {
 			        if (!display.readAndDispatch ()) display.sleep ();
 			    }
 			    display.dispose ();
 			}
 		}).start(); 
 	}
}