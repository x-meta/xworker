package xworker.app.view.swt.widgets;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.dataObject.DataObject;
import xworker.util.UtilData;

public class DataObjectActions {
	public static void openDataObjectEditor(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Display display = Display.getCurrent();
		if(display == null || display.isDisposed() || display.getActiveShell() == null) {
			throw new ActionException("No active shell, path=" + self.getMetadata().getPath());
		}
		
		Object dataObject = self.doAction("getDataObject", actionContext);
		
		ActionContext ac = new ActionContext();
		ac.put("parent", display.getActiveShell());
		ac.put("parentContext", actionContext);
		ac.put("dataObject", dataObject);
		ac.put("thing", self);
		
		Thing prototype = World.getInstance().getThing("xworker.app.view.swt.widgets.prototype.DataObjectEditDialog");
		Shell shell = prototype.doAction("create", ac);
		shell.setVisible(true);
	}
	
	/**
	 * 打开编辑窗口时根据数据对象设置窗口大小。
	 * @param actionContext
	 */
	public static void openDataObjectEditorInit(ActionContext actionContext) {
		int width = -1;
		int height = -1;
		Object dataObject = actionContext.get("dataObject");
		Thing descriptor = null;
		if(dataObject instanceof Thing) {
			descriptor = (Thing) dataObject;
		}else if(dataObject instanceof DataObject) {
			descriptor = ((DataObject) dataObject).getMetadata().getDescriptor();
		}
		
		if(descriptor != null) {
			width = descriptor.getInt("editDialogWith", -1);
			height = descriptor.getInt("editDialogHeight", -1);
		}
		
		if(width != -1 && height != -1) {
			Shell shell = actionContext.getObject("shell");
			shell.setSize(width, height);
		}
	}
	
	public static void openDataObjectEditorOkButton(ActionContext actionContext) {
		Thing dataObjectForm = actionContext.getObject("dataObjectForm");
		
		ActionContext parentContext = actionContext.getObject("parentContext");
		Thing thing = actionContext.getObject("thing");
		Shell shell = actionContext.getObject("shell");
		
		DataObject dataObject = dataObjectForm.doAction("getDataObject", actionContext);
		if(dataObject != null && UtilData.isTrue(thing.doAction("isUpdate", parentContext))) {		
			dataObject.update(parentContext);
		}
		
		Object result = thing.doAction("ok", parentContext, "dataObject", dataObject);
		if(result != null && UtilData.isTrue(result) == false) {
			return;
		}else {
			shell.dispose();
		}
	}
	
	public static void openDataObjectEditorCancelButton(ActionContext actionContext) {
		Thing dataObjectForm = actionContext.getObject("dataObjectForm");
		
		ActionContext parentContext = actionContext.getObject("parentContext");
		Thing thing = actionContext.getObject("thing");
		Shell shell = actionContext.getObject("shell");
		
		DataObject dataObject = dataObjectForm.doAction("getDataObject", actionContext);
		
		Object result = thing.doAction("cancel", parentContext, "dataObject", dataObject);
		if(result != null && UtilData.isTrue(result) == false) {
			return;
		}else {
			shell.dispose();
		}
	}
}
