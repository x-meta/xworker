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
package xworker.swt.widgets;

import org.eclipse.swt.widgets.Display;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class DisplayCreator {
	public static Object create(ActionContext actionContext) {
		Thing self = (Thing) actionContext.get("self");

		Display display = Display.getCurrent();
		if (display == null) {
			display = new Display();
		}

		// 保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), display);
		actionContext.peek().put("parent", display);
		for (Thing child : self.getAllChilds()) {
			child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");

		return display;
	}

	public static void asyncExec(final ActionContext actionContext) {
		// display
		Display display = (Display) actionContext.get("display");
		if (display == null) {
			display = Display.getCurrent();
		}

		// runable
		World world = World.getInstance();
		Thing actionThing = null;
		String actionMethod = (String) actionContext.get("method");
		Runnable runnable = null;
		Object thing = actionContext.get("action");
		if (thing == null) {
			return;
		} else if (thing instanceof String) {
			thing = world.getThing("thingPath");
		} else if (thing instanceof Thing) {
			actionThing = (Thing) thing;
		} else if (thing instanceof Runnable) {
			runnable = (Runnable) thing;
		}

		if (actionMethod == null || actionMethod == "") {
			actionMethod = "run";
		}

		// doasync
		if (runnable == null) {
			final Thing aThing = actionThing;
			final String aMethod = actionMethod;
			runnable = new Runnable() {
				public void run() {
					aThing.doAction(aMethod, actionContext);
				}
			};
		}
		display.asyncExec(runnable);
	}

	public static void syncExec(final ActionContext actionContext) {
		// display
		Display display = (Display) actionContext.get("display");
		if (display == null) {
			display = Display.getCurrent();
		}

		// runable
		World world = World.getInstance();
		Thing actionThing = null;
		String actionMethod = (String) actionContext.get("method");
		Runnable runnable = null;
		Object thing = actionContext.get("action");
		if (thing == null) {
			return;
		} else if (thing instanceof String) {
			thing = world.getThing("thingPath");
		} else if (thing instanceof Thing) {
			actionThing = (Thing) thing;
		} else if (thing instanceof Runnable) {
			runnable = (Runnable) thing;
		}

		if (actionMethod == null || actionMethod == "") {
			actionMethod = "run";
		}

		// doasync
		if (runnable == null) {
			final Thing aThing = actionThing;
			final String aMethod = actionMethod;
			runnable = new Runnable() {
				public void run() {
					aThing.doAction(aMethod, actionContext);
				}
			};
		}
		display.syncExec(runnable);
	}	    
}