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
package xworker.app.view.swt.data.events;

import org.xmeta.ActionContext;

public class RadioDataStoreListener {
	public static void onReconfig(final ActionContext actionContext){
		CheckBoxDataStoreListener.onReconfig(actionContext);
	}
	
	public static void onLoaded(final ActionContext actionContext){
		CheckBoxDataStoreListener.onLoaded(actionContext, true);
	}
	
	public static void beforeLoad(final ActionContext actionContext){
		CheckBoxDataStoreListener.beforeLoad(actionContext);
	}
	
	public static void setSelection(final ActionContext actionContext){
		CheckBoxDataStoreListener.setSelection(actionContext);
	}
	
	public static void onInsert(final ActionContext actionContext){
		CheckBoxDataStoreListener.onInsert(actionContext, true);
	}
	
	public static void onUpdate(final ActionContext actionContext){
		CheckBoxDataStoreListener.onUpdate(actionContext);
	}
	
	public static void onRemove(final ActionContext actionContext){
		CheckBoxDataStoreListener.onRemove(actionContext);
	}
}