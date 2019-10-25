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
package xworker.swt.events.custom;

import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.events.BaseListener;

public class ICTabFolder2Listener extends BaseListener implements CTabFolder2Listener{

	public ICTabFolder2Listener(ActionContext actionContext, Thing actionThing){
		super(actionContext, actionThing);
	}
	
	public void close(CTabFolderEvent event) {
		invokeMethod("close", new Object[]{"event", event});
	}

	public void maximize(CTabFolderEvent event) {
		invokeMethod("maximize", new Object[]{"event", event});
	}

	public void minimize(CTabFolderEvent event) {
		invokeMethod("minimize", new Object[]{"event", event});
	}

	public void restore(CTabFolderEvent event) {
		invokeMethod("restore", new Object[]{"event", event});
	}

	public void showList(CTabFolderEvent event) {
		invokeMethod("showList", new Object[]{"event", event});
	}

	
}