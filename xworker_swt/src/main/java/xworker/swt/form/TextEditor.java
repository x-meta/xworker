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
package xworker.swt.form;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;

import xworker.swt.ActionContainer;
import xworker.swt.util.PoolableControlFactory;

/**
 * 向StyledText添加Redo、undo等功能。
 * 
 * @author zyx
 *
 */
public class TextEditor extends KeyAdapter implements ExtendedModifyListener {
	private static final int MAX_STACK_SIZE = 250;
	private static final String KEY = "XWORKER_TEXTEDITOR_ATTACH"; 
			
	private List<RedoUndoEntity> undoStack;
	private List<RedoUndoEntity> redoStack;
	private StyledText styledText;
	private boolean isUndo = false;
	private boolean isRedo = false;

	public TextEditor(StyledText styleText) {
		undoStack = new LinkedList<RedoUndoEntity>();
		redoStack = new LinkedList<RedoUndoEntity>();
		this.styledText = styleText;		
	}

	public static TextEditor attach(StyledText styleText) {		
		TextEditor editor = (TextEditor) styleText.getData(KEY);
		if(editor != null) {
			return editor;
		}
		editor = new TextEditor(styleText);
		styleText.setData(KEY, editor);
		styleText.addExtendedModifyListener(editor);
		styleText.addKeyListener(editor);
		styleText.setKeyBinding('\t' | SWT.TAB, -1);
		styleText.setKeyBinding('\t' | SWT.SHIFT, -1);
		styleText.setKeyBinding(SWT.TAB | SWT.SHIFT, -1);
		styleText.addTraverseListener(new TraverseListener(){

			public void keyTraversed(TraverseEvent event) {
				if(event.detail == SWT.TRAVERSE_TAB_NEXT || event.detail == SWT.TRAVERSE_TAB_PREVIOUS ){
					event.doit = false;
				}
			}			
		});
		
		return editor;
	}
	
	public boolean isRedoable() {
		return redoStack.size() > 0;
	}
	
	public boolean isUndoable() {
		return undoStack.size() > 0;
	}

	public void modifyText(ExtendedModifyEvent event) {
		String currText = styledText.getText();
		String newText = currText.substring(event.start, event.start
				+ event.length);
		
		//设置redo,undo
		RedoUndoEntity entity = new RedoUndoEntity();
		entity.length = event.length;
		entity.start = event.start;
		entity.newText = newText;
		entity.replacedText = event.replacedText;
		
		if(isUndo){
			redoStack.add(0, entity);
			isUndo = false;
		}else if(!isRedo){
			if (undoStack.size() == MAX_STACK_SIZE) {
				undoStack.remove(undoStack.size() - 1);
			}
			undoStack.add(0, entity);
			redoStack.clear();
		}else{
			undoStack.add(0, entity);
			isRedo = false;
		}		
	}

	public void keyPressed(KeyEvent e) {	
		//System.out.println("'" + e.keyCode + "'");
		if(e.keyCode == 'z' && e.stateMask == SWT.CTRL){			
			undo();
		}else if(e.keyCode == 'y' && e.stateMask == SWT.CTRL){
			redo();
		}else if(e.keyCode == 'a' && e.stateMask == SWT.CTRL){
			styledText.selectAll();						
		}else if(e.keyCode == 'f' && e.stateMask == SWT.CTRL){		
			if(styledText.getData("frDialog") == null){
				Control frDialog = PoolableControlFactory.borrowControl(styledText.getShell(),
						"xworker.swt.xworker.CodeEditorSearchDialog", null);
				ActionContext context = (ActionContext) frDialog.getData("_poolActionContext");
				context.put("styledText", styledText);
				context.put("currentIndex", 0);
				styledText.setData("frDialog", frDialog);
				frDialog.setVisible(true);
				frDialog.setFocus();
			}
		}else if(e.keyCode == '.'){
			/*
			if(styledText.getData("assitDialog") == null){
				Control assitDialog = PoolableControlFactory.borrowControl(styledText.getShell(),
						"xworker.swt.xworker.CodeEditorContentAssist/@shell", null);
				ActionContext context = (ActionContext) assitDialog.getData("_poolActionContext");
				context.put("styledText", styledText);
				context.put("currentIndex", 0);
				styledText.setData("assitDialog", assitDialog);
				
				int x = 0; 
				int y = 0;
				Point location = styledText.getCaret().getLocation();
				Point slocation = styledText.getLocation();
				x = location.x + slocation.x;
				y = location.y + slocation.y;
				Composite parent = styledText;
				do{
					Point plocation = parent.getLocation();
					x = x + plocation.x;
					y = y + plocation.y;
					
					parent = parent.getParent();
				}while(parent != null);
				
				assitDialog.setLocation(x, y + 25 + styledText.getCaret().getSize().y);
				assitDialog.setVisible(true);
			}*/
		}else if(e.keyCode == SWT.ESC){
			if(styledText.getData("assitDialog") != null){
				Control assitDialog = (Control) styledText.getData("assitDialog");
				ActionContext context = (ActionContext) assitDialog.getData("_poolActionContext");
				ActionContainer actions = (ActionContainer) context.get("actions");
				actions.doAction("hide");
			}else if(styledText.getData("frDialog") != null){
				Control frDialog = (Control) styledText.getData("frDialog");
				PoolableControlFactory.returnControl(styledText.getParent(), "xworker.swt.xworker.CodeEditorSearchDialog", frDialog);
				frDialog.setVisible(false);
			}
		}else if(e.keyCode == SWT.TAB){
			boolean setSelection = true;
			int selectionStart = styledText.getSelection().x;
			int selectionEnd = styledText.getSelection().y;
			if(selectionStart == selectionEnd){
				setSelection = false;
				styledText.insert("\t");
				return;
			}
			
			if(e.stateMask == SWT.SHIFT){
				//Shift + Tab

				
				int lineStart = styledText.getLineAtOffset(styledText.getSelection().x);
				int lineEnd = styledText.getLineAtOffset(styledText.getSelection().y);
				String endLine = styledText.getLine(lineEnd);
				int startOffset = styledText.getOffsetAtLine(lineStart);
				int endOffset = styledText.getOffsetAtLine(lineEnd) + endLine.length() ;
				
				boolean canMove = true;
				String newText = null;
				
				for(int i=lineStart; i<=lineEnd; i++){
					String line = styledText.getLine(i);
					if(!line.startsWith("\t") && !line.startsWith("    ")){
						canMove = false;
					}else{
						String newLine = null;
						if(line.startsWith("\t")){
							newLine = line.substring(1, line.length());
							selectionEnd -= 1;
							if(i == lineStart){
								selectionStart -= 1;
							}
						}else{
							newLine = line.substring(4, line.length());
							selectionEnd -= 4;
							if(i == lineStart){
								selectionStart -= 4;
							}
						}
						if(newText == null){
							newText = newLine;
						}else{
							newText = newText + "\n" + newLine;
						}
					}
				}
				
				if(canMove){
					//替换
					styledText.replaceTextRange(startOffset, endOffset - startOffset, newText);
					if(selectionStart < startOffset){
						selectionStart = startOffset;
					}
					if(setSelection){
						styledText.setSelection(selectionStart, selectionEnd);
					}
				}
			}else{
//				Tab

				int lineStart = styledText.getLineAtOffset(styledText.getSelection().x);
				int lineEnd = styledText.getLineAtOffset(styledText.getSelection().y);
				String endLine = styledText.getLine(lineEnd);
				int startOffset = styledText.getOffsetAtLine(lineStart);
				int endOffset = styledText.getOffsetAtLine(lineEnd) + endLine.length() ;
				
				String newText = null;
				for(int i=lineStart; i<=lineEnd; i++){
					String line = styledText.getLine(i);
					line.replaceAll("\t", "    "); //把Tab转换为空格

					if(newText == null){
						newText = "    " + line;
					}else{
						newText = newText + "\n    " + line;
					}
					if(i == lineStart){
						selectionStart += 4;
					}
					selectionEnd += 4;
				}

				//替换
				styledText.replaceTextRange(startOffset, endOffset - startOffset, newText);
				styledText.setSelection(selectionStart, selectionEnd);
			}
		}
		
		if(e.keyCode == 0x0D){
			styledText.setData("__isMyEnter__", true);
		}
	}

	public void keyReleased(KeyEvent e) {	
		if(e.doit == false){
			return;
		}
		
		//确保是自己(StyledText)按的回车
		Boolean isEnter = (Boolean) styledText.getData("__isMyEnter__");
		if(e.keyCode == 0x0D && isEnter != null && isEnter == true){
			styledText.setData("__isMyEnter__", false);
			
			int caretOffset = styledText.getCaretOffset();
			int lineIndex = styledText.getLineAtOffset(caretOffset);
			int preLineOffset = 0;
			if(lineIndex > 0){
				preLineOffset = styledText.getOffsetAtLine(lineIndex - 1);
			}
			String lastLine = null;
			if(caretOffset > 0){
				lastLine = styledText.getText(preLineOffset, caretOffset - 1);
			}
			//取前一行的空格和tab
			String space = "";
			if(lastLine != null){
				for(int i=0; i<lastLine.length(); i++){
					char ch = lastLine.charAt(i);
					if(ch == ' ' || ch == '\t'){
						space = space + ch;
					}else{
						break;
					}
				}
			}

			if(!"".equals(space)){					
				styledText.insert(space);
				styledText.setCaretOffset(caretOffset + space.length());
			}
		}
	}
	
	public void undo() {
		try{
			if (undoStack.size() > 0) {
				isUndo = true;
				RedoUndoEntity lastEdit = (RedoUndoEntity) undoStack.remove(0);
				int editLength = lastEdit.length;			
				int startReplaceIndex = lastEdit.start;
				styledText.replaceTextRange(startReplaceIndex, editLength, lastEdit.replacedText);				
				styledText.setCaretOffset(lastEdit.start);
			}
		}catch(Exception e){
			e.printStackTrace();			
		}
	}

	public void redo() {
		try{
			if (redoStack.size() > 0) {
				isRedo = true;
				RedoUndoEntity lastEdit = (RedoUndoEntity) redoStack.remove(0);
				int editLength = lastEdit.length;			
				int startReplaceIndex = lastEdit.start;				
				styledText.replaceTextRange(startReplaceIndex, editLength, lastEdit.replacedText);
				styledText.setCaretOffset(lastEdit.start + lastEdit.replacedText.length());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	class RedoUndoEntity{
		String newText;
		String replacedText;
		int start;
		int length;
	}

}