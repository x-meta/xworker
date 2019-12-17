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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;

public class ExpandBarCreator {
    public static Object create(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
		Action styleAction = world.getAction("xworker.swt.widgets.Composite/@scripts/@getStyles");
		int style = (Integer) styleAction.run(actionContext);
		
		if(self.getBoolean("BORDER"))
		    style |= SWT.BORDER;
		if(self.getBoolean("V_SCROLL"))
		    style |= SWT.V_SCROLL;
		
		Composite parent = (Composite) actionContext.get("parent");
		ExpandBar bar = new ExpandBar(parent, style);
		
		if(self.getStringBlankAsNull("spacing") != null) {
			bar.setSpacing(self.getInt("spacing"));
		}
		
		bar.addExpandListener(new ExpandBarAutoSetHeightListener(self, actionContext));
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", bar);
		bindings.put("self", self);
		try{
		    Action initAction = world.getAction("xworker.swt.widgets.Control/@actions/@init");
		    initAction.run(actionContext);
		}finally{
		    actionContext.pop();
		}
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), bar);
		actionContext.peek().put("parent", bar);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		Designer.attach(bar, self.getMetadata().getPath(), actionContext);
		return bar;        
	}
    
    /**
     * 计算Bar的高度，设置到Item上。
     * @author zhangyuxiang
     *
     */
    static class ExpandBarHeightSetter{
    	ExpandItem item;
    	ExpandBar bar;
    	
    	public ExpandBarHeightSetter(ExpandItem item, ExpandBar bar) {
    		this.item = item;
    		this.bar = bar;
    		
    		if(bar != null && !bar.isDisposed()) {
    			bar.getDisplay().asyncExec(new Runnable() {
    				public void run() {
    					try {
    						int height = 0;
    						for(ExpandItem citem : ExpandBarHeightSetter.this.bar.getItems()) {
    							if(citem.getExpanded() == false) {
    								height += citem.getHeaderHeight() + ExpandBarHeightSetter.this.bar.getSpacing();
    							}else {
    								height += citem.getHeaderHeight() + citem.getHeight() + ExpandBarHeightSetter.this.bar.getSpacing();
    							}
    						}
    						height = height + 1;
    						ExpandBarHeightSetter.this.item.setHeight(height);
    					}catch(Exception e) {
    						
    					}
    				}
    			});
    		}
    	}
    }
    
    static class ExpandBarAutoSetHeightListener implements ExpandListener {
    	Thing thing;
    	ActionContext actionContext;
    	
    	public ExpandBarAutoSetHeightListener(Thing thing, ActionContext actionContext) {
    		this.thing = thing;
    		this.actionContext = actionContext;
    	}
    	
		@Override
		public void itemCollapsed(ExpandEvent e) {
			if(thing.getBoolean("onlyOneItem") == false) {
				final ExpandItem item = (ExpandItem) e.item;
				final Control control = item.getControl();
				
				if(control.getParent().getParent() instanceof ExpandBar) {
					ExpandBar bar = (ExpandBar) control.getParent();
					ExpandItem parentItem = (ExpandItem) control.getParent().getData("__ExpandBarItem__");
					new ExpandBarHeightSetter(parentItem, bar);
				}
			}
		}

		@Override
		public void itemExpanded(ExpandEvent e) {
			ExpandItem item = (ExpandItem) e.item;
			Control control = item.getControl();
			
			if(thing.getBoolean("onlyOneItem")) {
				//每次只设置一个，把控件的大小设置为最大
				ExpandBar bar = item.getParent();
				int height = (bar.getItemCount() - 1) * bar.getSpacing();
				for(ExpandItem citem : bar.getItems()) {
					height += citem.getHeaderHeight();
					if(citem != item && citem.getExpanded()) {
						citem.setExpanded(false);
					}
				}
				
				height = bar.getSize().y - height - bar.getSpacing() - 1;
				control.setSize(control.getSize().x, height);	
				item.setHeight(height);
				//item.getParent().getParent().update();
			}else {
				if(control.getParent().getParent() instanceof ExpandBar) {
					ExpandBar bar = (ExpandBar) control.getParent();
					ExpandItem parentItem = (ExpandItem) control.getParent().getData("__ExpandBarItem__");
					
					new ExpandBarHeightSetter(parentItem, bar);
				}
			}
			
			
		}
    	
    }
}