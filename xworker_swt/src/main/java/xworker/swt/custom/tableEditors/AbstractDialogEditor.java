package xworker.swt.custom.tableEditors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionParams;

public abstract class AbstractDialogEditor extends AbstractTableEditor{
	private static Logger logger = LoggerFactory.getLogger(AbstractDialogEditor.class);
	
	Text text;
	Button openDialogButton;
	Composite composite;
	Listener openDialogListener;
	
	public abstract void openDialog(Event event);
	
	@Override
	public Object create(ActionContext actionContext) {
		Thing compositeThing = World.getInstance().getThing("xworker.swt.custom.tableEditors.DialogEditor/@composite");
		composite = compositeThing.doAction("create", actionContext);
		text = actionContext.getObject("text");
		openDialogButton = actionContext.getObject("openDialogButton");
		
		//处理各种事件
		openDialogListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					openDialog(event);
				}catch(Exception e) {
					logger.error("Open dialog error, path=" + self.getMetadata().getPath(), e);
				}
			}			
		};
		openDialogButton.addListener(SWT.Selection, openDialogListener);
		text.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Text text = (Text) event.widget;				
				
		        if (event.keyCode == SWT.CR || event.keyCode == SWT.TAB) {
		        	ItemEditorUtils.saveValue(item, AbstractDialogEditor.this.getColumn(), text.getText(),
		        			AbstractDialogEditor.this.actionContext);
		        	
		        	//cursorThing.doAction("setValue", parentContext, 
		     	    //        UtilMap.toParams(new Object[]{"item", item, 
		     	    //        		"column", TableEditorUtils.getCursorColumn(cursor), "value", text.getText()}));
		    	    doDispose();
		    	}else if (event.keyCode == SWT.ESC) {
		    		doDispose();
		    		event.doit = false;
		    	}else if(event.keyCode == SWT.ARROW_DOWN || (event.stateMask & SWT.ARROW_DOWN) == SWT.ARROW_DOWN) {
		    		openDialogListener.handleEvent(event);
		    		event.doit = false;
		    	}
			}
			
		});
		return composite;
	}

	@Override
	@ActionParams(names="value")
	public void setValue(Object value, ActionContext actionContext) {
		if(value != null) {
			text.setText(String.valueOf(value));
		}else {
			text.setText("");
		}
		text.selectAll();
		text.forceFocus();
	}

	@Override
	public Object getValue(ActionContext actionContext) {
		return text.getText();
	}

	@Override
	public void doDispose() {
		composite.dispose();
	}

}
