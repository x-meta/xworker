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
package xworker.swt.events;

import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class IShellListener extends BaseListener implements ShellListener{

	public IShellListener(ActionContext actionContext, Thing actionThing){
		super(actionContext, actionThing);
	}
	
	public void shellActivated(ShellEvent event) {
		invokeMethod("shellActivated", new Object[]{"event", event});
	}

	public void shellClosed(ShellEvent event) {		
		invokeMethod("shellClosed", new Object[]{"event", event});
	}

	public void shellDeactivated(ShellEvent event) {
		invokeMethod("shellDeactivated", new Object[]{"event", event});
	}

	public void shellDeiconified(ShellEvent event) {
		invokeMethod("shellDeiconified", new Object[]{"event", event});
	}

	public void shellIconified(ShellEvent event) {
		invokeMethod("shellIconified", new Object[]{"event", event});
	}

}