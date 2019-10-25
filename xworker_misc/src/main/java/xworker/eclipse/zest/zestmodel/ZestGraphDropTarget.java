package xworker.eclipse.zest.zestmodel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.zest.core.widgets.Graph;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class ZestGraphDropTarget implements DropTargetListener{
	Graph graph;
	ActionContext actionContext;
	
	public ZestGraphDropTarget(Graph graph, ActionContext actionContext){
		this.graph = graph;
		this.actionContext = actionContext;
	}
	
	@Override
	public void dragEnter(DropTargetEvent event) {
	}

	@Override
	public void dragLeave(DropTargetEvent event) {
	}

	@Override
	public void dragOperationChanged(DropTargetEvent event) {
	}

	@Override
	public void dragOver(DropTargetEvent event) {
	}

	@Override
	public void drop(DropTargetEvent event) {
		//path是节点的描述者
		String path = (String) event.data;
		Thing desc = World.getInstance().getThing(path);
		
		//创建节点实例
		if(desc != null){
			Thing node = desc.detach();
			node.doAction("create", actionContext, "parent", graph);
		}
	}

	@Override
	public void dropAccept(DropTargetEvent event) {
	
	}
	
	public static void create(ActionContext actionContext){
		//Thing self = actionContext.getObject("self");
		
		Graph parent = actionContext.getObject("parent");
		DropTarget dropTarget = new DropTarget(parent, SWT.NONE);
		dropTarget.addDropListener(new ZestGraphDropTarget(parent, actionContext));
		dropTarget.setTransfer(new Transfer[]{TextTransfer.getInstance()});
	}
	
}
