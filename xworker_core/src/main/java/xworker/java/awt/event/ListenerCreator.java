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
package xworker.java.awt.event;

import java.awt.dnd.DropTargetListener;
import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.beans.PropertyChangeListener;

import javax.swing.event.CaretListener;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.MenuListener;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.swing.event.ThingCaretListener;
import xworker.java.swing.event.ThingHyperlinkListener;
import xworker.java.swing.event.ThingInternalFrameListener;
import xworker.java.swing.event.ThingListSelectionListener;
import xworker.java.swing.event.ThingMenuDragMouseListener;
import xworker.java.swing.event.ThingMenuKeyListener;
import xworker.java.swing.event.ThingMenuListener;
import xworker.java.swing.event.ThingTreeExpansionListener;
import xworker.java.swing.event.ThingTreeSelectionListener;
import xworker.java.swing.event.ThingTreeWillExpandListener;

public class ListenerCreator {
	public static TreeWillExpandListener createTreeWillExpandListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		TreeWillExpandListener listener = new ThingTreeWillExpandListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static TreeSelectionListener createTreeSelectionListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		TreeSelectionListener listener = new ThingTreeSelectionListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static TreeExpansionListener createTreeExpansionListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		TreeExpansionListener listener = new ThingTreeExpansionListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static MenuListener createMenuListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		MenuListener listener = new ThingMenuListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static MenuKeyListener createMenuKeyListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		MenuKeyListener listener = new ThingMenuKeyListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static MenuDragMouseListener createMenuDragMouseListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		MenuDragMouseListener listener = new ThingMenuDragMouseListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static ListSelectionListener createListSelectionListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		ListSelectionListener listener = new ThingListSelectionListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static InternalFrameListener createInternalFrameListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		InternalFrameListener listener = new ThingInternalFrameListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static HyperlinkListener createHyperlinkListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		HyperlinkListener listener = new ThingHyperlinkListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static CaretListener createCaretListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		CaretListener listener = new ThingCaretListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static WindowStateListener createWindowStateListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		WindowStateListener listener = new ThingWindowStateListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static WindowListener createWindowListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		WindowListener listener = new ThingWindowListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static WindowFocusListener createWindowFocusListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		WindowFocusListener listener = new ThingWindowFocusListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static PropertyChangeListener createPropertyChangeListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		PropertyChangeListener listener = new ThingPropertyChangeListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static MouseWheelListener createMouseWheelListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		MouseWheelListener listener = new ThingMouseWheelListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static MouseMotionListener createMouseMotionListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		MouseMotionListener listener = new ThingMouseMotionListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static MouseListener createMouseListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		MouseListener listener = new ThingMouseListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static KeyListener createKeyListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		KeyListener listener = new ThingKeyListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static InputMethodListener createInputMethodListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		InputMethodListener listener = new ThingInputMethodListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static HierarchyListener createHierarchyListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		HierarchyListener listener = new ThingHierarchyListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static HierarchyBoundsListener createHierarchyBoundsListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		HierarchyBoundsListener listener = new ThingHierarchyBoundsListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
		
	public static FocusListener createFocusListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		FocusListener listener = new ThingFocusListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static DropTargetListener createDropTargetListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		DropTargetListener listener = new ThingDropTargetListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
	
	public static ComponentListener createComponentListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		ComponentListener listener = new ThingComponentListener(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		return listener;
	}
}