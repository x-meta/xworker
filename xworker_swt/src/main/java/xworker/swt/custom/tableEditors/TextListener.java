package xworker.swt.custom.tableEditors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.xmeta.util.UtilMap;

public class TextListener implements Listener{
	AbstractTableEditor editor;
	
	public TextListener(AbstractTableEditor editor) {
		this.editor = editor;
	}
	
	@Override
	public void handleEvent(Event event) {   
		Text text = (Text) event.widget;
		
        if (event.character == SWT.CR || event.character == SWT.TAB) {
        	editor.cursorThing.doAction("setValue", editor.parentContext, 
     	            UtilMap.toParams(new Object[]{"item", editor.item, 
     	            		"column", editor.getColumn(), "value", text.getText()}));
    	    editor.doDispose();
    	}
        
    	// close the text editor when the user hits "ESC"
    	if (event.character == SWT.ESC) {
    		editor.doDispose();
    		event.doit = false;
    	}
	}

}
