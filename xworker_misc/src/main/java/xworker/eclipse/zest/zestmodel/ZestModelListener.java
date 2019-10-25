package xworker.eclipse.zest.zestmodel;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.zest.core.widgets.Graph;
import org.xmeta.ActionContext;

public class ZestModelListener implements SelectionListener{
	Graph graph;
	ActionContext actionContext;
	
	public ZestModelListener(Graph graph, ActionContext actionContext){
		this.graph = graph;
		this.actionContext = actionContext;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		System.out.println("widgetDefaultSelected:" + event);
	}

	@Override
	public void widgetSelected(SelectionEvent event) {
		System.out.println("widgetSelected:" + event);
	}

	public static void create(ActionContext actionContext){
		Graph parent = actionContext.getObject("parent");
		parent.addSelectionListener(new ZestModelListener(parent, actionContext));
	}
}
