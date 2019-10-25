package xworker.swt.custom.tableEditors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionParams;
import org.xmeta.util.UtilMap;

import xworker.swt.util.SwtDialog;
import xworker.swt.util.SwtDialogCallback;

@ActionClass(creator="createInstance")
public class DialogEditor extends AbstractTableEditor{
	private static Logger logger = LoggerFactory.getLogger(DialogEditor.class);
	
	Thing shellThing;
	Shell shell;
	SwtDialog dialog;
	Text text;
	Button openDialogButton;
	Composite composite;
	Listener openDialogListener;
	
	public static DialogEditor createInstance(ActionContext actionContext) {
		return ItemEditorUtils.createInstance(DialogEditor.class, actionContext);
	}

	@Override
	public Object create(ActionContext actionContext) {
		shellThing = self.doAction("getShell", actionContext);
		Thing compositeThing = World.getInstance().getThing("xworker.swt.custom.tableEditors.DialogEditor/@composite");
		composite = compositeThing.doAction("create", actionContext);
		text = actionContext.getObject("text");
		openDialogButton = actionContext.getObject("openDialogButton");
		
		//处理各种事件
		openDialogListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					if(shellThing == null) {
						logger.warn("shell thing is null, path=" + self.getMetadata().getPath());
						return;
					}
					
					//避免重复打开
					if(DialogEditor.this.shell != null  && !DialogEditor.this.shell.isDisposed()) {
						DialogEditor.this.shell.setVisible(true);
						return;
					}
					
					Shell parentShell = text.getShell();
					DialogEditor.this.actionContext.peek().put("parent", parentShell);
					ActionContext ac = new ActionContext(DialogEditor.this.actionContext);
					ac.put("parent", parentShell);
					ac.put("text", text);
					ac.put("value", text.getText());
					Shell shell = shellThing.doAction("create", ac);
					dialog = new SwtDialog(parentShell, shell, ac);
					dialog.open(new SwtDialogCallback() {
						@Override
						public void disposed(Object result) {
							if(result != null) {
								text.setText(String.valueOf(result));
								text.forceFocus();
							}
						}						
					});
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
		        	cursorThing.doAction("setValue", parentContext, 
		     	            UtilMap.toParams(new Object[]{"item", item, 
		     	            		"column", ItemEditorUtils.getCursorColumn(cursor), "value", text.getText()}));
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
		if(shell != null && !shell.isDisposed()) {
			shell.dispose();
		}
		
		composite.dispose();
	}
}
