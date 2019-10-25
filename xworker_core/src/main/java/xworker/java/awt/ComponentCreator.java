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

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.java.JavaCreator;

public class ComponentCreator {
	public static void createDropTarget(ActionContext actionContext){
		JComponent parent = (JComponent) actionContext.get("parent");
		
		Thing thing = World.getInstance().getThing("xworker.java.awt.dnd.DropTarget");
		DropTarget obj = (DropTarget) thing.run("create", actionContext);
		
		if(obj != null){
			parent.setDropTarget(obj);
		}
	}
	
	public static void createPropertyChangeListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Component parent = (Component) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			PropertyChangeListener listener = (PropertyChangeListener) child.doAction("create", actionContext);
			if(listener != null){
				parent.addPropertyChangeListener(listener);
			}
		}
	}
	
	public static void createMouseWheelListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Component parent = (Component) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			MouseWheelListener listener = (MouseWheelListener) child.doAction("create", actionContext);
			if(listener != null){
				parent.addMouseWheelListener(listener);
			}
		}
	}
	
	public static void createMouseMotionListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Component parent = (Component) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			MouseMotionListener listener = (MouseMotionListener) child.doAction("create", actionContext);
			if(listener != null){
				parent.addMouseMotionListener(listener);
			}
		}
	}
	
	public static void createMouseListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Component parent = (Component) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			MouseListener listener = (MouseListener) child.doAction("create", actionContext);
			if(listener != null){
				parent.addMouseListener(listener);
			}
		}
	}
	
	public static void createKeyListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Component parent = (Component) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			KeyListener listener = (KeyListener) child.doAction("create", actionContext);
			if(listener != null){
				parent.addKeyListener(listener);
			}
		}
	}
	
	public static void createInputMethodListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Component parent = (Component) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			InputMethodListener listener = (InputMethodListener) child.doAction("create", actionContext);
			if(listener != null){
				parent.addInputMethodListener(listener);
			}
		}
	}
	
	public static void createHierarchyListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Component parent = (Component) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			HierarchyListener listener = (HierarchyListener) child.doAction("create", actionContext);
			if(listener != null){
				parent.addHierarchyListener(listener);
			}
		}
	}
	
	public static void createHierarchyBoundsListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Component parent = (Component) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			HierarchyBoundsListener listener = (HierarchyBoundsListener) child.doAction("create", actionContext);
			if(listener != null){
				parent.addHierarchyBoundsListener(listener);
			}
		}
	}
	
	public static void createFocusListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Component parent = (Component) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			FocusListener listener = (FocusListener) child.doAction("create", actionContext);
			if(listener != null){
				parent.addFocusListener(listener);
			}
		}
	}
	
	public static void createComponentListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Component parent = (Component) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			ComponentListener listener = (ComponentListener) child.doAction("create", actionContext);
			if(listener != null){
				parent.addComponentListener(listener);
			}
		}
	}
	
	public static void init(Component obj, Thing thing, Container parent, ActionContext actionContext){
		obj.setName(thing.getMetadata().getName());
		
		Color background = AwtCreator.createColor(thing, "background", actionContext);
		if(background != null){
			obj.setBackground(background);
		}
		
		ComponentOrientation componentOrientation = AwtCreator.createComponentOrientationConstants(thing, "componentOrientation");
		if(componentOrientation != null){
			obj.setComponentOrientation(componentOrientation);
		}
		
		Cursor cursor = AwtCreator.createCursor(thing, "cursor", actionContext);
		if(cursor != null){
			obj.setCursor(cursor);
		}
		
		Boolean enabled = JavaCreator.createBoolean(thing, "enabled");
		if(enabled != null){
			obj.setEnabled(enabled);
		}
		
		Boolean focusable = JavaCreator.createBoolean(thing, "focusable");
		if(focusable != null){
			obj.setFocusable(focusable);
		}
		
		Boolean focusTraversalKeysEnabled = JavaCreator.createBoolean(thing, "focusTraversalKeysEnabled");
		if(focusTraversalKeysEnabled != null){
			obj.setFocusTraversalKeysEnabled(focusTraversalKeysEnabled);
		}
		
		Font font = AwtCreator.createFont(thing, "font", actionContext);
		if(font != null){
			obj.setFont(font);
		}
		
		Color foreground = AwtCreator.createColor(thing, "foreground", actionContext);
		if(foreground != null){
			obj.setForeground(foreground);
		}
		
		Boolean ignoreRepaint = JavaCreator.createBoolean(thing, "ignoreRepaint");
		if(ignoreRepaint != null){
			obj.setIgnoreRepaint(ignoreRepaint);
		}
		
		Point location = AwtCreator.createPoint(thing, "location", actionContext);
		if(location != null){
			obj.setLocation(location);
		}
		
		Dimension maximumSize = AwtCreator.createDimension(thing, "maximumSize", actionContext);
		if(maximumSize != null){
			obj.setMaximumSize(maximumSize);
		}
		
		Dimension minimumSize = AwtCreator.createDimension(thing, "minimumSize", actionContext);
		if(minimumSize != null){
			obj.setMinimumSize(minimumSize);
		}
		
		Dimension preferredSize = AwtCreator.createDimension(thing, "preferredSize", actionContext);
		if(preferredSize != null){
			obj.setPreferredSize(preferredSize);
		}
		
		Dimension size = AwtCreator.createDimension(thing, "size", actionContext);
		if(size != null){
			obj.setSize(size);
		}
		
		Boolean visible = JavaCreator.createBoolean(thing, "visible");
		if(visible != null){
			obj.setVisible(visible);
		}
	}
}