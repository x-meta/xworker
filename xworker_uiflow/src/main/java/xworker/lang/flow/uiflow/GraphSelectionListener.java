package xworker.lang.flow.uiflow;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.xmeta.Thing;

public class GraphSelectionListener implements SelectionListener {
	UiFlow uiFlow;
	
	public GraphSelectionListener(UiFlow uiFlow){
		this.uiFlow = uiFlow;
	}
	
	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		//System.out.println(event);
	}

	@Override
	public void widgetSelected(SelectionEvent event) {
		//System.out.println("widgetSelected");
		ToolItem editToolItem = uiFlow.actionContext.getObject("editToolItem");
		ToolItem linkToolItem = uiFlow.actionContext.getObject("linkToolItem");
		ToolItem deleteToolItem = uiFlow.actionContext.getObject("deleteToolItem");
		ToolItem runToolItem = uiFlow.actionContext.getObject("runToolItem");
		MenuItem editMenuItem = uiFlow.actionContext.getObject("editMenuItem");
		MenuItem linkMenuItem = uiFlow.actionContext.getObject("linkMenuItem");
		MenuItem deleteMenuItem = uiFlow.actionContext.getObject("deleteMenuItem");
		MenuItem runMenuItem = uiFlow.actionContext.getObject("runMenuItem");
				
		if(event.item instanceof GraphNode){
			editToolItem.setEnabled(true);
			linkToolItem.setEnabled(true);
			deleteToolItem.setEnabled(true);
			runToolItem.setEnabled(true);
			
			editMenuItem.setEnabled(true);
			linkMenuItem.setEnabled(true);
			deleteMenuItem.setEnabled(true);
			runMenuItem.setEnabled(true);
			
			uiFlow.showOutlineDoc((Thing) event.item.getData());
			if("setLink".equals(uiFlow.actionContext.get("selectionAction"))){
				//选择操作是设置连接
				GraphNode linkStartNode = uiFlow.actionContext.getObject("linkStartNode");
				if(linkStartNode != null){
					Thing start = (Thing) linkStartNode.getData();
					Thing target = (Thing) event.item.getData();
					start.doAction("updateConnection", uiFlow.actionContext, "target", target);
					//清空操作
					uiFlow.actionContext.g().put("linkStartInfo", null);
					uiFlow.actionContext.g().put("selectionAction", null);
					uiFlow.setLinkEnd();
				}
			}
		}else if(event.item instanceof GraphConnection){
			editToolItem.setEnabled(false);
			linkToolItem.setEnabled(false);
			runToolItem.setEnabled(false);
			deleteToolItem.setEnabled(true);
			
			editMenuItem.setEnabled(false);
			linkMenuItem.setEnabled(false);
			deleteMenuItem.setEnabled(true);
			runMenuItem.setEnabled(false);
		}else{
			editToolItem.setEnabled(false);
			linkToolItem.setEnabled(false);
			deleteToolItem.setEnabled(false);
			runToolItem.setEnabled(false);
			
			editMenuItem.setEnabled(false);
			linkMenuItem.setEnabled(false);
			deleteMenuItem.setEnabled(false);
			runMenuItem.setEnabled(false);
			
			uiFlow.showOutlineDoc(uiFlow.getThing());
		}
		
		uiFlow.actionContext.g().put("currentGraphSelection", event.item);
		//System.out.println(event);
	}

}
