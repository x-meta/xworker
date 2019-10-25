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
package xworker.app.view.swt.data;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

/**
 * 如果DataStore绑定到了一个控件，当控件dispose时通过此方法取消监听绑定。
 * 
 * @author Administrator
 *
 */
public class DataStoreDisposeListener implements Listener{
	static private final String  KEY = "__DataStoreDisposeListener__key__";
	static DataStoreDisposeListener instance = new DataStoreDisposeListener();
	
	/**
	 * 绑定到一个控件上。
	 * 
	 * @param widget
	 */
	public static void attach(Widget widget){
		if(widget.getData(KEY) == null){
			widget.addListener(SWT.Dispose, instance);
			widget.setData(KEY, KEY);
		}
	}
	
	@Override
	public void handleEvent(Event event) {
		if(event.widget != null){
			try{
				Thing store = (Thing) event.widget.getData(DataStore.STORE);
				Thing listener = (Thing) event.widget.getData(DataStore.LISTENER);
				
				//移除监听
				if(store != null && listener != null){
					store.doAction("removeListener", UtilMap.toMap(new Object[]{"listener", listener}));
				}
			}catch(Exception e){				
			}
		}
	}
	
	

}