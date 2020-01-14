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
package xworker.swt.form;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;

import xworker.swt.editor.EditorModifyListener;

/**
 * 表单中的控件的修改监听，触发表单的事件或属性的事件。
 * 
 * @author zhangyuxiang
 *
 */
public class FormModifyListener implements ModifyListener, SetableModifyListener{
	private static Logger logger = LoggerFactory.getLogger(FormModifyListener.class);
	
	/** 原始监听器 */
	ModifyListener listener;
	
	/** 表单事物 */
	Thing formThing;
	
	/** 要监听的属性 */
	Thing attribute;
	
	/** 变量上下文 */
	ActionContext context;
	
	public FormModifyListener(Thing formThing, Thing attribute, ActionContext context, ModifyListener listener){
		this.formThing = formThing;
		this.attribute = attribute;
		this.context = context;
		this.listener = listener;
	}

	@Override
	public void modifyText(ModifyEvent event) {
		if(!isEnable()){
			return;
		}
		if(listener != null){
			listener.modifyText(event);
		}

		Display.getCurrent().asyncExec(new Runnable(){
			public void run(){
				
				doModifyLast();
			}
		});
	}
	
	/**
	 * 如果在Modify事件中执行，那么当前编辑控件有可能是取不到值的，因此安排到后续执行。
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void doModifyLast(){
		boolean enabled = this.isEnable();
		try{
			this.setEnable(false);
			//表单、属性和联动等
			String formModifyAction = formThing.getString("modifyAction");
			String attributeModifyAction = attribute.getString("modifyAction");
			String modifyStoreListener = attribute.getString("modifyStoreListener");		
			if(UtilString.isNotNull(formModifyAction) || UtilString.isNotNull(attributeModifyAction) || UtilString.isNotNull(modifyStoreListener)){
				//取表单中的值
				Object data = ((Thing) context.get("model")).doAction("getValue", context);
				
				try{
					Bindings bindings = context.push();
					bindings.put("data", data);
					if(context.get("params") == null){
						bindings.put("params", data);
					}else{
						Map baseParams = (Map) context.get("params");
						Map newParams = new HashMap();
						newParams.putAll(baseParams);
						newParams.putAll((Map) data);
						bindings.put("params", newParams);
					}
				
					if(UtilString.isNotNull(formModifyAction)){
						formThing.doAction(formModifyAction, context);
					}
					
					if(UtilString.isNotNull(attributeModifyAction)){
						attribute.doAction(attributeModifyAction, context);
					}
					
					if(UtilString.isNotNull(modifyStoreListener)){
						for(String attr : modifyStoreListener.split("[,]")){
							Object store = context.get(attr + "Store");							
							if(store != null && store instanceof Thing){								
								((Thing) store).doAction("load", context);
							}
						}
					}
				}finally{
					context.pop();
				}
			}
		}catch(Exception e){
			logger.error("Form modify event error, formPath=" + formThing.getMetadata().getPath(), e);
		}finally {
			this.setEnable(enabled);
		}
	}

	public SelectionListener getSelectionListener(){
		if(listener instanceof EditorModifyListener){
			return ((EditorModifyListener) listener).getModifySelectionListener();
		}else{
			SelectionListener selectionListener = new SelectionListener(){
				public void widgetDefaultSelected(SelectionEvent event) {
					ModifyEvent mevent = selectionEventToModifyEvent(event);
					modifyText(mevent);
				}

				public void widgetSelected(SelectionEvent event) {
					ModifyEvent mevent = selectionEventToModifyEvent(event);
					modifyText(mevent);
				}		
			};
			return selectionListener;
		}
	}
	
	private ModifyEvent selectionEventToModifyEvent(SelectionEvent event) {
		Event ev = new Event();
		ev.widget = event.widget;
		ev.data = event.data;
		ev.display = event.display;
		ev.time = event.time;
		ev.widget = event.widget;
		
		ModifyEvent mevent = new ModifyEvent(ev);		
		return mevent;
	}
	
	public boolean isEnable() {
		if(listener instanceof EditorModifyListener){
			return ((EditorModifyListener) listener).isEnable(); 
		}else{
			return true;
		}
	}

	public void setEnable(boolean enable){
		if(listener instanceof EditorModifyListener){
			((EditorModifyListener) listener).setEnable(enable);
		}
	}
}