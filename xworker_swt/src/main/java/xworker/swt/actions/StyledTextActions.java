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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import ognl.OgnlException;
import xworker.swt.custom.StyledTextInputStreamRunnable;
import xworker.swt.util.SwtTextUtils;
import xworker.swt.util.SwtUtils;

public class StyledTextActions {
	private static Logger logger = LoggerFactory.getLogger(StyledTextActions.class);
	
	/**
	 * 绑定InputStream到StyledText。
	 * 
	 * @param actionContext
	 * @return
	 * @throws OgnlException
	 */
	public static Object bindInputStream(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("data");
		Widget text = (Widget) self.doAction("getStyledText", actionContext);
		InputStream inputStream = (InputStream) self.doAction("getInputStream", actionContext);
		boolean autoScroolToBottom = self.getBoolean("autoScroolToBottom");
		
		if(text != null && inputStream != null){
			String charset = self.getStringBlankAsNull("charset");
			if(charset == null){
				charset = "utf-8";
			}
						
			StyledTextInputStreamRunnable run = new StyledTextInputStreamRunnable(text, inputStream, autoScroolToBottom, charset);
			if(self.getBoolean("createThread")){
				new Thread(run).start();
			}else{
				run.run();
			}
			
			return run;
		}else{
			return null;
		}
	}
	
	public static Object keyInputToInputStream(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		if(SwtUtils.isRWT()) {
			return null;
		}else {
			return SwtUtils.runIAction("xworker.swt.util.VerifyEventToInputStream", self, actionContext);
		}
		
		/*
		String inputStreamVarName = self.getStringBlankAsNull("inputStreamVarName");
		if(inputStreamVarName != null){
			actionContext.getScope(0).put(inputStreamVarName, listener.getInputStream());
		}
		actionContext.getScope(0).put(self.getMetadata().getName(), listener);
		*/
	}
	
	public static Object keyInputToOutputStream(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		if(SwtUtils.isRWT()) {
			return null;
		}else {
			return SwtUtils.runIAction("xworker.swt.util.VerifyEventToOutputStream", self, actionContext);
		}
		//actionContext.getScope(0).put(self.getMetadata().getName(), listener);
	}
	
	public static void append(ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		final Widget styledText = (Widget) self.doAction("getStyledText", actionContext);
		final String text = (String) self.doAction("getText", actionContext);
		if(styledText != null && text != null){
			styledText.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						SwtTextUtils.append(styledText, text);
						//styledText.append(text);
						if(self.getBoolean("autoScroolToBottom")){
							SwtTextUtils.scrollToBottom(text);
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			});			
		}
	}
	
	public static void setText(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Widget styledText = (Widget) self.doAction("getStyledText", actionContext);
		String text = (String) self.doAction("getText", actionContext);

		if(styledText == null){
		    logger.info("SetText: styleText is null, path=" + self.getMetadata().getPath());
		    return;
		}

		if(text == null){
			logger.info("SetText: text is null, path=" + self.getMetadata().getPath());
		    return;
		}

		SwtTextUtils.setText(styledText, text);
		//styledText.setText(text);
	}
}