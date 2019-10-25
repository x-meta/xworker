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

import org.xmeta.ActionContext;

public class HtmlEditorHttpShowEditorHtmlCreator {
    public static Object doAction(ActionContext actionContext){
        /*
        def content = requestBean.editorContent;
        if(requestBean.init != "true"){
             println content;
             world.setData("xworker_editor_html_content", content);
             def htmlThing = world.getData("xworker_editor_html_content_htmlThing");
             def htmlActionContext = world.getData("xworker_editor_html_content_actionContext");
             def display = world.getData("xworker_editor_html_content_display");
        
             //def value =  browser.getData("query");
             display.syncExec([
                  run : {
                       htmlThing.doAction("setValue", htmlActionContext, ["value":content]);
                  }
             ] as Runnable);
        }*/
        
        return "success";
    }

}