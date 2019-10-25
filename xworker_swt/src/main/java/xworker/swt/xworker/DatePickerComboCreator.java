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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.swt.ActionContainer;
import xworker.swt.design.Designer;
import xworker.swt.events.SwtListener;
import xworker.swt.util.SwtDialog;
import xworker.swt.util.SwtDialogCallback;
import xworker.swt.util.SwtUtils;

public class DatePickerComboCreator {
    @SuppressWarnings("unchecked")
	public static Object create(ActionContext actionContext){
        World world = World.getInstance();
        Thing self = (Thing) actionContext.get("self");
		
		Thing compositeThing = null;
		if(self.getBoolean("BORDER") == true){
		    compositeThing = world.getThing("xworker.swt.xworker.DatePickerCombo/@datePickerComposite");   
		}else{
		    compositeThing = world.getThing("xworker.swt.xworker.DatePickerCombo/@datePickerComposite1");   
		}
		
		//创建SWT控件
		ActionContext ac = new ActionContext(actionContext);
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
		        if(!"Listeners".equals(child.getThingName())){
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
		text.setData("showTime", self.getBoolean("showTime"));
		actionContext.getScope(0).put(self.getString("name"), text);
		
		Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		composite.setData(AttributeEditor.INPUT_CONTROL, text);
		return composite;        
	}
    
    public static void textKeyDown(ActionContext actionContext){
    	Event event = (Event) actionContext.get("event");
    	SwtListener buttonSelection = (SwtListener) actionContext.get("buttonSelection");

    	if (event.keyCode == SWT.ARROW_DOWN) {
    	   buttonSelection.handleEvent(event);
    	}
    }
    
    public static void buttonSelection(ActionContext actionContext){
    	World world = World.getInstance();
    	Event event = (Event) actionContext.get("event");

    	//打开界面
    	Thing shellThing = world.getThing("xworker.swt.xworker.DatePickerCombo/@shell1");
    	ActionContext ac = new ActionContext();
    	ac.put("parent", ((Control) event.widget).getShell());
    	ac.put("text",actionContext.get("text") );
    	Shell newShell = (Shell) shellThing.doAction("create", ac);
    	event.widget.setData("shell", newShell);

    	final Text text = (Text) actionContext.get("text");
    	//Rectangle listRect = text.getBounds();
    	//Composite datePickerComposite = (Composite) actionContext.get("datePickerComposite");
    	Point point = text.getParent().toDisplay(text.getLocation());
    	Point comboSize = text.getSize();
    	
    	SwtUtils.setShellRelateLocation(newShell, point, comboSize);
    	/*
    	//int width = Math.max(comboSize.x, listRect.width + 2);
    	Rectangle shellRect = newShell.getBounds();
    	Monitor monitor = text.getMonitor();
    	if(point.y + shellRect.height > monitor.getClientArea().height){
    	    newShell.setLocation(point.x, point.y - shellRect.height);    
    	}else{
    	    newShell.setLocation(point.x, point.y + comboSize.y);
    	}*/

    	//如果不显示时间，关闭时间
    	if((Boolean) text.getData("showTime") == false){
    	    ((Composite) ac.get("timeComposite")).dispose();
    	    newShell.pack();
    	}

    	String pattern = (String) text.getData("pattern");
    	if(pattern == null || "".equals(pattern)){
    	    pattern = "yyyy-MM-dd";
    	}
    	final SimpleDateFormat sf = new SimpleDateFormat(pattern);
    	if(text.getText() != null){
    	    try{
    	        Date date = sf.parse(text.getText());
    	        ((ActionContainer) ac.get("actions")).doAction("initDateTable", ac, UtilMap.toParams(new Object[]{"date",date}));
    	    }catch(Exception e){
    	    }
    	}
    	

    	SwtDialog dialog = new SwtDialog(text.getShell(), newShell, ac);
    	dialog.open(new SwtDialogCallback() {
			@Override
			public void disposed(Object result) {
				Date date = (Date) result;
				if(date != null){
		    	    text.setText(sf.format(date));    
		    	}
		    	text.setFocus();
			}
    		
    	});
    	/*
    	Date date = (Date) dialog.open();
    	if(date != null){
    	    text.setText(sf.format(date));    
    	}
    	text.setFocus();*/
    }

    public static void textDispose(ActionContext actionContext){
    	/*
    	Composite datePickerComposite = (Composite) actionContext.get("datePickerComposite");
    	
    	//父Composite一起释放
    	if(datePickerComposite != null && !datePickerComposite.isDisposed()){
    	    datePickerComposite.dispose();
    	}*/
    }
    
    public static void goPreMonthButtonAction(ActionContext actionContext){
    	Table dateTable = (Table) actionContext.get("dateTable");
    	
    	GregorianCalendar calendar = new GregorianCalendar();
    	Date date = (Date) dateTable.getData();
    	calendar.setTime(date);
    	calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
    	((ActionContainer) actionContext.get("actions")).doAction("initDateTable", actionContext, UtilMap.toParams(new Object[]{"date", calendar.getTime()}));
    }
    
    public static void yearMonthModify(ActionContext actionContext){
    	Spinner yearText = (Spinner) actionContext.get("yearText");
    	ActionContainer actions = (ActionContainer) actionContext.get("actions");
    	
    	if(actionContext.get("init")  != null &&  (Boolean) actionContext.get("init") == true || "".equals(yearText.getText())){
    	    return;
    	}

    	Combo monthCombo = (Combo) actionContext.get("monthCombo");
    	int year = Integer.parseInt(yearText.getText());
    	int month = monthCombo.getSelectionIndex();

    	GregorianCalendar calendar = new GregorianCalendar();
    	
    	Table dateTable = (Table) actionContext.get("dateTable");
    	Date date = (Date) dateTable.getData();
    	calendar.setTime(date);
    	calendar.set(Calendar.YEAR, year);
    	calendar.set(Calendar.MONTH, month);
    	actions.doAction("initDateTable", actionContext, UtilMap.toParams(new Object[]{"date", calendar.getTime(), "setTextData", false}));
    }
    
    public static void goNextMonthButtonAction(ActionContext actionContext){
    	Table dateTable = (Table) actionContext.get("dateTable");
    	ActionContainer actions = (ActionContainer) actionContext.get("actions");
    	
    	GregorianCalendar calendar = new GregorianCalendar();
    	Date date = (Date) dateTable.getData();
    	calendar.setTime(date);
    	calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
    	actions.doAction("initDateTable", actionContext, UtilMap.toParams(new Object[]{"date", calendar.getTime()}));
    }
    
    public static void dataTableCursorSelection(ActionContext actionContext){
    	Table dateTable = (Table) actionContext.get("dateTable");
    	//ActionContainer actions = (ActionContainer) actionContext.get("actions");
    	TableCursor dateTableCursor = (TableCursor) actionContext.get("dateTableCursor");
    	Event event = (Event) actionContext.get("event");
    	
    	int rowIndex = 0;
    	int column = dateTableCursor.getColumn();
    	TableItem row = dateTableCursor.getRow();
    	for(int i=0; i<dateTable.getItems().length; i++){
    	    if(dateTable.getItems()[i] == row){
    	        rowIndex = i;
    	        break;
    	    }
    	}

    	int[][] itemMonths = (int[][]) dateTable.getData("itemMonths");
    	int addMonth = itemMonths[rowIndex][column];
    	int day = Integer.parseInt(row.getText(column));

    	GregorianCalendar calendar = new GregorianCalendar();
    	Date date = (Date) dateTable.getData();
    	calendar.setTime(date);
    	calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + addMonth);
    	calendar.set(Calendar.DATE, day);

    	actionContext.getScope(0).put("result", calendar.getTime());

    	//直接关闭本窗口swt会报错，启动线程关闭它
    	event.doit = false;
    	final Display display = ((Shell) actionContext.get("pickShell")).getDisplay();
        final Shell parent = (Shell) actionContext.get("parent");
    	new Thread(new Runnable(){
    		public void run(){
    			display.asyncExec(new Runnable(){
    				public void run(){
    					parent.forceActive();
    				}
    			});
    		}
    	}).start();
    }
    
    public static void todayButtonSelection(ActionContext actionContext){
    	ActionContainer actions = (ActionContainer) actionContext.get("actions");
    	actions.doAction("initDateTable", actionContext, UtilMap.toParams(new Object[]{"date", new Date()}));
    }
    
    public static void shellAction(ActionContext actionContext){
    	final Shell pickShell = (Shell) actionContext.get("pickShell");
    	Date result = actionContext.getObject("result");
    	if(SwtUtils.isRWT() && result != null) {    		
    		Text text = actionContext.getObject("text");
    		String pattern = (String) text.getData("pattern");
        	if(pattern == null || "".equals(pattern)){
        	    pattern = "yyyy-MM-dd";
        	}
        	final SimpleDateFormat sf = new SimpleDateFormat(pattern);
        	text.setText(sf.format(result));
    	}
    	if(pickShell != null && !pickShell.isDisposed()){
    		pickShell.getDisplay().asyncExec(new Runnable() {
    			public void run() {
    				try {    					
    					pickShell.dispose();
    				}catch(Exception e) {    					
    				}
    			}
    		});
    	    
    	}
    }
    
    public static void dateTableResized(ActionContext actionContext) {
    	Event event = actionContext.getObject("event");
    	Table table = (Table) event.widget;
    	int width = table.getClientArea().width;
    	if(width > 1000) {
    		//RWT下会突然变大，是不正常的，原因未知，加入此代码可以避免变形
    		return;
    	}
    	
    	int columnWidth = width / 7;
    	for(TableColumn tc : table.getColumns()) {
    		tc.setWidth(columnWidth);
    	}
    }
}