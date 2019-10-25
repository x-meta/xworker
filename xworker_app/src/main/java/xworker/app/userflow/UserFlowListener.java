package xworker.app.userflow;

import xworker.dataObject.DataObject;

public interface UserFlowListener {
	public void started(DataObject task);
	
	public void finished(DataObject task);
}
