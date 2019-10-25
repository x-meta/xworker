package xworker.lang.flow.uiflow;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphNode;
import org.xmeta.ActionContext;
import org.xmeta.Thing;


public class GraphDropTarget implements DropTargetListener{
	Graph graph;
	ActionContext actionContext;
	
	public GraphDropTarget(UiFlow uiFlow, Graph graph, ActionContext actionContext){
		this.graph = graph;
		this.actionContext = actionContext;
		
		DropTarget dropTarget = new DropTarget(graph, SWT.NONE);
		dropTarget.addDropListener(this);
		dropTarget.setTransfer(new Transfer[]{TextTransfer.getInstance()});
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
		UiFlow uiFlow = actionContext.getObject("uiFlow");
		String path = (String) event.data;
		Thing child = new Thing(path);
		child.put("name", "new" + child.getThingName());
		uiFlow.thing.addChild(child);
		uiFlow.thing.save();
		
		GraphNode node = (GraphNode) child.doAction("createZestGraphNode", actionContext,  "graph", graph);		
		if(node != null){
			//放入缓存
			uiFlow.graphNodes.put(child.getMetadata().getPath(), node);
			Point location = graph.toControl(event.x, event.y);
			node.setLocation(location.x, location.y);
		}
	}

	@Override
	public void dropAccept(DropTargetEvent event) {
	
	}
	

}
