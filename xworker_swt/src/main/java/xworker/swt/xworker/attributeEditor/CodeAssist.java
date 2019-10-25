package xworker.swt.xworker.attributeEditor;

import java.util.List;

import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;

import xworker.swt.util.SwtTextUtils;
import xworker.swt.xworker.CodeAssistor;
import xworker.swt.xworker.CodeAssitContent;

public class CodeAssist {
	public static void textModify(ActionContext actionContext){
		Event event = actionContext.getObject("event");
		
		final Control text = (Control) event.widget;
		final CodeAssistor assistor = actionContext.getObject("assistor");
		final List<CodeAssitContent> contents = actionContext.getObject("contents");
		
		assistor.setDelayAction(new Runnable(){
			public void run(){
				 assistor.showContents(SwtTextUtils.getText(text), contents);
			}
		});		   
	}
	
	public static void textDefaultSelection(ActionContext actionContext) {
		Table table = actionContext.getObject("table");
		TableItem[] items = table.getSelection();
		Text filterText = actionContext.getObject("filterText");
		final Control text = actionContext.getObject("text");
		
		String value = "";
		if(items == null || items.length == 0){
		    value = filterText.getText().trim();
		}else{
			CodeAssitContent data = (CodeAssitContent) items[0].getData();
		    value = data.value.trim();
		}

		Point selection = SwtTextUtils.getSelection(text);
		int offset = SwtTextUtils.getCaretOffset(text) - (selection.y - selection.x);
		SwtTextUtils.insert(text, value);
		//text.insert(value);
		if(SwtTextUtils.getCaretOffset(text) == offset){
		    SwtTextUtils.setCaretOffset(text, offset + value.length());
		}

		final CodeAssistor assistor = actionContext.getObject("assistor");
		assistor.checkInput(text, offset, offset + value.length());

		Shell shell = actionContext.getObject("shell");
		shell.setVisible(false);
		text.getDisplay().asyncExec(new Runnable(){
			public void run() {
				text.setFocus();
			}
		});
	}
	
	public static void textKeyDown(ActionContext actionContext) {
		Table table = actionContext.getObject("table");
		Event event = actionContext.getObject("event");
		
		int index = table.getSelectionIndex();

		if(event.keyCode == 16777218){
		    if(index >= 0){
		        index = index + 1;
		    }else{
		        index = 0;
		    }
		    
		    if(index > table.getItems().length - 1){
		        index = 0;
		    }
		}else if(event.keyCode == 16777217){
		    if(index > 0){
		        index = index - 1;
		    }else{
		        index = table.getItems().length - 1;
		    }    
		}

		if(table.getItems().length > 0){
		    table.setSelection(index);
		}
	}
	
	public static void tableSelection(ActionContext actionContext) {
		Event event = actionContext.getObject("event");
		final Control text = actionContext.getObject("text");
		CodeAssitContent data = (CodeAssitContent) event.item.getData();
		
		String value = data.value;
		int offset = SwtTextUtils.getCaretOffset(text);
		SwtTextUtils.insert(text, value);

		if(SwtTextUtils.getCaretOffset(text) == offset){
		    SwtTextUtils.setCaretOffset(text, offset + value.length());
		}

		Shell shell = actionContext.getObject("shell");
		shell.setVisible(false);
		text.getDisplay().asyncExec(new Runnable(){
			public void run() {
				text.setFocus();
			}
		});
	}
	
	public static void shellClosed(ActionContext actionContext) {		
		ShellEvent event = actionContext.getObject("event");
		final Control text = actionContext.getObject("text");
		
		((Shell) event.widget).setVisible(false);
		event.doit = false;

		text.setFocus();
	}
}
