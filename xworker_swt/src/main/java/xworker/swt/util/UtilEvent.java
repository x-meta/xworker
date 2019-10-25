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
package xworker.swt.util;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Event;
import org.xmeta.ActionContext;

public class UtilEvent {
	/**
	 * 由于可能是TypedEvent或Event，而这连个类的基类不同，所以使用此方法取事件中的widget。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object getEventWidget(ActionContext actionContext){
		Object event = actionContext.get("event");
		if(event instanceof Event){
			return ((Event) event).widget;
		}else if(event instanceof TypedEvent){
			return ((TypedEvent) event).widget;
		}else{
			return null;
		}
	}
	
	public static Object getEventItem(ActionContext actionContext){
		Object event = actionContext.get("event");
		if(event instanceof Event){
			return ((Event) event).item;
		}else if(event instanceof SelectionEvent){
			return ((SelectionEvent) event).item;
		}else{
			return null;
		}
	}
}