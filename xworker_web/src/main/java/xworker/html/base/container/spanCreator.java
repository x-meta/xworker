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
package xworker.html.base.container;

import org.xmeta.ActionContext;
import org.xmeta.util.UtilMap;

import xworker.html.HtmlUtil;
import xworker.util.UtilTemplate;

public class spanCreator {
    public static Object toHtml(ActionContext actionContext) throws Throwable{
        String childHtmls = HtmlUtil.getChildsHtml(actionContext);
        return UtilTemplate.process(UtilMap.toMap(new Object[]{"object", actionContext.get("self"), "childHtmls",childHtmls}), "/xworker/html/base/templates/span.ftl", "freemarker", "utf-8");
    }

}