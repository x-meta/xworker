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
package xworker.swt.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.swt.design.Designer;

public class BrowserCreator {
	private static Logger logger = LoggerFactory.getLogger(BrowserCreator.class);
	
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
		int browserStyle = SWT.NONE;
		if(UtilString.eq(self, "BORDER", "true")){
		    browserStyle = browserStyle | SWT.BORDER;
		}
		
		if(UtilString.eq(self, "MOZILLA", "true")){
		    browserStyle = browserStyle | SWT.MOZILLA;
		}
		
		if(UtilString.eq(self, "NO_FOCUS", "true")){
		    browserStyle |= SWT.NO_FOCUS;
		}
		
		boolean webKit = false;
		if(UtilString.eq(self, "WEBKIT", "true")){			
	    	webKit = true;
		}
		    
		Composite parent = (Composite) actionContext.get("parent");
		Browser browser = null;
		if(webKit && SWT.getVersion() > 3700){// && UtilBrowser.isWebKit()){
			//如果使用webKit，早期的swt版本或者平台没有安装类似的浏览器都会出错
			//但是在ie10是webKit，如果标记webKit那么fckEditor没办法使用
			try{
				browser = new Browser(parent, browserStyle | 65536);
			}catch(Throwable e){				
				//logger.warn("webkit not supported: " + e.getMessage());
			}
		}
		
		if(!webKit || browser == null){
			browser = new Browser(parent, browserStyle);
		}
		
		if(self.getStringBlankAsNull("javascriptEnabled") != null){
			try {
				browser.setJavascriptEnabled(self.getBoolean("javascriptEnabled"));
			}catch(Throwable t) {
				//Eclipse RAP不能使用
			}
		}
		
		String url = self.getString("url");
		if(url != null && url != ""){
			try {			
				browser.setUrl(url);
			}catch(Throwable t) {
				t.printStackTrace();
			}
		}else {
			String text = self.getString("text");
			if(text != null && text != ""){
			    browser.setText(text);
			}
		}
		//BrowserDisposeListener.attach(browse);
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), browser);
		Bindings bindings = actionContext.push();
		bindings.put("parent", browser);
		try{
			actionContext.peek().put("parent", browser);
			for(Thing child : self.getAllChilds()){
			    child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
		
		Designer.attach(browser, self.getMetadata().getPath(), actionContext);
		return browser;        
	}
}