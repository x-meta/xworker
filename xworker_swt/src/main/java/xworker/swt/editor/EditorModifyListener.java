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
package xworker.swt.editor;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TypedEvent;
import org.xmeta.util.ActionContainer;

import xworker.swt.form.SetableModifyListener;

/**
 * 向编辑表单传递的修改事件监听。
 * 
 * @author zyx
 *
 */
public class EditorModifyListener implements ModifyListener, SetableModifyListener{
	//private static final String INIT_KEY = "__xworker.ui.editor__inited__";
	ActionContainer actions ;
	//boolean enable = false;
	//改成ThreadLocale，考虑到在不同的线程的情况
	ThreadLocal<Boolean> enable = new ThreadLocal<Boolean>();
	SelectionListener selectionListener = new SelectionListener(){
		public void widgetDefaultSelected(SelectionEvent event) {
			handlerEvent(event);
		}

		public void widgetSelected(SelectionEvent event) {
			handlerEvent(event);
		}		
	};
	
	protected void handlerEvent(TypedEvent event){
		if(isEnable()){
			actions.doAction("modify", null, "evnet", event);
		}
		/*
		if(enable && (event == null || event.widget == null || event.widget.getData(INIT_KEY) != null)){
			actions.doAction("modify");
		}else if(event != null && event.widget != null && event.widget.getData(INIT_KEY) == null){
			event.widget.setData(INIT_KEY, INIT_KEY);
		}	*/	
	}
	
	public boolean isEnable() {
		Boolean en = enable.get();
		if(en == null) {
			//原先是false，RWT下DataObjectBinder示例不生效，现在改成true, 2019-12-12
			return true;
		}
		return en;
	}

	public void setEnable(boolean enable){
		this.enable.set(enable);		
	}
	
	public EditorModifyListener(ActionContainer actions ){
		this.actions = actions;
	}
	
	public void modifyText(ModifyEvent event) {
		handlerEvent(event);
	}
	
	public SelectionListener getModifySelectionListener(){
		return selectionListener;
	}
}