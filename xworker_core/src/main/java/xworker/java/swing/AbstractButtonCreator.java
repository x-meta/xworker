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

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.Icon;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;
import xworker.java.awt.AwtCreator;

public class AbstractButtonCreator {

	public static void init(AbstractButton button, Thing self, Container parent, ActionContext actionContext){
		JComponentCreator.init(button, self, parent, actionContext);
		
		String actionCommand = self.getStringBlankAsNull("actionCommand");
		if(actionCommand != null){
			button.setActionCommand(actionCommand);
		}
		
		Boolean borderPainted = JavaCreator.createBoolean(self, "borderPainted");
		if(borderPainted != null){
			button.setBorderPainted(borderPainted);
		}
		
		Boolean contentAreaFilled = JavaCreator.createBoolean(self, "contentAreaFilled");
		if(contentAreaFilled != null){
			button.setContentAreaFilled(contentAreaFilled);
		}
		
		Icon disabledIcon = SwingCreator.createIcon(self, "disabledIcon", actionContext);
		if(disabledIcon != null){
			button.setDisabledIcon(disabledIcon);
		}
		
		Icon disabledSelectedIcon = SwingCreator.createIcon(self, "disabledSelectedIcon", actionContext);
		if(disabledSelectedIcon != null){
			button.setDisabledSelectedIcon(disabledSelectedIcon);
		}
		
		Integer displayedMnemonicIndex = JavaCreator.createInteger(self, "displayedMnemonicIndex");
		if(displayedMnemonicIndex != null){
			button.setDisplayedMnemonicIndex(displayedMnemonicIndex);
		}
		
		Boolean enabled = JavaCreator.createBoolean(self, "enabled");
		if(enabled != null){
			button.setEnabled(enabled);
		}
		
		Boolean focusPainted = JavaCreator.createBoolean(self, "focusPainted");
		if(focusPainted != null){
			button.setFocusPainted(focusPainted);
		}
		
		Boolean hideActionText = JavaCreator.createBoolean(self, "hideActionText");
		if(hideActionText != null){
			button.setHideActionText(hideActionText);
		}
		
		Integer horizontalAlignment = SwingCreator.createConstants(self, "horizontalAlignment");
		if(horizontalAlignment != null){
			button.setHorizontalAlignment(horizontalAlignment);
		}
		
		Integer horizontalTextPosition = JavaCreator.createInteger(self, "horizontalTextPosition");
		if(horizontalTextPosition != null){
			button.setHorizontalTextPosition(horizontalTextPosition);
		}
		
		Icon icon = SwingCreator.createIcon(self, "icon", actionContext);
		if(icon != null){
			button.setIcon(icon);
		}
		
		Integer iconTextGap = JavaCreator.createInteger(self, "iconTextGap");
		if(iconTextGap != null){
			button.setIconTextGap(iconTextGap);
		}
		
		Integer mnemonic = AwtCreator.createMnemonic(self, "mnemonic");
		if(mnemonic != null){
			button.setMnemonic(mnemonic);
		}
		
		Long multiClickThreshhold = JavaCreator.createLong(self, "multiClickThreshhold");
		if(multiClickThreshhold != null){
			button.setMultiClickThreshhold(multiClickThreshhold);
		}
		
		Icon pressedIcon = SwingCreator.createIcon(self, "pressedIcon", actionContext);
		if(pressedIcon != null){
			button.setPressedIcon(pressedIcon);
		}
		
		Boolean rolloverEnabled = JavaCreator.createBoolean(self, "rolloverEnabled");
		if(rolloverEnabled != null){
			button.setRolloverEnabled(rolloverEnabled);
		}
		
		Icon rolloverIcon = SwingCreator.createIcon(self, "rolloverIcon", actionContext);
		if(rolloverIcon != null){
			button.setRolloverIcon(rolloverIcon);
		}
		
		Icon rolloverSelectedIcon = SwingCreator.createIcon(self, "rolloverSelectedIcon", actionContext);
		if(rolloverSelectedIcon != null){
			button.setRolloverSelectedIcon(rolloverSelectedIcon);
		}
		
		Boolean selected = JavaCreator.createBoolean(self, "selected");
		if(selected != null){
			button.setSelected(selected);
		}
		
		Icon selectedIcon = SwingCreator.createIcon(self, "selectedIcon", actionContext);
		if(selectedIcon != null){
			button.setSelectedIcon(selectedIcon);
		}
		
		String text = JavaCreator.createText(self, "text", actionContext);
		if(text != null){
			button.setText(text);
		}
		
		Integer verticalAlignment = SwingCreator.createConstants(self, "verticalAlignment");
		if(verticalAlignment != null){
			button.setVerticalAlignment(verticalAlignment);
		}
		
		Integer verticalTextPosition = SwingCreator.createConstants(self, "verticalTextPosition");
		if(verticalTextPosition != null){
			button.setVerticalTextPosition(verticalTextPosition);
		}
	}
	
	public static void createActions(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		AbstractButton parent = (AbstractButton) actionContext.get("parent");
		for(Thing child : self.getChilds()){
			Action action = (Action) child.doAction("create", actionContext);
			if(action != null){
				parent.setAction(action);
			}
		}
	}
	
	public static void createButtonModels(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		AbstractButton parent = (AbstractButton) actionContext.get("parent");
		for(Thing child : self.getChilds()){
			ButtonModel model = (ButtonModel) child.doAction("create", actionContext);
			if(model != null){
				parent.setModel(model);
			}
		}
	}
}