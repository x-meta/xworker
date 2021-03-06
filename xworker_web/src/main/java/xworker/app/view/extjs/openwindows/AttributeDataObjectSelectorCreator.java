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
package xworker.app.view.extjs.openwindows;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class AttributeDataObjectSelectorCreator {
    public static void httpDo(ActionContext actionContext){
        //通过模板生成属性数据对象选择远程控件事物
        Thing template = World.getInstance().getThing("xworker.app.view.extjs.openwindows.AttributeSelectorTemplate");
        Thing selector = (Thing) template.doAction("run", actionContext);
        
        //输出Extjs的相关代码
        selector.getChilds().get(0).doAction("httpDo", actionContext);
    }

}