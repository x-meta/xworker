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

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;
import xworker.swt.events.SwtListener;
import xworker.swt.util.SwtDialog;
import xworker.swt.util.SwtDialogCallback;

public class TriggerFieldCreator {
    @SuppressWarnings("unchecked")
	public static Object create(ActionContext actionContext){
        World world = World.getInstance();
        Thing self = (Thing) actionContext.get("self");
		
		Thing compositeThing = world.getThing("xworker.swt.xworker.TriggerField/@datePickerComposite").detach();
		//替换text属性
		Thing textThing = (Thing) compositeThing.get("Text@0");
		textThing.put("READ_ONLY", self.get("READ_ONLY"));
		compositeThing.put("BORDER", self.get("BORDER"));
		
		Thing buttonThing = (Thing) compositeThing.get("Button@0");
		if("popup".equals(self.getString("type"))){ //弹出和下拉的按钮样式不同，一个是向下箭头一个是*号按钮
		    buttonThing.put("type", "SWT.PUSH");
		    buttonThing.put("style", "");
		    buttonThing.put("text", "*");
		}
		
		//创建SWT控件
		ActionContext ac = new ActionContext(actionContext);
		ac.getScope(0).put("parent", actionContext.get("parent"));
		ac.getScope(0).put("thing", self);
		//log.info("stackCount=" + ac.getScopes().size());
		Composite composite = null;
		Designer.pushCreator(self);
		try{
			composite = (Composite) compositeThing.doAction("create", ac);
		}finally{
			Designer.popCreator();
		}
		try{
		    Bindings bindings = ac.push(null);
		    bindings.put("parent", ac.get("composite"));
		    
		    for(Thing child : self.getAllChilds()){
		        if("Listeners".equals(child.getThingName())){
		            child.doAction("create", ac);
		        }
		    }
		    
		    //事件
		    bindings.put("parent", ac.get("text"));
		    for(Thing child : (List<Thing>) self.get("Listeners@")){
		        child.doAction("create", ac);
		    }
		}finally{
		    ac.pop();
		}
		
		//把text返回，text中存放composite
		Text text = (Text) ac.get("hiddenText");
		text.setData("composite", composite);
		((Text) ac.get("text")).setData("composite", composite);
		actionContext.getScope(0).put(self.getString("name"), text);
		Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		composite.setData(AttributeEditor.INPUT_CONTROL, text);
		return composite;        
	}

    public static void createShell(ActionContext actionContext) throws Exception{
		throw new Exception("you should implement this(createShell) method");        
	}

    public static void initValue(ActionContext actionContext){
        /*
        */
    }
    
    public static void textKeyDown(ActionContext actionContext){
    	Event event = (Event) actionContext.get("event");
    	SwtListener buttonSelection = (SwtListener) actionContext.get("buttonSelection");
    	
    	if (event.keyCode == SWT.ARROW_DOWN) {
    	   buttonSelection.handleEvent(event);
    	}
    }
    
    public static void buttonSelection(final ActionContext actionContext){
    	Event event = (Event) actionContext.get("event");
    	
    	final Thing thing = (Thing) actionContext.get("thing");
    	final Text text = (Text) actionContext.get("text");
    	Composite composite = (Composite) actionContext.get("composite");
    	final Text hiddenText = (Text) actionContext.get("hiddenText");
    	
    	//打开界面
    	final ActionContext ac = new ActionContext(actionContext);
    	ac.put("parent", ((Control) event.widget).getShell());
    	Shell newShell = (Shell) thing.doAction("createShell", ac);
    	event.widget.setData("shell", newShell);

    	if("dropdown".equals(thing.getString("type"))){
    	    //Rectangle listRect = text.getBounds();
    	    Point point = composite.toDisplay(text.getLocation());
    	    Point comboSize = text.getSize();
    	    //int width = Math.max(comboSize.x, listRect.width + 2);
    	    Rectangle shellRect = newShell.getBounds();
    	    Monitor monitor = text.getMonitor();
    	    if(point.y + shellRect.height > monitor.getClientArea().height){
    	        newShell.setLocation(point.x, point.y - shellRect.height);    
    	    }else{
    	        newShell.setLocation(point.x, point.y + comboSize.y);
    	    }
    	}


    	SwtDialog dialog = new SwtDialog(text.getShell(), newShell, ac);
    	dialog.open(new SwtDialogCallback() {
			@Override
			public void disposed(Object r) {
				String result = (String) r;
		    	String resultLabel = (String) ac.get("resultLabel");
		    	if(result == null){
		    	    result = "";
		    	}
		    	if(resultLabel == null){
		    	    resultLabel = "";
		    	}

		    	try{
		    	    actionContext.getScope(0).put(thing.getString("name") + "_FromShell", true);  
		    	    if(thing.getBoolean("valueLabelSplit") == false){
		    	        //如果不是值标签分离的，Model等的设置值同步到到显示的text
		    	        text.setText(result);
		    	        hiddenText.setText(result);
		    	    }else{
		    	        //值和标签是分离的，初始化标签数据
		    	        text.setText(resultLabel);
		    	        hiddenText.setText(result);
		    	    }
		    	}finally{
		    	    actionContext.getScope(0).put(thing.getString("name") + "_FromShell", null);  
		    	}
		    	text.setFocus();
			}
    		
    	});
    	
    }
    
    public static void textDispose(ActionContext actionContext){
    	/*
    	Composite composite = (Composite) actionContext.get("composite");
    	
    	//父Composite一起释放
    	if(!composite.isDisposed()){
    	    composite.dispose();
    	}*/
    }
    
    public static void hiddenTextModify(ActionContext actionContext){
    	Thing thing = (Thing) actionContext.get("thing");
    	String name = thing.getString("name");
    	Text text = (Text) actionContext.get("text");
    	Text hiddenText = (Text) actionContext.get("hiddenText");
    	
    	//text和hiddenText相互会影响，为避免递归操作
    	if(actionContext.get(name + "_FromText") == null && actionContext.get(name + "_FromShell") == null){
    	    try{
    	        actionContext.getScope(0).put(name + "_FromHidden", true);  
    	        if(thing.getBoolean("valueLabelSplit") == false){
    	            //如果不是值标签分离的，Model等的设置值同步到到显示的text
    	            text.setText(hiddenText.getText());
    	        }else{
    	            //值和标签是分离的，初始化标签数据
    	            thing.doAction("initValue", actionContext);
    	        }
    	    }finally{
    	        actionContext.getScope(0).put(name + "_FromHidden", null);  
    	    }
    	}
    }
    
    public static void textModify(ActionContext actionContext){
    	Thing thing = (Thing) actionContext.get("thing");
    	String name = thing.getString("name");
    	Text text = (Text) actionContext.get("text");
    	Text hiddenText = (Text) actionContext.get("hiddenText");
    	
    	//text和hiddenText相互会影响，为避免递归操作
    	if(actionContext.get(name + "_FromHidden") == null && actionContext.get(name + "_FromShell") == null){
    	    try{
    	        actionContext.getScope(0).put(name + "_FromText", true);  
    	        if(thing.getBoolean("READ_ONLY") == false){
    	            //如果不是只读的，当用户修改了text的值时同步到隐藏输入
    	            hiddenText.setText(text.getText());
    	        }
    	    }finally{
    	        actionContext.getScope(0).put(name + "_FromText", null);  
    	    }
    	}
    }
}