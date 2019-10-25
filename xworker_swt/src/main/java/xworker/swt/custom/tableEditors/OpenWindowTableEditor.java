package xworker.swt.custom.tableEditors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionParams;

@ActionClass(creator="createInstance")
public class OpenWindowTableEditor extends AbstractTableEditor{
	Text text = null;
	Composite rootComposite;
	
	public Object create(ActionContext actionContext){
		Thing compositeThing = World.getInstance().getThing("xworker.swt.custom.tableEditors.PopWindowEditor/@composite");
		rootComposite = compositeThing.doAction("create", actionContext);
		text = actionContext.getObject("text");
		text.addListener(SWT.KeyDown, new TextListener(this));
		text.addListener(SWT.Dispose, new Listener() {

			@Override
			public void handleEvent(Event event) {
				//如果窗口已经打开，关闭
				Shell shell = (Shell) text.getData("shell");
				if(shell != null && !shell.isDisposed()) {
					shell.dispose();
				}
			}			
		});
		
		//Openwindow需要attribute属性
		actionContext.g().put("attribute", this.self);
		return rootComposite;
	}
	
	@ActionParams(names="value")
	public void setValue(Object value, ActionContext actionContext){
		if(value == null) {
			text.setText("");
		}else {
			text.setText(String.valueOf(value));
		}
		text.selectAll();
		text.setFocus();
	}
	
	public Object getValue(ActionContext actionContext){
		return text.getText();
	}
	
	public static OpenWindowTableEditor createInstance(ActionContext actionContext) {
		return ItemEditorUtils.createInstance(OpenWindowTableEditor.class, actionContext);
	}

	@Override
	public void doDispose() {
	
		
		rootComposite.dispose();
	
	}
}
