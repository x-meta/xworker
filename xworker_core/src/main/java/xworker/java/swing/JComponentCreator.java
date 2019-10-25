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
package xworker.java.swing;

import java.awt.Container;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;
import javax.swing.border.Border;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.java.JavaCreator;
import xworker.java.awt.ContainerCreator;

public class JComponentCreator {
	public static void createTransferHandler(ActionContext actionContext){
		JComponent parent = (JComponent) actionContext.get("parent");
		
		Thing thing = World.getInstance().getThing("xworker.javax.swing.TransferHandler");
		TransferHandler obj = (TransferHandler) thing.run("create", actionContext);
		
		if(obj != null){
			parent.setTransferHandler(obj);
		}
	}
	
	public static void createInputVerifier(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JComponent parent = (JComponent) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			InputVerifier inputVerifier = (InputVerifier) child.doAction("create", actionContext);
			if(inputVerifier != null){
				parent.setInputVerifier(inputVerifier);
				break;
			}
		}
	}
	
	public static void createComponentPopupMenu(ActionContext actionContext){
		JComponent parent = (JComponent) actionContext.get("parent");
		
		Thing jpopMenuThing = World.getInstance().getThing("xworker.javax.swing.JPopupMenu");
		JPopupMenu jpopMenu = (JPopupMenu) jpopMenuThing.run("create", actionContext);
		
		if(jpopMenu != null){
			parent.setComponentPopupMenu(jpopMenu);
		}
		
	}
	
	public static void createBorder(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JComponent parent = (JComponent) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Border border = (Border) child.doAction("create", actionContext);
			if(border != null){
				parent.setBorder(border);
				break;
			}
		}
	}
	
	public static void init(JComponent obj, Thing thing, Container parent, ActionContext actionContext){
		ContainerCreator.init(obj, thing, parent, actionContext);
				
		Float alignmentX = JavaCreator.createFloat(thing, "alignmentX");
		if(alignmentX != null){
			obj.setAlignmentX(alignmentX);
		}
		
		Float alignmentY = JavaCreator.createFloat(thing, "alignmentY");
		if(alignmentY != null){
			obj.setAlignmentY(alignmentY);
		}
		
		Boolean autoscrolls = JavaCreator.createBoolean(thing, "autoscrolls");
		if(autoscrolls != null){
			obj.setAutoscrolls(autoscrolls);
		}
		
		Integer debugOptions = SwingCreator.createDebugGraphicsConstants(thing, "debugOptions");
		if(debugOptions != null){
			obj.setDebugGraphicsOptions(debugOptions);
		}
		
		Boolean doubleBuffered = JavaCreator.createBoolean(thing, "doubleBuffered");
		if(doubleBuffered != null){
			obj.setDoubleBuffered(doubleBuffered);
		}
		
		Boolean enabled = JavaCreator.createBoolean(thing, "enabled");
		if(enabled != null){
			obj.setEnabled(enabled);
		}
		
		/*
		Font font = AwtCreator.createFont(thing, "font", actionContext);
		if(font != null){
			obj.setFont(font);
		}
		
		Color foreground = AwtCreator.createColor(thing, "foreground", actionContext);
		if(foreground != null){
			obj.setForeground(foreground);
		}*/
		
		Boolean  inheritsPopupMenu = JavaCreator.createBoolean(thing, "inheritsPopupMenu");
		if(inheritsPopupMenu != null){
			obj.setInheritsPopupMenu(inheritsPopupMenu);
		}
		
		String toolTipText = JavaCreator.createText(thing, "toolTipText", actionContext);
		if(toolTipText != null){
			obj.setToolTipText(toolTipText);
		}
		
		Boolean verifyInputWhenFocusTarget = JavaCreator.createBoolean(thing, "verifyInputWhenFocusTarget");
		if(verifyInputWhenFocusTarget != null){
			obj.setVerifyInputWhenFocusTarget(verifyInputWhenFocusTarget);
		}
		
		/*
		Boolean visible = JavaCreator.createBoolean(thing, "visible");
		if(visible != null){
			obj.setVisible(visible);
		}*/
	}
}