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
package xworker.java.swing.text;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.InputMethodListener;

import javax.swing.DropMode;
import javax.swing.event.CaretListener;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import javax.swing.text.NavigationFilter;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;
import xworker.java.awt.AwtCreator;
import xworker.java.swing.JComponentCreator;

public class JTextComponentCreator {
	public static void createNavigationFilter(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JTextComponent parent = (JTextComponent) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			NavigationFilter l = (NavigationFilter) child.doAction("create", actionContext);
			if(l != null){
				parent.setNavigationFilter(l);
				break;
			}
		}
	}
	
	public static void createHighlighter(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JTextComponent parent = (JTextComponent) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Highlighter l = (Highlighter) child.doAction("create", actionContext);
			if(l != null){
				parent.setHighlighter(l);
				break;
			}
		}
	}
	
	public static void createKeymap(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JTextComponent parent = (JTextComponent) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Keymap l = (Keymap) child.doAction("create", actionContext);
			if(l != null){
				parent.setKeymap(l);
				break;
			}
		}
	}
	public static void createDocument(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JTextComponent parent = (JTextComponent) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Document l = (Document) child.doAction("create", actionContext);
			if(l != null){
				parent.setDocument(l);
				break;
			}
		}
	}
	
	public static void createInputMethodListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JTextComponent parent = (JTextComponent) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			InputMethodListener l = (InputMethodListener) child.doAction("create", actionContext);
			if(l != null){
				parent.addInputMethodListener(l);
			}
		}
	}
		
	public static void createCaretListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JTextComponent parent = (JTextComponent) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			CaretListener l = (CaretListener) child.doAction("create", actionContext);
			if(l != null){
				parent.addCaretListener(l);
			}
		}
	}
	
	public static void init(JTextComponent comp, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(comp, thing, parent, actionContext);
		
		Color caretColor = AwtCreator.createColor(thing, "caretColor", actionContext);
		if(caretColor != null){
			comp.setCaretColor(caretColor);
		}
		
		Integer caretPosition = JavaCreator.createInteger(thing, "caretPosition");
		if(caretPosition != null){
			comp.setCaretPosition(caretPosition);
		}
		
		Color disabledTextColor = AwtCreator.createColor(thing, "disabledTextColor", actionContext);
		if(disabledTextColor != null){
			comp.setDisabledTextColor(disabledTextColor);
		}
		
		Boolean dragEnabled = JavaCreator.createBoolean(thing, "dragEnabled");
		if(dragEnabled != null){
			comp.setDragEnabled(dragEnabled);
		}
		
		DropMode dropMode = AwtCreator.createDropMode(thing, "dropMode", actionContext);
		if(dropMode !=null){
			comp.setDropMode(dropMode);
		}
		
		Boolean editable = JavaCreator.createBoolean(thing, "editable");
		if(editable != null){
			comp.setEditable(editable);
		}
		
		Character focusAccelerator = JavaCreator.createChar(thing, "focusAccelerator");
		if(focusAccelerator != null){
			comp.setFocusAccelerator(focusAccelerator);
		}
		
		Color selectedTextColor = AwtCreator.createColor(thing, "selectedTextColor", actionContext);
		if(selectedTextColor != null){
			comp.setSelectedTextColor(selectedTextColor);
		}
		
		Color selectionColor = AwtCreator.createColor(thing, "selectionColor", actionContext);
		if(selectionColor != null){
			comp.setSelectionColor(selectionColor);
		}
		
		String text = JavaCreator.createText(thing, "text", actionContext);
		if(text != null){
			comp.setText(text);
		}
		
		Integer selectionStart = JavaCreator.createInteger(thing, "selectionStart");
		if(selectionStart != null){
			comp.setSelectionStart(selectionStart);
		}
		
		Integer selectionEnd = JavaCreator.createInteger(thing, "selectionEnd");
		if(selectionEnd != null){
			comp.setSelectionEnd(selectionEnd);
		}
	}
}