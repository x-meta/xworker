package xworker.swt.design.sync;

import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Control;

import xworker.task.TaskManager;

public class SwtSyncer {
	public static void startSync(Control control) {
		SwtSyncTask task = new SwtSyncTask(control);
		
		TaskManager.getScheduledExecutorService().scheduleWithFixedDelay(task, 5000, 2000, TimeUnit.MILLISECONDS);
	}
}
