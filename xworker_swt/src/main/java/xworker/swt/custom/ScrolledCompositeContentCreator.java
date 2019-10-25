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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ScrolledCompositeContentCreator {
    public static void create(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        
        //保存变量和创建子事物
        actionContext.peek().put("parent", actionContext.get("parent"));
        for(Thing child : self.getAllChilds()){
            Control childWidget = (Control) child.doAction("create", actionContext);
            
            ((ScrolledComposite) actionContext.get("parent")).setContent(childWidget);
            Point size = childWidget.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            childWidget.setSize(size);
            ((ScrolledComposite) actionContext.get("parent")).setMinSize(size);
        
            break;
        }
        actionContext.peek().remove("parent");
    }

}