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
package xworker.swt.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ResourceCreator {
    public static void create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
		Map<String, Object> res = new HashMap<String, Object>();
		Widget parent = (Widget) actionContext.get("parent");
		parent.setData(self.getString("name"), res);
		for(Thing child : self.getAllChilds()){
		    Object r = child.doAction("create", actionContext);
		    res.put(child.getString("name"), r);
		}
		
		//parent.setData(self.name. res);
		actionContext.getScope(0).put(self.getString("name"), res);        
	}

    public static Object createSystemImage(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Control parent = (Control) actionContext.get("parent");
    	
    	String im = self.getString("image");
    	Image image = null;
    	if("CANCEL".equals(im)){
    	    image = parent.getDisplay().getSystemImage(SWT.ICON_CANCEL);
    	}else if("ERROR".equals(im)){
    	    image = parent.getDisplay().getSystemImage(SWT.ICON_ERROR);
    	}else if("INFORMATION".equals(im)){
    	    image = parent.getDisplay().getSystemImage(SWT.ICON_INFORMATION);
    	}else if("QUESTION".equals(im)){
    	    image = parent.getDisplay().getSystemImage(SWT.ICON_QUESTION);
    	}else if("SEARCH".equals(im)){
    	    image = parent.getDisplay().getSystemImage(SWT.ICON_SEARCH);
    	}else if("WARNING".equals(im)){
    	    image = parent.getDisplay().getSystemImage(SWT.ICON_WARNING);
    	}else if("WORKING".equals(im)){
    	    image = parent.getDisplay().getSystemImage(SWT.ICON_WORKING);
    	}

    	actionContext.getScope(0).put(self.getMetadata().getName(), image);
    	return image;
    }
    
    public static Object createDisplayImage(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	Control parent = (Control) actionContext.get("parent");
    	
    	Display display = parent.getDisplay();
		Rectangle screenREc = display.getBounds();
		
    	int x = (Integer) self.doAction("getX", actionContext);
    	int y = (Integer) self.doAction("getY", actionContext);
    	int width = (Integer) self.doAction("getWidth", actionContext);
    	int height = (Integer) self.doAction("getHeight", actionContext);
    	
    	if(width <= 0 || height <= 0){
    		width = screenREc.width;
    		height = screenREc.width;
    	}
    	
		//截取屏幕    	
		final Image screenImage = new Image(display, width, height);
		GC gc = new GC(display);
		gc.copyArea(screenImage, x, y);
		gc.dispose();
		
		parent.addDisposeListener(new DisposeListener(){
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				screenImage.dispose();	
			}
			
		});
		
		actionContext.g().put(self.getMetadata().getName(), screenImage);		
		return screenImage;
    }
}