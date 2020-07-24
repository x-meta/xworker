package xworker.swt.design.sync;

import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Control;

import xworker.swt.design.DesignTools;
import xworker.swt.design.Designer;
import xworker.task.TaskManager;

public class SwtSyncer {
	public static void startSync(Control control) {
		//出现过类加载问题，在SwtSyncTask里DesignTools类加载问题，怀疑是线程的问题，故在这里先加载
		DesignTools.hasLayout(control);
		Designer.getDesigner();
		
		SwtSyncTask task = new SwtSyncTask(control);
		
		TaskManager.getScheduledExecutorService().scheduleWithFixedDelay(task, 5000, 2000, TimeUnit.MILLISECONDS);
	}
}
