package xworker.debug;

import java.lang.Thread.State;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;

public class DebugConsole implements Runnable {
	ActionContext actionContext;
	Shell shell;	
	
	public DebugConsole(ActionContext actionContext) {
		this.actionContext = actionContext;
		
		shell = actionContext.getObject("shell");
	}
	
	public void run() {
		try {
			while(shell.isDisposed() == false) {
				Thread.sleep(2000);
				
				check();
			}
		}catch(Exception e) {
			//e.printStackTrace();
		}
	}
	
	private void check() {
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					Tree threadTree = actionContext.getObject("threadTree");
					ThreadGroup root = getRootThreadGroup(Thread.currentThread().getThreadGroup());
					checkThreadGroup(root, threadTree.getItem(0));
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	
	
	private void checkThreadGroup(ThreadGroup threadGroup, TreeItem treeItem) {
		//检查线程
		Thread[] threads = new Thread[threadGroup.activeCount() * 2];
		threadGroup.enumerate(threads, false);
		for(Thread th : threads) {
			if(th == null) {
				continue;
			}
			
			boolean have = false;
			for(TreeItem childItem : treeItem.getItems()) {
				if(childItem.getData() == th) {
					have = true;
					break;
				}
			}
			
			if(!have) {
				TreeItem childItem = new TreeItem(treeItem, SWT.NONE);
				childItem.setData(th);
				childItem.setText(th.getName());
				childItem.setImage((Image) actionContext.get("threadGoImg"));
			}
		}
		
		//检查线程组
		ThreadGroup[] threadGroups = new ThreadGroup[threadGroup.activeGroupCount() * 2];
		threadGroup.enumerate(threadGroups, false);
		for(ThreadGroup th : threadGroups) {
			if(th == null) {
				continue;
			}
			
			boolean have = false;
			TreeItem item = null;
			for(TreeItem childItem : treeItem.getItems()) {
				if(childItem.getData() == th) {
					have = true;
					item = childItem;
					break;
				}
			}
			
			if(!have) {
				TreeItem childItem = new TreeItem(treeItem, SWT.NONE);
				childItem.setData(th);
				childItem.setText(th.getName());
				childItem.setImage((Image) actionContext.get("threadGroupImg"));
				item = childItem;
			}
			
			checkThreadGroup(th, item);
		}
		
		//检查线程或者线程组是否还存在
		for(int i=0; i<treeItem.getItemCount(); i++) {
			TreeItem item = treeItem.getItem(i);
			if(item.getData() instanceof Thread) {
				Thread th = (Thread) item.getData();
				if(th.getState() == State.TERMINATED) {
					item.dispose();
					i--;
				}
			}else if(item.getData() instanceof ThreadGroup) {
				ThreadGroup thg = (ThreadGroup) item.getData();
				if(thg.isDestroyed()) {
					item.dispose();
					i--;
				}
			}
		}
	}
	
	private ThreadGroup getRootThreadGroup(ThreadGroup threadGroup) {
		if(threadGroup.getParent() != null) {
			return getRootThreadGroup(threadGroup.getParent());
		}else {
			return threadGroup;
		}
	}
}
