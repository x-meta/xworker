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

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;

import xworker.swt.util.PoolableControlFactory;

public class CodeEditorSearchDialogCreator {
	public static void findButtonSelection(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		Button findReplaceButton = (Button) actionContext.get("findReplaceButton");
		Button replaceButton= (Button) actionContext.get("replaceButton");
		Button closeButton = (Button) actionContext.get("closeButton");
		Button findButton = (Button) actionContext.get("findButton");
		Button upButton = (Button) actionContext.get("upButton");
		Button replaceAllButton = (Button) actionContext.get("replaceAllButton");
		Shell shell = (Shell) actionContext.get("shell");
		Control parent = (Control) actionContext.get("parent");		
		StyledText styledText = (StyledText) actionContext.get("styledText");
		Text replaceText = (Text) actionContext.get("replaceText");		
		Text searchText = (Text) actionContext.get("searchText");
		int currentIndex = styledText.getSelection().x;
		
		if(event.widget == findReplaceButton || event.widget == replaceButton){
		    String replaceTextStr = replaceText.getText();
		    
		    if(styledText.getSelectionText() != ""){
		        styledText.insert(replaceTextStr);
		    }
		}
		    
		if(event.widget == closeButton){
		    shell.setVisible(false);
		    
		    styledText.setData("frDialog", null);
		    PoolableControlFactory.returnControl(parent, "xworker.swt.xworker.CodeEditorSearchDialog", shell);
		}else if(event.widget == findButton || event.widget == findReplaceButton){
		    String findText = searchText.getText();
		    if(findText != ""){
		        if(actionContext.get("currentIndex") == null){
		            actionContext.getScope(0).put("currentIndex", currentIndex);
		        }

		        String text = styledText.getText();
		        if(upButton.getSelection()){
		            int index = text.lastIndexOf(findText, currentIndex - 1);
		            if(index != -1){
		                styledText.setSelection(index, index + findText.length());
		                currentIndex = index - findText.length() - 1;
		            }            
		        }else{
		            int index = text.indexOf(findText, currentIndex + 1);
		            if(index != -1){
		                styledText.setSelection(index, index + findText.length());
		                currentIndex = index + findText.length() + 1;
		            }
		        }
		    }
		}else if(event.widget == replaceAllButton){
		    String findText = searchText.getText();
		    String replaceTextStr = replaceText.getText();
		    
		    if(findText != ""){        
		        while(true){
		            String text = styledText.getText();
		            int index = text.indexOf(findText, currentIndex);
		            if(index != -1){
		                styledText.setSelection(index, index + findText.length());
		                currentIndex = index + replaceTextStr.length() + 1;
		                
		                if(styledText.getSelectionText() != ""){
		                    styledText.insert(replaceTextStr);
		                }
		            }else{
		                break;
		            }
		        }
		    }
		}
	}
}