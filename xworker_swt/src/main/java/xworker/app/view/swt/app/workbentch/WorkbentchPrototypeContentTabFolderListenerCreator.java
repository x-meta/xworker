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
package xworker.app.view.swt.app.workbentch;

import java.util.Map;

import org.eclipse.swt.custom.CTabFolderEvent;
import org.xmeta.ActionContext;

import xworker.swt.ActionContainer;

public class WorkbentchPrototypeContentTabFolderListenerCreator {
    @SuppressWarnings("unchecked")
	public static void close(ActionContext actionContext){
    	CTabFolderEvent event = (CTabFolderEvent) actionContext.get("event");
    	
        //TABITEM已关闭，移除实例
        Map<String, Object> instance = (Map<String, Object>) event.item.getData();
        if(instance != null){
        	Map<String, Object> editor = (Map<String, Object>) instance.get("editor");
        	Map<String, Object> instances = (Map<String, Object>) editor.get("instances");
            instances.remove(instance.get("id"));
        }
        actionContext.put("currentEditor", null);
        
        ((ActionContainer) actionContext.get("workbentchActions")).doAction("initAfterEditorChanged");
    }

}