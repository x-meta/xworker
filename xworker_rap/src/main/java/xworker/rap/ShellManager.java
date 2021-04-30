package xworker.rap;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * Shell窗口在游戏平台上最小化后会看不到，因此制作该工具来解决此问题。
 * 
 * @author zyx
 *
 */
public class ShellManager {
	Shell shell;
	List list;
	java.util.List<Shell> shells = new ArrayList<Shell>();
	
	public ShellManager(Shell parent) {
		Display display = parent.getDisplay();
		for(Shell s : display.getShells()) {
			shells.add(s);
		}
		
		shell = new Shell(parent, SWT.CLOSE);
		shell.setSize(300, 400);
		shell.setLayout(new GridLayout());
		shell.setText("Shell Manager");
		
		list = new List(shell, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData listGridData = new GridData(GridData.FILL_BOTH);
		list.setLayoutData(listGridData);
		
		for(Shell s : shells) {
			list.add(s.getText());
		}
		
		list.addListener(SWT.DefaultSelection, new Listener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void handleEvent(Event event) {
				int index = list.getSelectionIndex();
				Shell s = shells.get(index);
				s.forceActive();
				s.setMinimized(false);
				s.setVisible(true);			
				s.open();
				shell.dispose();
			}
			
		});
		
		shell.setVisible(true);		
	}	
}
