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
package xworker.swt.util;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;

/**
 * SWT3.5及以上。
 * 
 * @author Administrator
 *
 */
public class UtilBrowserJavaScriptFunction extends BrowserFunction implements DisposeListener{
	UtilBrowser utilBrowser;
	
	public static void attach(UtilBrowser utilBrowser){
		//XWorker浏览钱相关的函数
		UtilBrowserJavaScriptFunction uf = new UtilBrowserJavaScriptFunction(utilBrowser.browser, "utilBrowserFunction");
		uf.setUtilBrowser(utilBrowser);
		
		//码农的世界相关的函数
		//new MaNongJavaScriptFunction(utilBrowser.browser, "manongFunction");
	}
	
	public UtilBrowserJavaScriptFunction(Browser browser, String name) {
		super(browser, name);		
		
		browser.addDisposeListener(this);
	}

	public void setUtilBrowser(UtilBrowser utilBrowser){
		this.utilBrowser = utilBrowser;
	}
	
	@Override
	public Object function(Object[] arguments) {
		if(arguments.length >= 1 && arguments[0] instanceof String){
			utilBrowser.handle((String) arguments[0]);
			return 1;
		}else{
			return 0;
		}
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {
		if(this.isDisposed() == false) {
			this.dispose();
		}
	}

}