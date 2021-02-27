package xworker.app.view.swt.data.dialogs;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.app.view.swt.data.DataStore;
import xworker.dataObject.DataObject;
import xworker.lang.executor.Executor;
import xworker.swt.util.SwtUtils;

public class NewDataObjectDialog {
	private static final String TAG = NewDataObjectDialog.class.getName();
	
	public static void okButtonAction(ActionContext actionContext){
		Thing form = actionContext.getObject("form");
		Boolean confirm = actionContext.getObject("confirm");
		String message = actionContext.getObject("message");
		Shell shell = actionContext.getObject("shell");
		Boolean insert = actionContext.getObject("insert");
		Object ds = actionContext.get("dataStore");
		Thing dataStore = null;//(Thing) actionContext.get("dataStore");
		if(ds instanceof DataStore) {
			dataStore = ((DataStore) ds).getStore();
		}else {
			dataStore = (Thing) ds;
		}
		ActionContext parentContext = actionContext.getObject("parentContext");
		Boolean continueEdit = actionContext.getBoolean("continueEdit");
		Object initDataObject = actionContext.get("initDataObject");
		Object initValues = actionContext.get("initValues");
		
		DataObject dataObject = (DataObject) form.doAction("getDataObject", actionContext);
		if(confirm && message != null){
		    MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK |SWT.CANCEL);
		    box.setText(shell.getText());
		    box.setMessage(message);
		    if(box.open() != SWT.OK){
		        return;
		    }
		}

		if(insert){
		    try{
		        dataObject = (DataObject) dataStore.doAction("insert", parentContext, "record", dataObject);
		    }catch(Exception e){
		        Executor.error(TAG, "insert dataObject error", e);
		        MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		        box.setText(shell.getText());
		        box.setMessage("" + e.getMessage());
		        SwtUtils.openMessageBox(box, null, actionContext);
		        //box.open();
		        return;
		    }
		}

		if(continueEdit){
		    List<DataObject> result = actionContext.getObject("result");
		    if(result == null){
		        result = Collections.emptyList();
		        actionContext.getScope(0).put("result", result);
		    }
		    result.add(dataObject);
		    
		    //重新初始化表单
		    form.doAction("setValues", actionContext, "values", initDataObject);
		    if(initValues != null){
		        form.doAction("setPartialValues", actionContext, "values", initValues);
		    }
		}else{
		    actionContext.getScope(0).put("result", dataObject);
		    shell.dispose();
		}
	}
	
	public static void setDataObject(ActionContext actionContext){
		Thing form = actionContext.getObject("form");
		Shell shell = actionContext.getObject("shell");
		Object initValues = actionContext.get("initValues");		
		Object obj = actionContext.getObject("dataObject");
		DataObject dataObject = null;
		Thing descriptor = null;
		if(obj instanceof Thing){
			descriptor = (Thing) obj;
			dataObject = new DataObject(descriptor);
		}else{
			dataObject = (DataObject) obj;
			descriptor = dataObject.getMetadata().getDescriptor();
		}
		
		form.doAction("setDataObject", actionContext, "dataObject", dataObject);

		if(initValues != null){
		    form.doAction("setValues", actionContext, "values", initValues);
		}

		int editDialogWith = descriptor.getInt("editDialogWith");
		int editDialogHeight = descriptor.getInt("editDialogHeight");
		if(editDialogWith != 0 && editDialogHeight != 0){
		    shell.setSize(editDialogWith, editDialogHeight);
		}else{
		    shell.pack();
		}
		SwtUtils.centerShell(shell);
	}
	
	public static void setValues(ActionContext actionContext){
		Thing form = actionContext.getObject("form");
		Object values = actionContext.get("values");		
		
		//取数据
		form.doAction("setValues", actionContext, "values", values);
	}
}
