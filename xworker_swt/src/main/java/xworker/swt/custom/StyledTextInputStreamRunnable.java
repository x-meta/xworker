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
package xworker.swt.custom;

import java.io.InputStream;

import org.eclipse.swt.widgets.Widget;
import org.xmeta.util.ExceptionUtil;

import xworker.lang.executor.Executor;
import xworker.swt.functions.AutoScroll;
import xworker.swt.util.SwtTextUtils;
import xworker.swt.util.SwtUtils;

/**
 * 读取InputStream并把数据显示到StyledText的线程。
 * 
 * @author Administrator
 *
 */
public class StyledTextInputStreamRunnable implements Runnable, AutoScroll{
	private static final String TAG = StyledTextInputStreamRunnable.class.getName();
	
	Widget text;
	InputStream inputStream;
	boolean autoScroolToBottom;
	String charset;
	
	public StyledTextInputStreamRunnable(Widget text, InputStream inputStream, boolean autoScroolToBottom, String charset){
		this.text = text;
		this.inputStream = inputStream;
		this.autoScroolToBottom = autoScroolToBottom;
		this.charset = charset;
		
		SwtUtils.regist(text, this, AutoScroll.class);
	}
	
	public void setAutoScroolToBottom(boolean autoScroolToBottom){
		this.autoScroolToBottom = autoScroolToBottom;
	}
	
	public void run(){
		try{
			byte[] bytes = new byte[1024];
			int length = -1;
			while((length = inputStream.read(bytes)) != -1){
				final String str = new String(bytes, 0, length, charset);
				if(text == null || text.isDisposed()){
					break;
				}
				
				text.getDisplay().asyncExec(new Runnable(){
					public void run(){
						SwtTextUtils.append(text, str);
						//text.append(str);
						
						if(autoScroolToBottom){
							SwtTextUtils.scrollToBottom(text);
							 //text.setTopIndex(text.getLineCount() - 1);
							 //text.setCaretOffset(text.getText().length());
						}
					}
				});			
			}
		}catch(final Exception e){
			Executor.info(TAG, "get data form inputStream error", e);
			
			if(text != null && text.isDisposed() == false) {
				//是InputStream结束了
				text.getDisplay().asyncExec(new Runnable(){
					public void run(){
						SwtTextUtils.append(text, ExceptionUtil.toString(e));
						
						if(autoScroolToBottom){
							SwtTextUtils.scrollToBottom(text);
							 //text.showSelection();
						}
					}
				});
			}
		}
	}

	@Override
	public boolean isAutoScroll() {
		return autoScroolToBottom;
	}

	@Override
	public void setAutoScroll(boolean autoScroll) {
		this.autoScroolToBottom = autoScroll;
		
		if(autoScroolToBottom){
			SwtTextUtils.scrollToBottom(text);
			// text.setTopIndex(text.getLineCount() - 1);
			// text.setCaretOffset(text.getText().length());
		}
	}

}