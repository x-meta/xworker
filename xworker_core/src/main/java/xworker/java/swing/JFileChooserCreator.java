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
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;
import xworker.java.awt.AwtCreator;

public class JFileChooserCreator {
	public static void createFileFilters(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JFileChooser parent = (JFileChooser) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			FileFilter c = (FileFilter) child.doAction("create", actionContext);
			if(c != null){
				parent.setFileFilter(c);
			}
		}
	}
	
	public static void createActionListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JFileChooser parent = (JFileChooser) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			ActionListener c = (ActionListener) child.doAction("create", actionContext);
			if(c != null){
				parent.addActionListener(c);
			}
		}
	}
	
	public static JFileChooser create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		
		//创建
		JFileChooser comp = new JFileChooser();
		
		//初始化
		init(comp, self, null, actionContext);
		
		//创建子节点
		try{
			actionContext.push().put("parent", comp);
			for(Thing child : self.getChilds()){
				child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
		
		//放置和返回变量
		actionContext.getScope(0).put(self.getMetadata().getName(), comp);
		return comp;
	}
	
	public static void init(JFileChooser comp, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(comp, thing, parent, actionContext);
		
		Boolean acceptAllFileFilterUsed = JavaCreator.createBoolean(thing, "acceptAllFileFilterUsed");
		if(acceptAllFileFilterUsed != null){
			comp.setAcceptAllFileFilterUsed(acceptAllFileFilterUsed);
		}
		
		Integer approveButtonMnemonic = AwtCreator.createMnemonic(thing, "approveButtonMnemonic");
		if(approveButtonMnemonic != null){
			comp.setApproveButtonMnemonic(approveButtonMnemonic);
		}
		
		String approveButtonText = JavaCreator.createText(thing, "approveButtonText", actionContext);
		if(approveButtonText != null){
			comp.setApproveButtonText(approveButtonText);
		}
		
		String approveButtonToolTipText = JavaCreator.createText(thing, "approveButtonToolTipText", actionContext);
		if(approveButtonToolTipText != null){
			comp.setApproveButtonToolTipText(approveButtonToolTipText);
		}
		
		Boolean controlButtonsAreShown = JavaCreator.createBoolean(thing, "controlButtonsAreShown");
		if(controlButtonsAreShown != null){
			comp.setControlButtonsAreShown(controlButtonsAreShown);
		}
		
		File currentDirectory = JavaCreator.createFile(thing, "currentDirectory", actionContext);
		if(currentDirectory != null){
			comp.setCurrentDirectory(currentDirectory);
		}
		
		String dialogTitle = JavaCreator.createText(thing, "dialogTitle", actionContext);
		if(dialogTitle != null){
			comp.setDialogTitle(dialogTitle);
		}
		
		Integer dialogType = null;
		String value = thing.getString("dialogType");
		if("OPEN_DIALOG".equals(value)){
			dialogType = JFileChooser.OPEN_DIALOG;
		}else if("SAVE_DIALOG".equals(value)){
			dialogType = JFileChooser.SAVE_DIALOG;
		}else if("CUSTOM_DIALOG".equals(value)){
			dialogType = JFileChooser.CUSTOM_DIALOG;
		}
		if(dialogType != null){
			comp.setDialogType(dialogType);
		}
		
		Boolean dragEnabled = JavaCreator.createBoolean(thing, "dragEnabled");
		if(dragEnabled != null){
			comp.setDragEnabled(dragEnabled);
		}
		
		Boolean fileHidingEnabled = JavaCreator.createBoolean(thing, "fileHidingEnabled");
		if(fileHidingEnabled != null){
			comp.setFileHidingEnabled(fileHidingEnabled);
		}
		
		Integer fileSelectionMode = null;
		value = thing.getString("fileSelectionMode");
		if("FILES_ONLY".equals(value)){
			fileSelectionMode = JFileChooser.FILES_ONLY;
		}else if("DIRECTORIES_ONLY".equals(value)){
			fileSelectionMode = JFileChooser.DIRECTORIES_ONLY;
		}else if("FILES_AND_DIRECTORIES".equals(value)){
			fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES;
		}
		if(fileSelectionMode != null){
			comp.setFileSelectionMode(fileSelectionMode);
		}
		
		Boolean multiSelectionEnabled = JavaCreator.createBoolean(thing, "multiSelectionEnabled");
		if(multiSelectionEnabled != null){
			comp.setMultiSelectionEnabled(multiSelectionEnabled);
		}
		
		
	}
}