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

import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ITraverseListener extends BaseListener implements TraverseListener{

	public ITraverseListener(ActionContext actionContext, Thing actionThing){
		super(actionContext, actionThing);
	}
	
	public void keyTraversed(TraverseEvent event) {
		invokeMethod("keyTraversed", new Object[]{"event", event});
	}

}