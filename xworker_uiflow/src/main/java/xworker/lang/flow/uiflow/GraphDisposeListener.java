package xworker.lang.flow.uiflow;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.zest.core.widgets.GraphNode;
import org.xmeta.Thing;
import org.xmeta.World;

public class GraphDisposeListener implements DisposeListener{
	UiFlow uiFlow;
	
	public GraphDisposeListener(UiFlow uiFlow){
		this.uiFlow = uiFlow;
	}
	
	@Override
	public void widgetDisposed(DisposeEvent event) {
		//保存所有节点的位置，当前没有考虑节点是其它事物的情况，如果有那么需要考虑
		for(String key : uiFlow.graphNodes.keySet()){
			GraphNode node = uiFlow.graphNodes.get(key);
			Thing thing = World.getInstance().getThing(key);
			if(thing != null && node != null){
				thing.put("x", String.valueOf(node.getLocation().x));
				thing.put("y", String.valueOf(node.getLocation().y));
			}
		}
		
		//保存事物
		if(uiFlow.thing != null) {
			uiFlow.thing.save();
		}
	}

}
