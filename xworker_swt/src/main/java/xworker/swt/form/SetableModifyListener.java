package xworker.swt.form;

import org.eclipse.swt.events.SelectionListener;

public interface SetableModifyListener {
	public boolean isEnable();
	
	public void setEnable(boolean enable);
	
	public SelectionListener getSelectionListener();
}
