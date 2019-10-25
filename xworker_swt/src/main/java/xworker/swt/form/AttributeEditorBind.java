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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import xworker.swt.ActionContainer;

/**
 * 属性编辑器绑定。
 * 
 * @author zhangyuxiang
 *
 */
public class AttributeEditorBind {
	private static Logger logger = LoggerFactory.getLogger(AttributeEditorBind.class);
	
	/** 数据仓库 */
	Thing dataStore;
	
	/** 绑定到的控件名 */
	String bindTo;
	
	/** 绑定方式 */
	String bindType;
	
	/** DataStore所在的变量上下文 */
	ActionContext ac;
	
	public AttributeEditorBind(Thing dataStore, String bindTo, String bindType, ActionContext ac){
		this.dataStore = dataStore;
		this.bindTo = bindTo;
		this.bindType = bindType;		
		this.ac = ac;
	}
	
	public void bind(final ActionContext actionContext){		
		try{
			final Thing editModel = (Thing) actionContext.get("__editModel__model"); 
			Listener listener = new Listener(){
				Thing editModel_ = editModel;
				ActionContext actionContext_ = actionContext;
				@Override
				public void handleEvent(Event arg0) {
					try{
						Map<String, Object> params = new HashMap<String, Object>();
						params.put("params", editModel_.doAction("getValue", actionContext_));
						if(arg0.type == SWT.Modify){
							params.put((String) arg0.widget.getData("_attributeName"), arg0.data);
						}
						
						dataStore.doAction("load", ac, params);
					}catch(Exception e){
						logger.error("attribute editor store load on bind error", e);
					}
				}				
			};
			
			String[] controls = bindTo.split("[|]");
			for(String controlStr : controls){
				Object control = OgnlUtil.getValue(controlStr + "Input", actionContext);
				Widget widget = null;
				if(control instanceof Widget){
					widget = (Widget) control;					
				}else if(control instanceof ActionContainer){
					ActionContainer cthing = (ActionContainer) control;
					control = cthing.doAction("getControl");
					if(control instanceof Widget){
						widget = (Widget) control;
					}
				}
				if(widget != null){
					widget.setData("_attributeName", controlStr);
					if("selection".equals(bindType)){
						widget.addListener(SWT.Selection, listener);						
					}else{
						widget.addListener(SWT.Modify, listener);
					}
				}
			}
						
		}catch(Exception e){
			logger.error("attribute editor bind error", e);
		}
	}
}