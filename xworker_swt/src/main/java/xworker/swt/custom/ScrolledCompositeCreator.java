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
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;

public class ScrolledCompositeCreator {
    public static Object create(ActionContext actionContext){
        World world = World.getInstance();
        Thing self = (Thing) actionContext.get("self");
		
		Action styleAction = world.getAction("xworker.swt.widgets.Composite/@scripts/@getStyles");
		int style = (Integer) styleAction.run(actionContext);
		        
		Composite parent = (Composite) actionContext.get("parent");
		ScrolledComposite composite = new ScrolledComposite(parent, style);
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", composite);
		bindings.put("self", self);
		try{
		    Action initAction = world.getAction("xworker.swt.widgets.Control/@actions/@init");
		    initAction.run(actionContext);
		}finally{
		    actionContext.pop();
		}
		
		composite.setAlwaysShowScrollBars(self.getBoolean("alwaysShowScrollBars"));
		composite.setExpandHorizontal(self.getBoolean("expandHorizontal"));
		composite.setExpandVertical(self.getBoolean("expandVertical"));
		int minHeight = self.getInt("minHeight", 0);
		int minWidth = self.getInt("minWidth", 0);
		Point size = null;
		if(minHeight > 0){
			size = new Point(minWidth, minHeight);
		}
		/*
		composite.setMinHeight(self.getInt("minHeight", 0));
		composite.setMinWidth(self.getInt("minWidth", 0));
		*/
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), composite);
		actionContext.peek().put("parent", composite);
		for(Thing child : self.getAllChilds()){
		    Object obj = child.doAction("create", actionContext);
		    if(obj instanceof Control){
		    	Control childWidget = (Control) obj;
		    	composite.setContent(childWidget);
		    	if(size != null){
		    		childWidget.setSize(size);
		    		composite.setMinSize(size);
		    	}else{
		    		size = childWidget.getSize();
		    		if(size.x == -1){
			    		childWidget.setSize(childWidget.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			    		composite.setMinSize(childWidget.getSize());
		    		}else{
		    			composite.setMinSize(size);
		    		}
		    	}
		    }
		}
		actionContext.peek().remove("parent");
		
		//自动调整子控件大小的监听
		if(self.getBoolean("autoResize")){
			new ResizeListener(composite);
		}
		
		Designer.attach(composite, self.getMetadata().getPath(), actionContext);
		return composite;       
	}

    public static class ResizeListener implements Listener{
    	ScrolledComposite composite;
    	
    	public ResizeListener(ScrolledComposite composite){
    		this.composite = composite;
    		this.composite.addListener(SWT.Resize, this);
    	}
    	
		@Override
		public void handleEvent(Event arg0) {
			Point size = composite.getSize();
			int width = 0;
			int height = 0;
			for(Control control : composite.getChildren()){
				Point controlSize = control.computeSize(SWT.DEFAULT , SWT.DEFAULT); 
				control.setSize(controlSize);
				if(control instanceof Composite){
					//((Composite) control).layout();
				}
				if(controlSize.x > width){
					width = controlSize.x;
				}
				height += controlSize.y;
			}
			composite.setMinWidth(width);
			composite.setMinHeight(height);
			composite.layout();
		}
    	
    }
}