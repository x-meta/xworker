package xworker.swt.custom.tableEditors;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.xmeta.ActionContext;
import org.xmeta.annotation.ActionClass;
import org.xmeta.util.UtilFile;
import org.xmeta.util.UtilString;

@ActionClass(creator="createInstance")
public class DirectoryEditor  extends AbstractDialogEditor{
	@Override
	public void openDialog(Event event) {
		String path = text.getText();
		String paramsStr = self.getStringBlankAsNull("inputattrs");
		Map<String, String> params = UtilString.getParams(paramsStr);
		int style = SWT.OPEN;
		if(params != null){
			String type = params.get("type");
			if("OPEN".equals(type)){
				style = SWT.OPEN;
			}else if("SAVE".equals(type)){
				style = SWT.SAVE;
			}else if("MULTI".equals(type)){
				style = SWT.MULTI;
			}
		}
		DirectoryDialog dialog = new DirectoryDialog(text.getShell(), style);
		dialog.setFilterPath(path);
		
		String fileName = dialog.open();
		if (fileName != null) {
			fileName = UtilFile.toXWorkerFilePath(fileName);

			text.setText(fileName);
		}
	}
	
	public static DirectoryEditor createInstance(ActionContext actionContext) {
		return ItemEditorUtils.createInstance(DirectoryEditor.class, actionContext);
	}

}
