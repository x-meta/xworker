package xworker.swt.design.sync;

import java.util.concurrent.Future;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Thing;
import org.xmeta.cache.ThingEntry;

import xworker.swt.design.DesignTools;
import xworker.swt.design.Designer;

public class SwtSyncTask implements Runnable{
	private static Logger logger = LoggerFactory.getLogger(SwtSyncTask.class);
	
	Control control;
	
	Future<SwtSyncTask> future;
	
	public SwtSyncTask(Control control) {
		this.control = control;
	}
	
	public void setFuture(Future<SwtSyncTask> future) {
		this.future = future;
	}
	
	@Override
	public void run() {
		if(control == null || control.isDisposed()) {
			//如果是任务，取消任务
			if(future != null) {
				future.cancel(false);				
			}
			
			return;
		}
		
		control.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					checkControl(control);
				}catch(Exception e) {
					if(!control.isDisposed()) {
						logger.error("Sync swt eror", e);
					}
				}
			}
		});
	}

	private void checkControl(Control control) {
		if(Designer.getDesignerDialogShell() != null && Designer.getDesignerDialogShell().isDisposed() == false) {
			//设计工具已打开，二者会冲突，因此本自动更新不生效
			return;
		}
		
		ThingEntry entry = Designer.getThingEntry(control);
		//String className = control.getClass().getSimpleName();
		if(entry != null) {
			if(entry.isChanged()) {
				Thing thing = entry.getThing();				
				if(thing != null && thing.isTransient()) {
					return;
				}
				
				if(thing == null) {
					DesignTools.remove(control, false);
					entry.getThing(); //更新Entry，避免重复更新
					return;
				}else if(isChanged(thing, thing.getMetadata().getLastModified())){
					DesignTools.update(control, false);
					entry.getThing(); //更新Entry，避免重复更新
					return;
				}
			}
		}
		
		if(control instanceof Composite) {
			for(Control child : ((Composite) control).getChildren()) {
				checkControl(child);
			}
		}
	}
	
	private boolean isChanged(Thing thing, long lastModified) {
		
		for(Thing child : thing.getChilds()) {
			if(child.getMetadata().getLastModified() == lastModified) {				
				if(!isSameTime(child, lastModified)) {
					//是子节点的子节点发生了变化
					return false;
				}
			}else {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean isSameTime(Thing thing, long lastModified) {
		if(thing.getMetadata().getLastModified() != lastModified) {
			return false;
		}
		
		for(Thing child : thing.getChilds()) {
			if(!isSameTime(child, lastModified)) {
				return false;
			}
		}
		
		return true;
	}
}
