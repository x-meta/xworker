package xworker.app.view.swt.widgets.form;

import xworker.dataObject.DataObject;

public interface DataObjectFormListener {
	public void onSetDataObject(DataObjectForm dataObjectForm, DataObject dataObject);
	
	public void onMidified(DataObjectForm dataObjectForm);
}
