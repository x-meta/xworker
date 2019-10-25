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
package xworker.swt.actions;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilAction;
import org.xmeta.util.UtilString;

import xworker.swt.util.SwtDialog;
import xworker.swt.util.SwtUtils;

public class FileActions {	
	
	/**
	 * 选择文件当做字符串。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object selectFileAsString(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		if(SwtUtils.isRWT()) {
			Shell shell = (Shell) createFileDialog(actionContext);
			shell.open();
			return null;
		}else {
			//打开文件对话框
			FileDialog fileDialog = (FileDialog) createFileDialog(actionContext);
			String file = fileDialog.open();
			
			//保存变量
			String varName = self.getStringBlankAsNull("varName");
			UtilAction.putVarByActioScope(self, varName, file, actionContext);
			
			return file;
		}
	}
	
	/**
	 * 创建文件对话框。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object createFileDialog(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//样式
		int style = SWT.NONE;
		if("SAVE".equals(self.getString("style"))){
			style = style | SWT.SAVE;
		}else{
			style = style | SWT.OPEN;
		}
		if(self.getBoolean("MULTI")){
			style = style | SWT.MULTI;
		}
		
		//创建FileDialog
		Shell parent = null;
		String parentShell = self.getStringBlankAsNull("parentShell");
		if(parentShell != null){
			parent = (Shell) OgnlUtil.getValue(parentShell, actionContext);
		}else{
			parent = (Shell) actionContext.get("parent");
		}
		
		if(SwtUtils.isRWT()) {
			ActionContext ac = new ActionContext();
			ac.put("parent", parent);
			ac.put("parentContext", actionContext);
			ac.put("thing", self);
			Thing shellThing = World.getInstance().getThing("xworker.swt.actions.prototypes.OpenFileDialog");
			Shell shell = shellThing.doAction("create", ac);
			if((style & SWT.SAVE) == SWT.SAVE) {
				shell.setText(UtilString.getString("lang:d=保存&en=Save", actionContext));
			}
			return shell;
		}else {
			FileDialog fileDialog = new FileDialog(parent, style);
			
			//其他属性
			String fileName = self.getStringBlankAsNull("fileName");
			if(fileName != null){
				fileDialog.setFileName(fileName);
			}
			String filterExtensions = self.getStringBlankAsNull("filterExtensions");
			if(filterExtensions != null){
				fileDialog.setFilterExtensions(UtilString.getStringArray(filterExtensions));
			}
			String filterNames = self.getStringBlankAsNull("filterNames");
			if(filterNames != null){
				fileDialog.setFilterNames(UtilString.getStringArray(filterNames));
			}
			String filterPath = self.getStringBlankAsNull("filterPath");
			if(filterPath != null){
				fileDialog.setFilterPath(filterPath);
			}
			int filterIndex = self.getInt("filterIndex", 0);
			fileDialog.setFilterIndex(filterIndex);		
			boolean overwrite = self.getBoolean("overwrite");
			fileDialog.setOverwrite(overwrite);
			
			return fileDialog;
		}
	}
	
	public static Object createDirectoryDialog(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Shell shell = (Shell) self.doAction("getShell", actionContext);
		File rootDir = (File) self.doAction("getRootDir", actionContext);
		
		ActionContext ac = new ActionContext();
		ac.put("parentContext", actionContext);
		ac.put("parent", shell);
		ac.put("rootDir", rootDir);
		ac.put("thing", self);
		
		Thing thing = World.getInstance().getThing("xworker.swt.actions.prototypes.CreateDirectoryDialog");
		Shell dshell = (Shell) thing.doAction("create", ac);
		if(SwtUtils.isRWT()) {
			SwtDialog dialog = new SwtDialog(shell, dshell, actionContext);
			dialog.open(null);
			return null;
		} else {
			return SwtDialog.open(dshell, ac);
		}	
	}
	
	public static Object createNewFileDialog(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Shell shell = (Shell) self.doAction("getShell", actionContext);
		File rootDir = (File) self.doAction("getRootDir", actionContext);
		
		ActionContext ac = new ActionContext();
		ac.put("parentContext", actionContext);
		ac.put("parent", shell);
		ac.put("rootDir", rootDir);
		ac.put("thing", self);
		
		Thing thing = World.getInstance().getThing("xworker.swt.actions.prototypes.CreateFileDialog");
		Shell dshell = (Shell) thing.doAction("create", ac);
		if(SwtUtils.isRWT()) {
			SwtDialog dialog = new SwtDialog(shell, dshell, actionContext);
			dialog.open(null);
			return null;
		} else {
			return SwtDialog.open(dshell, ac);
		}	
	}
	
	public static Object createThingFileDialog(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Shell shell = (Shell) self.doAction("getShell", actionContext);
		File rootDir = (File) self.doAction("getRootDir", actionContext);
		
		ActionContext ac = new ActionContext();
		ac.put("parentContext", actionContext);
		ac.put("parent", shell);
		ac.put("rootDir", rootDir);
		ac.put("thing", self);
		
		String variables = self.getStringBlankAsNull("variables");
		if(variables != null){
			for(String var : variables.split("[,]")){
				ac.put(var, actionContext.get(var));
			}
		}
		
		Thing thing = World.getInstance().getThing("xworker.swt.actions.prototypes.CreateThingFileDialog");
		Shell dshell = (Shell) thing.doAction("create", ac);
		if(SwtUtils.isRWT()) {
			SwtDialog dialog = new SwtDialog(shell, dshell, actionContext);
			dialog.open(null);
			return null;
		} else {
			return SwtDialog.open(dshell, ac);
		}
	}
}