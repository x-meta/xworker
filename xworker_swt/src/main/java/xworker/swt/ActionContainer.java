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
package xworker.swt;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class ActionContainer extends xworker.lang.actions.ActionContainer{
	//private static Logger log = LoggerFactory.getLogger(ActionContainer.class);
	static World world = World.getInstance();
		
	private ActionAsyncContainer asyncContainer; 
	
	public ActionContainer(Thing actions, ActionContext actionContext){
		super(actions, actionContext);
		
		asyncContainer = new ActionAsyncContainer(actions, actionContext);
	}

	public ActionAsyncContainer async(){
		return asyncContainer;
	}	
}