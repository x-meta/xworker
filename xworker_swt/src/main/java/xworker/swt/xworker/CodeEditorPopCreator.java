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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.World;

import net.sf.colorer.FileType;
import net.sf.colorer.ParserFactory;
import net.sf.colorer.swt.ColorManager;
import net.sf.colorer.swt.TextColorer;
import xworker.swt.events.SwtListener;
import xworker.swt.form.TextEditor;
import xworker.swt.util.SwtUtils;

public class CodeEditorPopCreator {
	public static void textEditorPaint(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		StyledText text = (StyledText) event.widget;

		int line = text.getLineAtOffset(text.getCaretOffset()) + 1;
		int carOffset = text.getCaretOffset();
		int lineOffset = text.getOffsetAtLine(line - 1);

		Label statusLabel = (Label) actionContext.get("statusLabel");
		statusLabel.setText("" + line + ":" + (carOffset - lineOffset + 1));
		statusLabel.update();
	}
	
	public static void textEditorKeyDown(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		
		if(event.keyCode == 's' && event.stateMask == SWT.CTRL && actionContext.get("saveButtonSelection") != null){
		    ((SwtListener) actionContext.get("saveButtonSelection")).handleEvent(null);
		}
	}
	
	public static void okButtonSelection(ActionContext actionContext){
		if(SwtUtils.isRWT()){
			Text parentText = (Text) actionContext.get("parentText");
			Text textEditor = (Text) actionContext.get("textEditor");
			parentText.setText(textEditor.getText());
		}else {
			StyledText parentText = (StyledText) actionContext.get("parentText");
			StyledText textEditor = (StyledText) actionContext.get("textEditor");
			parentText.setText(textEditor.getText());
			parentText.setTopIndex(textEditor.getTopIndex());
			parentText.setCaretOffset(textEditor.getCaretOffset());
		}

		((Shell) actionContext.get("shell")).dispose();
	}
	
	public static void cancelButtonSelection(ActionContext actionContext){
		if(SwtUtils.isRWT()) {
		
		}else {
			StyledText parentText = (StyledText) actionContext.get("parentText");
			StyledText textEditor = (StyledText) actionContext.get("textEditor");
			
			parentText.setTopIndex(textEditor.getTopIndex());		
			parentText.setCaretOffset(textEditor.getCaretOffset());
		}

		((Shell) actionContext.get("shell")).dispose();
	}
	
	public static void init(ActionContext actionContext){
		if(SwtUtils.isRWT()) {
			return;
		}
		
		World world = World.getInstance();
		StyledText textEditor = (StyledText) actionContext.get("textEditor");
		
		ParserFactory pf = Colorer.getParserFactory();
		ColorManager colorManager = (ColorManager) world.getData("colorerColorManage");
		if(colorManager == null){
		    colorManager = new ColorManager();
		    world.setData("colorerColorManage", colorManager);
		}
		TextColorer textColorer = new TextColorer(pf, colorManager);
		textColorer.attach(textEditor);
		textColorer.setCross(true, true);
		textColorer.setRegionMapper("default", true);
		textColorer.setFileType((FileType) actionContext.get("fileType"));

		//设置redoundo和其他
		TextEditor.attach(textEditor);
	}
}