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
package xworker.java.awt;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;

public class WindowCreator {
	public static void createWindowStateListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Window parent = (Window) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			WindowStateListener l = (WindowStateListener) child.doAction("create", actionContext);
			if(l != null){
				parent.addWindowStateListener(l);
			}
		}
	}
	
	public static void createWindowListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Window parent = (Window) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			WindowListener l = (WindowListener) child.doAction("create", actionContext);
			if(l != null){
				parent.addWindowListener(l);
			}
		}
	}
	
	public static void createWindowFocusListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Window parent = (Window) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			WindowFocusListener l = (WindowFocusListener) child.doAction("create", actionContext);
			if(l != null){
				parent.addWindowFocusListener(l);
			}
		}
	}
	
	public static void createPropertyChangeListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Window parent = (Window) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			PropertyChangeListener l = (PropertyChangeListener) child.doAction("create", actionContext);
			if(l != null){
				parent.addPropertyChangeListener(l);
			}
		}
	}
	
	public static void init(Window obj, Thing thing, Container parent, ActionContext actionContext) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		ContainerCreator.init(obj, thing, parent, actionContext);
		
		Boolean alwaysOnTop = JavaCreator.createBoolean(thing, "alwaysOnTop");
		if(alwaysOnTop != null){
			obj.setAlwaysOnTop(alwaysOnTop);
		}
		
		Boolean autoRequestFocus = JavaCreator.createBoolean(thing, "autoRequestFocus");
		if(autoRequestFocus != null){
			//jdk1.7的功能
			try{
				Method method = Window.class.getMethod("setAutoRequestFocus", new Class[]{Boolean.class});
				if(method != null){
					method.invoke(obj, new Object[]{autoRequestFocus});
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		Boolean focusableWindowState = JavaCreator.createBoolean(thing, "focusableWindowState");
		if(focusableWindowState != null){
			obj.setFocusableWindowState(focusableWindowState);
		}
		
		Boolean focusCycleRoot = JavaCreator.createBoolean(thing, "focusCycleRoot");
		if(focusCycleRoot != null){
			obj.setFocusCycleRoot(focusCycleRoot);
		}
		
		Image iconImage = AwtCreator.createImage(thing, "iconImage", actionContext);
		if(iconImage != null){
			obj.setIconImage(iconImage);
		}
		
		Point location = AwtCreator.createPoint(thing, "location", actionContext);
		if(location != null){
			obj.setLocation(location);
		}
		
		Boolean locationByPlatform = JavaCreator.createBoolean(thing, "locationByPlatform");
		if(locationByPlatform != null){
			obj.setLocationByPlatform(locationByPlatform);
		}
		
		String locationRelativeTo = thing.getStringBlankAsNull("locationRelativeTo");
		if(locationRelativeTo != null){
			Component c = (Component) actionContext.get(locationRelativeTo);
			if(c != null){
				obj.setLocationRelativeTo(c);
			}
		}
		
		Dimension minimumSize = AwtCreator.createDimension(thing, "minimumSize", actionContext);
		if(minimumSize != null){
			obj.setMinimumSize(minimumSize);
		}
		
		Dialog.ModalExclusionType modalExclusionType = null;
		String value = thing.getStringBlankAsNull("modalExclusionType");
		if("APPLICATION_EXCLUDE".equals(value)){
			modalExclusionType = Dialog.ModalExclusionType.APPLICATION_EXCLUDE;
		}else if("NO_EXCLUDE".equals(value)){
			modalExclusionType = Dialog.ModalExclusionType.NO_EXCLUDE;
		}else if("TOOLKIT_EXCLUDE".equals(value)){
			modalExclusionType = Dialog.ModalExclusionType.TOOLKIT_EXCLUDE;
		}
		if(modalExclusionType != null){
			obj.setModalExclusionType(modalExclusionType);
		}
		
		Float opacity = JavaCreator.createFloat(thing, "opacity");
		if(opacity != null){
			//jdk1.7的功能
			Method method = Window.class.getMethod("setOpacity", new Class[]{Float.class});
			if(method != null){
				method.invoke(obj, new Object[]{opacity});
			}
		}
		
		Dimension size = AwtCreator.createDimension(thing, "size", actionContext);
		if(size != null){
			obj.setSize(size);
		}
		
		//Window.Type type = null;
		
		Boolean visible = JavaCreator.createBoolean(thing, "visible");
		if(visible != null){
			obj.setVisible(visible);
		}
	}
}