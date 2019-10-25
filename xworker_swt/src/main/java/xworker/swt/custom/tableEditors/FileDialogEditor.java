package xworker.swt.custom.tableEditors;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.xmeta.ActionContext;
import org.xmeta.annotation.ActionClass;
import org.xmeta.util.UtilFile;
import org.xmeta.util.UtilString;

@ActionClass(creator="createInstance")
public class FileDialogEditor  extends AbstractDialogEditor{
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
		FileDialog dialog = new FileDialog(text.getShell(), style);
		dialog.setFileName(path);
		if(params != null){
			if(params.get("filterExtensions") != null){
				String filterExtensions[] = UtilString.getStringArray(params.get("filterExtensions"));
				if(filterExtensions != null){
					dialog.setFilterExtensions(filterExtensions);
				}
			}
			
			if(params.get("filterNames") != null){
				String filterNames [] = UtilString.getStringArray(params.get("filterNames"));
				if(filterNames != null){
					dialog.setFilterNames(filterNames);
				}
			}
		}
		
		String fileName = dialog.open();
		if (fileName != null) {
			fileName = UtilFile.toXWorkerFilePath(fileName);

			text.setText(fileName);
		}
	}
	
	public static FileDialogEditor createInstance(ActionContext actionContext) {
		return ItemEditorUtils.createInstance(FileDialogEditor.class, actionContext);
	}

}
