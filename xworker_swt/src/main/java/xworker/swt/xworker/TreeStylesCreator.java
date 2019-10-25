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

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;

public class TreeStylesCreator {
	public static void treeCheckBoxSelection(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		TreeItem item = (TreeItem) event.item;
		
		if(item.getChecked()){
		    selectParent(item);
		    selectChilds(item);
		}else{
		    disselectChilds(item);
		}
	}
	
	public static void selectChilds(TreeItem item){
	    for(TreeItem childItem : item.getItems()){
	        childItem.setChecked(true);
	        selectChilds(childItem);
	    }
	}
	
	public static void disselectChilds(TreeItem item){
	    for(TreeItem childItem : item.getItems()){
	        childItem.setChecked(false);
	        disselectChilds(childItem);
	    }
	}
	
	public static void selectParent(TreeItem item){
	    TreeItem parentItem = item.getParentItem();
	    if(parentItem != null){
	        parentItem.setChecked(true);
	        selectParent(parentItem);
	    }
	}
}