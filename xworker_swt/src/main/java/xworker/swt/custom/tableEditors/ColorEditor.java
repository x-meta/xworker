package xworker.swt.custom.tableEditors;

import org.eclipse.swt.widgets.Event;
import org.xmeta.ActionContext;
import org.xmeta.annotation.ActionClass;

import xworker.swt.form.ThingDescriptorForm.ColorListener;

@ActionClass(creator="createInstance")
public class ColorEditor extends AbstractDialogEditor{

	ColorListener fontListener;
	
	@Override
	public void openDialog(Event event) {
		if(fontListener == null) {
			fontListener = new ColorListener(text);
		}
		
		fontListener.widgetSelected(null);
	}
	
	public static ColorEditor createInstance(ActionContext actionContext) {
		return ItemEditorUtils.createInstance(ColorEditor.class, actionContext);
	}

}
