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
package xworker.swt.xworker;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import freemarker.template.TemplateException;
import xworker.swt.design.Designer;
import xworker.swt.events.SwtListener;
import xworker.swt.form.TextEditor;
import xworker.swt.util.SwtUtils;
import xworker.util.UtilData;

public class CodeEditorCreator {
    public static Object create(ActionContext actionContext) throws IOException, TemplateException{
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("codeEditorThing");
    	Composite parent = (Composite) actionContext.get("parent");
    	
    	String codeName = UtilData.getString(self.getString("codeName"), actionContext);
    	String codeType = UtilData.getString(self.getString("codeType"), actionContext);
		ActionContext newContext = new ActionContext();
		newContext.put("parent", parent);
		newContext.put("codeName", codeName);
		newContext.put("codeType", codeType);
		newContext.put("parentContext", actionContext);
		ActionContext assistActionContext = self.doAction("getAssistActionContext", actionContext);		
		newContext.put("assistActionContext", assistActionContext);
		
		Thing codeEditor = world.getThing("xworker.swt.xworker.CodeEditor/@editorViewForm");
		Designer.pushCreator(self);
		Control root;
		try{
			root = codeEditor.doAction("create", newContext);
		}finally{
			Designer.popCreator();
		}
		Designer.attachCreator(root, self.getMetadata().getPath(), actionContext);
		
		Control textEditor = null;
		if(SwtUtils.isRWT()) {
			Text textEdtorT = newContext.getObject("editInput");
			textEdtorT.setData("actionContext", newContext);
			
			if(self.getStringBlankAsNull("code") != null){
				textEdtorT.setText(self.getString("code"));
			}
			
			textEdtorT.setData("lineLabel", newContext.get("editorLineLabel"));
			
			textEditor = textEdtorT;
		}else {
			StyledText textEditorS = (StyledText) newContext.get("editInput");
			textEditorS.setData("actionContext", newContext);
			if("true".equals(self.getString("WRAP"))){
				textEditorS.setWordWrap(true);
			}
			
			try{
				//代码着色
				Colorer.attach(textEditorS, codeName, codeType);
			}catch(Throwable ignored){
			}
			
			if(self.getStringBlankAsNull("code") != null){
				textEditorS.setText(self.getString("code"));
			}
			
			//设置redoundo和其他
			TextEditor.attach(textEditorS);
			textEditorS.setData("lineLabel", newContext.get("editorLineLabel"));
			
			textEditor = textEditorS;
		}
		
		//创建子节点
		actionContext.getScope(0).put(self.getString("name"), textEditor);
		actionContext.peek().put("parent", newContext.get("editorViewForm"));
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		//log.info("newContext.editorViewForm=" + newContext.editorViewForm.getLayoutData());
		
		//创建toolbar的自定义子节点
		newContext.peek().put("parent", newContext.get("editorToolBar"));
		for(Thing toolbarItems : self.getChilds("LeftToolItems")) {
			for(Thing item : toolbarItems.getChilds()) {
				item.doAction("create",  newContext);
			}
		}		

		return root;
	}

    public static void textKeyDown(ActionContext actionContext){
    	Event event = (Event) actionContext.get("event");
    	
    	StyledText text = (StyledText) event.widget;

    	int line =  text.getLineAtOffset(text.getCaretOffset()) + 1;
    	int carOffset = text.getCaretOffset();
    	int lineOffset = text.getOffsetAtLine(line - 1);

    	((Label) text.getData("lineLabel")).setText("" + line + ":" + (carOffset - lineOffset + 1));
    	((Label) text.getData("lineLabel")).update();

    	if(event.keyCode == 's' && event.stateMask == SWT.CTRL){
    		SwtListener okButtonSelection = (SwtListener) actionContext.get("okButtonSelection");
    	    if(okButtonSelection != null){
    	         okButtonSelection.handleEvent(null);
    	    }
    	}
    }
    
    public static void textMouseUp(ActionContext actionContext){
    	
    }
    
    public static void toolItemSelecitonScript(ActionContext actionContext){
    	World world = World.getInstance();
    	
        //取ViewForm数据对象的定义，要编辑的数据对象放置在ViewForm的数据对象中
    	if(SwtUtils.isRWT()) {
    		Text textEditor = (Text) actionContext.get("editInput");
    		
	    	ActionContext newContext = new ActionContext();
	    	newContext.put("parent", textEditor.getShell());
	    	newContext.put("fileType", textEditor.getData("fileType"));
	    	newContext.put("parentText", textEditor);
	    	newContext.put("saveButtonSelection", actionContext.get("okButtonSelection"));
	
	    	Thing shellObject = world.getThing("xworker.swt.xworker.CodeEditorPop/@shell");
	    	shellObject.doAction("create", newContext);
	
	    	Text newtextEditor = (Text) newContext.get("textEditor");
	   
	    	newtextEditor.setText(textEditor.getText());
	    	newtextEditor.setData("oldText", textEditor);
	
	    	//newtextEditor.setTopIndex(textEditor.getTopIndex());
	
	    	((Shell) newContext.get("shell")).open();
    	}else {
	    	StyledText textEditor = (StyledText) actionContext.get("editInput");
	
	    	ActionContext newContext = new ActionContext();
	    	newContext.put("parent", textEditor.getShell());
	    	newContext.put("fileType", textEditor.getData("fileType"));
	    	newContext.put("parentText", textEditor);
	    	newContext.put("saveButtonSelection", actionContext.get("okButtonSelection"));
	
	    	Thing shellObject = world.getThing("xworker.swt.xworker.CodeEditorPop/@shell");
	    	shellObject.doAction("create", newContext);
	
	    	StyledText newtextEditor = (StyledText) newContext.get("textEditor");
	    	if(textEditor.getWordWrap()){
	    		newtextEditor.setWordWrap(true);
	    	}
	    	newtextEditor.setText(textEditor.getText());
	    	newtextEditor.setData("oldText", textEditor);
	
	    	newtextEditor.setTopIndex(textEditor.getTopIndex());
	    	newtextEditor.setCaretOffset(textEditor.getCaretOffset());
	
	    	//CodeEditorSyncer.attach(textEditor, newContext.textEditor);
	    	//CodeEditorSyncer.attach(newContext.textEditor, textEditor);
	
	    	//克隆CodeAssistor
	    	CodeAssistor.clone(textEditor, newtextEditor);
	    	((Shell) newContext.get("shell")).open();
    	}
    }
    
    public static void contentTextKeyDown(ActionContext actionContext){
    	Event event = (Event) actionContext.get("event");

    	if(!SwtUtils.isRWT()) {
	    	StyledText text = (StyledText) event.widget;
	
	    	int line = text.getLineAtOffset(text.getCaretOffset()) + 1;
	    	int carOffset = text.getCaretOffset();
	    	int lineOffset = text.getOffsetAtLine(line - 1);
	    	
	
	    	((Label) text.getData("lineLabel")).setText("" + line + ":" + (carOffset - lineOffset + 1));
	    	((Label) text.getData("lineLabel")).update();
    	}else {
    		Text text = (Text) event.widget;
    		    		/*
	    	int line = 0;//text.getCaretLineNumber() + 1;
	    	int carOffset = 0;//text.getSelection().y;
	    	int lineOffset = 0;//text.get;
	    	
	
	    	((Label) text.getData("lineLabel")).setText("" + line + ":" + (carOffset - lineOffset + 1));
	    	((Label) text.getData("lineLabel")).update();*/
    	}

    	if(event.keyCode == 's' && event.stateMask == SWT.CTRL){
    		SwtListener okButtonSelection = (SwtListener) actionContext.get("okButtonSelection");
    	    if(okButtonSelection != null){
    	        okButtonSelection.handleEvent(null);
    	    }
    	}
    }
}