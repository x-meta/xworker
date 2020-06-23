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
package xworker.swt.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.UtilSwt;
import xworker.util.UtilData;

public class CTabFolderCreator {
	private static Logger logger = LoggerFactory.getLogger(CTabFolderCreator.class);
	
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		String sty = self.getString("style");
		if("TOP".equals(sty)){
			style |= SWT.TOP;
		}else if("BOTTOM".equals(sty)){
			 style |= SWT.BOTTOM;
		}
		
		if("true".equals(self.getString("BORDER")))
		    style |= SWT.BORDER;
		if("true".equals(self.getString("FLAT")))
		    style |= SWT.FLAT;
		if("true".equals(self.getString("CLOSE")))
		    style |= SWT.CLOSE;
		if("true".equals(self.getString("SINGLE")))
			style |= SWT.SINGLE;					
		    
		CTabFolder tab = new CTabFolder((Composite) actionContext.get("parent"), style);
		
		//增加延迟加载栏目的功能
		tab.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				CTabItem item = (CTabItem) event.item;
				Thing itemThing = World.getInstance().getThing((String) item.getData(Designer.DATA_THING));
				if(itemThing == null) {
					return;
				}
				
				try {
					String key = "__ctabfoler_item_delay_reloaded__";					
					if(!itemThing.getBoolean("delayReload") || UtilData.isTrue(item.getData(key))) {
						//已经加载了返回
						return;
					}					
					
					if(!itemThing.getBoolean("delayReload")) {
						//不需要延迟加载
						return;
					}
					ActionContext actionContext = (ActionContext) item.getData(Designer.DATA_ACTIONCONTEXT);
					actionContext.peek().put("parent", item.getParent());
					
					//创建子节点
					for(Thing child : itemThing.getAllChilds()){
			            Control control = (Control) child.doAction("create", actionContext);
			            item.setControl(control);
			            //应该只有一个控件节点
			            break;
			        }
					
					item.setData(key, true);
				}catch(Exception e) {
					logger.error("Delay reload item error, path=" + itemThing.getMetadata().getPath(), e);
				}
			}
		});
		tab.addListener(SWT.Selection, new CTabFolderSelection(self, tab, actionContext));
		
		if(self.getBoolean("MRUVisible"))
			tab.setMRUVisible(true);
		
		if(self.getBoolean("simple") && !SwtUtils.isRWT()) {
			tab.setSimple(true);
		}
		
		Color selectionBackground = UtilSwt.createColor(tab, self.getString("selectionBackground"), actionContext);
		Color selectionForeground = UtilSwt.createColor(tab, self.getString("selectionForeground"), actionContext);
		if(selectionBackground != null) tab.setSelectionBackground(selectionBackground);
		if(selectionForeground != null) tab.setSelectionForeground(selectionForeground);
		
		Bindings bindings = actionContext.push(null);
		bindings.put("control", tab);
		bindings.put("self", self);
		try{
		    Action initAction = World.getInstance().getAction("xworker.swt.widgets.Control/@actions/@init");
		    initAction.run(actionContext);
		}finally{
		    actionContext.pop();
		}
		
		actionContext.getScope(0).put(self.getString("name"), tab);
		
		//ActionContext newContext = new ActionContext(actionContext.getScope(0));
		CTabItem  firstItem = null;
		actionContext.peek().put("parent", tab);
		for(Thing child : self.getAllChilds()){ 
		    Object obj = child.doAction("create", actionContext);
		    if(firstItem == null && obj instanceof CTabItem){
		    	firstItem = (CTabItem) obj;
		    }
		}

		if(firstItem != null){
			tab.setSelection(firstItem);
			Event event = new Event();
			event.item = firstItem;
			tab.notifyListeners(SWT.Selection, event);
			//tab.showItem(firstItem);					
		}
		
		//关闭item同时销毁子控件的选项
		if(self.getBoolean("disposeControlOnItemClosed")){
			tab.addCTabFolder2Listener(new CTabFolder2Listener(){

				@Override
				public void close(CTabFolderEvent event) {
					CTabItem item = (CTabItem) event.item;
					if(item.getControl() != null){
						item.getControl().dispose();
					}
					item.dispose();					
				}

				@Override
				public void maximize(CTabFolderEvent event) {
				}

				@Override
				public void minimize(CTabFolderEvent event) {
				}

				@Override
				public void restore(CTabFolderEvent event) {
				}

				@Override
				public void showList(CTabFolderEvent event) {
				}
				
			});
		}
		
		Designer.attach(tab, self.getMetadata().getPath(), actionContext);
		
		return tab;
    }

    public static void createTopRight(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");
    	Thing parentThing = self.getParent();
    	if(parentThing.getBoolean("useItemTopRightControl")) {
    		//使用IItem的
    		return;
    	}
    	
    	int style = SWT.NONE;
    	String s = self.getStringBlankAsNull("style");
    	if("FILL".equals(s)){
    		style = style | SWT.FILL;
    	}else if(self.getBoolean("WRAP")) {
    		style = style | SWT.RIGHT | SWT.WRAP;
    	}else {
    		style = style | SWT.RIGHT;
    	}
    	
    	CTabFolder parent = actionContext.getObject("parent");
    	for(Thing child : self.getChilds()) {
    		Object obj = child.doAction("create", actionContext);
    		if(obj instanceof Control) {
    			parent.setTopRight((Control) obj, style);
    			return;
    		}
    	}
    }
    
    public static class CTabFolderSelection implements Listener{
    	Thing tabThing;
    	CTabFolder tabFolder;
    	ActionContext actionContext;
    	
    	public CTabFolderSelection(Thing tabThing, CTabFolder tabFolder, ActionContext actionContext) {
    		this.tabThing = tabThing;
    		this.tabFolder = tabFolder;
    		this.actionContext = actionContext;
    	}
    	
		@Override
		public void handleEvent(Event event) {
			Thing itemThing = Designer.getThing(event.item);
			if(itemThing == null) {
				return;
			}
			
			itemThing.doAction("onSelection", actionContext);
			
			if(tabThing.getBoolean("useItemTopRightControl")) {
				int style = SWT.NONE;
				Thing topRight = tabThing.getThing("TopRight@0");
				if(topRight != null) {					
			    	String s = topRight.getStringBlankAsNull("style");
			    	if("FILL".equals(s)){
			    		style = style | SWT.FILL;
			    	}else if(topRight.getBoolean("WRAP")) {
			    		style = style | SWT.RIGHT | SWT.WRAP;
			    	}else {
			    		style = style | SWT.RIGHT;
			    	}
				}else {
					style = SWT.RIGHT;
				}
				
				Control old = tabFolder.getTopRight();
				if(old != null && old.isDisposed() == false) {
					//当新的control为null时，如果不把旧的设置为不可见，那么topRight还是会显示出来
					old.setVisible(false);
				}
				Control control = itemThing.doAction("getTopRightControl", actionContext, "parent", tabFolder, "event", event);
				if(control != null && control.isDisposed() == false) {
					control.setVisible(true);
					tabFolder.setTopRight(control);				
				}else {
					Thing topRightThing = itemThing.doAction("getTopRightControlThing", actionContext);
					if(topRightThing != null) {
						control= topRightThing.doAction("create", actionContext, "parent", tabFolder, "event", event);
						if(control != null) {
							event.item.setData(CTabFolderCTabItemCreator.TOPRIGHT, control);
							control.setVisible(true);
							tabFolder.setTopRight(control);	
						}
					}
				}
			}
			
		}
    	
    }
}
	