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
package xworker.java.swing.event;

import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingMenuKeyListener extends ThingListener implements MenuKeyListener{
	public ThingMenuKeyListener(Thing thing, ActionContext actionContext){
		super(thing, actionContext);
	}

	@Override
	public void menuKeyTyped(MenuKeyEvent e) {
		doEvent("menuKeyTyped", e);
	}

	@Override
	public void menuKeyPressed(MenuKeyEvent e) {
		doEvent("menuKeyPressed", e);
	}

	@Override
	public void menuKeyReleased(MenuKeyEvent e) {
		doEvent("menuKeyReleased", e);
	}

}