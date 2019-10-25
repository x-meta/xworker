package xworker.ide.worldexplorer.editor;

import java.util.concurrent.TimeUnit;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

import xworker.task.TaskManager;

public class OutlineTreeMoveListener {
	public TreeItem lastItem;
	public Tree outlineTree;
	public ActionContext actionContext;
	public long changeTime = 0;
		
	public OutlineTreeMoveListener(Tree outlineTree, ActionContext actionContext){
		this.outlineTree = outlineTree;
		this.actionContext = actionContext;
	}
	
	public void mouseMoved(Event event){
		TreeItem item = outlineTree.getItem(new Point(event.x, event.y));
		if(item == null){
			lastItem = null;
			changeTime = System.currentTimeMillis();
		}else if(item != lastItem){
			//启动2秒后的显示文档的任务
			lastItem = item;
			changeTime = System.currentTimeMillis();
			TaskManager.getScheduledExecutorService().schedule(new ShowHelpTask(this, item), 2000, TimeUnit.MILLISECONDS);
		}
	}
	
	public static class ShowHelpTask implements Runnable{
		Thing thing;
		TreeItem item;
		long changeTime;
		OutlineTreeMoveListener listener;
		
		public ShowHelpTask(OutlineTreeMoveListener listener, TreeItem item){
			this.listener = listener;
			this.item = item;
			this.changeTime = listener.changeTime;
			thing = (Thing) item.getData();
		}
		
		public void run(){
			//System.out.println(listener.changeTime == changeTime);
			//System.out.println(listener.lastItem == item);
			if(listener.changeTime == changeTime && listener.lastItem == item){
				listener.outlineTree.getDisplay().asyncExec(new Runnable(){
					public void run(){
						Thing descriptor = thing.getDescriptor();
						
						ActionContainer actions = listener.actionContext.getObject("actions");
						actions.doAction("initOutlineBrowser", listener.actionContext, 
								"thing", thing, "descriptor", descriptor);
					}
				});
			}
		}
	}
	
	public static void treeMouseMoved(ActionContext actionContext){
		OutlineTreeMoveListener moveListener  = actionContext.getObject("innerOutlineMoveListener");
		if(moveListener != null){
			Event event = actionContext.getObject("event");
			moveListener.mouseMoved(event);
		}
	}
}
