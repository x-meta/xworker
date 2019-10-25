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
package xworker.swt.xworker;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;
import xworker.swt.events.SwtListener;
import xworker.swt.util.SwtDialog;
import xworker.swt.util.SwtDialogCallback;
import xworker.swt.util.SwtUtils;

public class PopComboCreator {
	private static Logger logger = LoggerFactory.getLogger(PopComboCreator.class);
	
    @SuppressWarnings("unchecked")
	public static Object create(ActionContext actionContext){
        World world = World.getInstance();
        Thing self = (Thing) actionContext.get("self");
		
		Thing compositeThing = null;
		if(self.getBoolean("BORDER") == true){
		    compositeThing = world.getThing("xworker.swt.xworker.PopCombo/@datePickerComposite");   
		}else{
		    compositeThing = world.getThing("xworker.swt.xworker.PopCombo/@datePickerComposite1");   
		}
		
		//创建SWT控件
		ActionContext ac = new ActionContext(actionContext);
		ac.put("parentContext", actionContext);
		ac.put("acContext", actionContext);
		ac.put("winHeight", self.getInt("popWinHeight"));
		ac.put("winWidth", self.getInt("popWinWidth"));
		ac.put("dynamicWinSize", self.getBoolean("dynamicWinSize"));
		ac.getScope(0).put("parent", actionContext.get("parent"));
		//log.info("stackCount=" + ac.getScopes().size());
		Composite composite = null;
		Designer.pushCreator(self);
		try{
			composite = (Composite) compositeThing.doAction("create", ac);
		}finally{
			Designer.popCreator();
		}
		if(self.getBoolean("READ_ONLY")){
		    ((Text) ac.get("text")).setEditable(false);
		}
		try{
		    Bindings bindings = ac.push(null);
		    bindings.put("parent", composite);
		    
		    for(Thing child : self.getAllChilds()){
		        if("Listeners".equals(child.getThingName())){
		            child.doAction("create", ac);
		        }
		    }
		    
		    //事件
		    bindings.put("parent", ac.get("text"));
		    for(Thing child : (java.util.List<Thing>) self.get("Listeners@")){
		        child.doAction("create", ac);
		    }
		}finally{
		    ac.pop();
		}
		
		//把text返回，text中存放composite
		Text text = (Text) ac.get("text");
		text.setData("pattern", self.getString("pattern"));
		text.setData("composite", composite);
		actionContext.getScope(0).put(self.getMetadata().getName(), text);
		
		Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		composite.setData(AttributeEditor.INPUT_CONTROL, text);
		return composite;        
	}
    
    public static void textKeyDown(ActionContext actionContext){
    	Event event = (Event) actionContext.get("event");
    	
    	if (event.keyCode == SWT.ARROW_DOWN) {
    	   ((SwtListener) actionContext.get("buttonSelection")).handleEvent(event);
    	}
    }
    
    public static void buttonSelection(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	Event event = (Event) actionContext.get("event");
    	
    	//打开界面
    	Thing shellThing = null;
    	if(SwtUtils.isRWT()) {
    		shellThing = world.getThing("xworker.swt.xworker.PopCombo/@shell11");
    	}else {
    		shellThing = world.getThing("xworker.swt.xworker.PopCombo/@shell1");
    	}
    	ActionContext ac = new ActionContext();
    	ac.put("text", actionContext.get("text"));
    	ac.put("parent", ((Control) event.widget).getShell());
    	ac.put("parentContext", actionContext);
    	Shell newShell = (Shell) shellThing.doAction("create", ac);
    	
    	event.widget.setData("shell", newShell);

    	//创建窗口中的Composite
    	//log.info("path=" + self.compositePath);
    	Thing compositeThing = self.doAction("getComposite", actionContext);//world.getThing(self.getString("compositePath"));
    	if(compositeThing != null){
    	    ac.peek().put("parent", newShell);
    	    compositeThing.doAction("create", ac);
    	    //System.out.println(control.hashCode() + "layoutData=" +  control.getLayoutData());
    	}
    	newShell.pack();

    	boolean dynamicWinSize = (Boolean) actionContext.get("dynamicWinSize");
    	int winWidth = (Integer) actionContext.get("winWidth");
    	int winHeight = (Integer) actionContext.get("winHeight");
    	final Text text = (Text) actionContext.get("text");
    	
    	if(!dynamicWinSize){
    	    int shellWidth = winWidth;
    	    if(shellWidth <= 0){
    	        shellWidth = text.getParent().getSize().x;
    	    }
    	    int shellHeight = winHeight;
    	    if(shellHeight <= 0){
    	        shellHeight = 100;
    	    }
    	    
    	    newShell.setSize(shellWidth, shellHeight);
    	}

    	Composite comboComposite = (Composite) actionContext.get("comboComposite");
    	//Rectangle listRect = text.getBounds();
    	Point point = comboComposite.toDisplay(text.getLocation());
    	Point comboSize = text.getSize();
    	//int width = Math.max(comboSize.x, listRect.width + 2);
    	Rectangle shellRect = newShell.getBounds();
    	Monitor monitor = text.getMonitor();
    	int clientHeight = monitor.getClientArea().height;
    	int bottomHeight = clientHeight - point.y - shellRect.height - comboSize.y;
    	int topHeight = point.y  - shellRect.height;
    	if(bottomHeight >= 0){
    		//朝下显示
    		newShell.setLocation(point.x, point.y + comboSize.y);    	      
    	}else if(topHeight >= 0){
    		//朝上显示
    		newShell.setLocation(point.x, point.y - shellRect.height);  
    	}else if(bottomHeight > topHeight){
    		//朝下显示，但是要减去超出显示区域的部分
    		newShell.setLocation(point.x, point.y + comboSize.y); 
    		newShell.setSize(shellRect.width, shellRect.height + bottomHeight);
    	}else{
    		//朝下显示，但是要减去超出显示区域的部分
    		newShell.setLocation(point.x, point.y - shellRect.height - topHeight);  
    		newShell.setSize(shellRect.width, shellRect.height + topHeight);
    	}
    	//System.out.println(ac.get("tree"));
    	final Object focusControl = self.doAction("getPopWinFocusControl", ac); 
    	//System.out.println("focusControl=" + focusControl);
    	if(focusControl != null && focusControl instanceof Control) {
    		newShell.addListener(SWT.Activate, new Listener() {
				@Override
				public void handleEvent(Event event) {
					((Control) focusControl).forceFocus();
				}
    			
    		});
    	}

    	SwtDialog dialog = new SwtDialog(text.getShell(), newShell, ac);
    	dialog.open(new SwtDialogCallback() {
			@Override
			public void disposed(Object result) {
				String textStr = (String) result;
		    	if(textStr != null){
		    	    text.setText(textStr);
		    	    text.forceFocus();
		    	}
			}
    		
    	});
    	/*
    	String textStr = (String) dialog.open();
    	if(textStr != null){
    	    text.setText(textStr);
    	    text.setFocus();
    	}*/
    }
    
    public static void textDispose(ActionContext actionContext){
    	/*
    	 这段代码在linux下回产生应用死掉，应该不需要
    	Composite comboComposite = (Composite) actionContext.get("comboComposite");
    	
    	//父Composite一起释放
    	if(!comboComposite.isDisposed()){
    	    comboComposite.dispose();
    	}*/
    }
    
    public static void shellAction(ActionContext actionContext){
    	final Shell shell = (Shell) actionContext.get("shell");
    	shell.setVisible(false);	
    	
    	if(shell != null && !shell.isDisposed()){
   			//shell.dispose();
    	}
    	
    	final Text text = actionContext.getObject("text");
    	//text.getShell().forceActive();   
    	
    	
    	text.getDisplay().asyncExec(new Runnable() {
    		public void run() {
    			try {
	    			shell.dispose();
	    			
	    			if(text.isDisposed() == false) {
	    				text.forceFocus();
	    			}
    			}catch(Exception e) {
    				logger.warn("Text dispose error", e);
    			}
    		}
    	});
    	
    }
    
    public static void shellDispoed(ActionContext actionContext) {
    	final Text text = actionContext.getObject("text");
    	if(text != null && text.isDisposed() == false) {
    		text.forceFocus();
    	}
    }
}