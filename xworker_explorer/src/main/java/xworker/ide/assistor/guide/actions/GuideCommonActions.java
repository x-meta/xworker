package xworker.ide.assistor.guide.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class GuideCommonActions {
	public static void showMessageBox(Shell shell, String msg){
		MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
	    box.setText("选择项目树");
	    box.setMessage(msg);
	    box.open();
	}
}
