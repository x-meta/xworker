package xworker.swt.custom.tableEditors;

import org.eclipse.swt.widgets.Event;
import org.xmeta.ActionContext;
import org.xmeta.annotation.ActionClass;

import xworker.swt.form.ThingDescriptorForm.FontListener;

@ActionClass(creator="createInstance")
public class FontEditor extends AbstractDialogEditor{

	FontListener fontListener;
	
	@Override
	public void openDialog(Event event) {
		if(fontListener == null) {
			fontListener = new FontListener(text);
		}
		
		fontListener.widgetSelected(null);
	}
	
	public static FontEditor createInstance(ActionContext actionContext) {
		return ItemEditorUtils.createInstance(FontEditor.class, actionContext);
	}
}
