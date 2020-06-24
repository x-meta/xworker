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
package xworker.swt.editor;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

import xworker.swt.util.UtilBrowserListener;

public class ToolTipStatusListener implements StatusTextListener, ProgressListener, UtilBrowserListener{
    Shell shell;
    public ToolTipStatusListener(Shell shell){
        this.shell = shell;
    }
    
    public void changed(StatusTextEvent event) {
    	//Browser browser = (Browser) event.getSource();
    	//System.out.println(event.text);
    	handleBrowserMessage(event.text); 	        
    }

    public void changed(ProgressEvent event) {
        //event.widget.execute("window.status=getDivSize()");
    }
    
    public void completed(ProgressEvent event) { 
        //println "completed";        
        ((Browser) event.widget).execute("getContents()");
    }

	@Override
	public boolean handleBrowserMessage(String message) {
		if(message != null && message.startsWith("html_edit_content")){
            //取编辑器内容的值
            //System.out.println("tooltip text=" + event.text);
    	    String content = message.substring(message.indexOf(":") + 1, message.length());
    	    //System.out.println(content);
            String[] sizes = content.split(":");
            int width = Integer.parseInt(sizes[1]) + 20;
            if(width < 420){
                width = 420;
            }
                
            int height = Integer.parseInt(sizes[0]);
            if(height < 10){
            	height = 10;
            }
            try{            
            	Point size = shell.getSize();
            	Rectangle area = shell.getClientArea();
            	int bx = size.x - area.width;
            	int by = size.y - area.height;
                shell.setSize(width + bx , height + 3 + by);                
            }catch(Exception e){
                shell.setSize(420, 300);
            }
            
            shell.setVisible(true);
            return true;
        }    
		
		return false;
	}
}