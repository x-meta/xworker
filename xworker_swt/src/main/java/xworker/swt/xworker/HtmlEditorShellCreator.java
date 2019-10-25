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

import org.eclipse.swt.browser.Browser;
import org.xmeta.ActionContext;

public class HtmlEditorShellCreator {
    public static void init(ActionContext actionContext){
        //browser.setUrl(webUrl + "do?sc=xworker.swt.xworker.HtmlEditorHttp/@showEditorHtml&toolbarSet=${toolbarSet}");
    	String value = (String) actionContext.get("value");
        if(value == null) value = "";
        Browser browser = (Browser) actionContext.get("browser");
        browser.setData(value);
        String html = value.replaceAll("(\")", "&_quot_;");
        html = html.replaceAll("(\n)", "&_n_;");
        html = html.replaceAll("(\r)", "&_r_;");
        //browser.execute("setContents(\"" + html + "\");");
        HtmlEditorCreator.executeScript(browser, "setContents(\"" + html + "\");", actionContext);
    }

}