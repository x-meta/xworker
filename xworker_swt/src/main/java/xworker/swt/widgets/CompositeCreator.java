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
import org.eclipse.swt.widgets.Composite;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class CompositeCreator {
    public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		if(self.getBoolean("BORDER"))
		    style |= SWT.BORDER;
		if(self.getBoolean("H_SCROLL"))
		    style |= SWT.H_SCROLL;
		if(self.getBoolean("V_SCROLL"))
		    style |= SWT.V_SCROLL;        
		if(self.getBoolean("NO_BACKGROUND"))
		    style |= SWT.NO_BACKGROUND;        
		if(self.getBoolean("NO_FOCUS"))
		    style |= SWT.NO_FOCUS;        
		if(self.getBoolean("NO_MERGE_PAINTS"))
		    style |= SWT.NO_MERGE_PAINTS;        
		if(self.getBoolean("NO_REDRAW_RESIZ"))
		    style |= SWT.NO_REDRAW_RESIZE;        
		if(self.getBoolean("NO_RADIO_GROUP"))
		    style |= SWT.NO_RADIO_GROUP;        
		if(self.getBoolean("EMBEDDED"))
		    style |= SWT.EMBEDDED;        
		if(self.getBoolean("DOUBLE_BUFFERED"))
		    style |= SWT.DOUBLE_BUFFERED;
		        
		Composite parent = (Composite) actionContext.get("parent");
		Composite composite = new Composite(parent, style);
		
		//log.info("stackCount=" + actionContext.getScopes().size());
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", composite);
		try{
		    self.doAction("init", actionContext);
		}finally{
		    actionContext.pop();
		}
		
		//变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), composite);
		actionContext.peek().put("parent", composite);
		for(Thing child : self.getChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		Designer.attach(composite, self.getMetadata().getPath(), actionContext);
		return composite;        
	}

    public static void init(ActionContext actionContext){
		Action action = World.getInstance().getAction("xworker.swt.widgets.Control/@actions/@init");
		action.run(actionContext);        
		
		Thing self = (Thing) actionContext.get("self");
		Composite composite = (Composite) actionContext.get("control");
		String backgroundMode = self.getStringBlankAsNull("backgroundMode");
		if(backgroundMode != null){
			if("INHERIT_NONE".equals(backgroundMode)){
				composite.setBackgroundMode(SWT.INHERIT_NONE);
				
			}else if("INHERIT_DEFAULT".equals(backgroundMode)){
				composite.setBackgroundMode(SWT.INHERIT_DEFAULT);
			}else if("INHERIT_FORCE".equals(backgroundMode)){
				composite.setBackgroundMode(SWT.INHERIT_FORCE);
			}
		}
	}

    public static int getStyles(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    			
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		if(self.getBoolean("BORDER"))
		    style |= SWT.BORDER;
		if(self.getBoolean("H_SCROLL"))
		    style |= SWT.H_SCROLL;
		if(self.getBoolean("V_SCROLL"))
		    style |= SWT.V_SCROLL;        
		if(self.getBoolean("NO_BACKGROUND"))
		    style |= SWT.NO_BACKGROUND;        
		if(self.getBoolean("NO_FOCUS"))
		    style |= SWT.NO_FOCUS;        
		if(self.getBoolean("NO_MERGE_PAINTS"))
		    style |= SWT.NO_MERGE_PAINTS;        
		if(self.getBoolean("NO_REDRAW_RESIZE"))
		    style |= SWT.NO_REDRAW_RESIZE;        
		if(self.getBoolean("NO_RADIO_GROUP"))
		    style |= SWT.NO_RADIO_GROUP;        
		if(self.getBoolean("EMBEDDED"))
		    style |= SWT.EMBEDDED;        
		if(self.getBoolean("DOUBLE_BUFFERED"))
		    style |= SWT.DOUBLE_BUFFERED;
		    
		return style;        
	}

    public static void runDesign(ActionContext actionContext){
    	/*
		ActionContext bin = new ActionContext();
		bin.put("parent", Display.getCurrent());
		bin.put("treeItem", ((Tree) actionContext.get("outlineTree")).getSelection()[0]);
		bin.put("treeListener", null);
		Thing designObj = World.getInstance().getThing("core.editor.swt.designer.Designer/@shell");
		Shell shell = (Shell) designObj.doAction("create", bin);
		bin.put("dataObject", actionContext.get("dataObject"));
		bin.refreshSelection.handleEvent(null);
		bin.dataText.setText(dataObject.metadata.path);
		
		shell.open();
		*/        
	}

}