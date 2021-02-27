package xworker.org.jsoup;

import java.util.Iterator;
import java.util.List;

import org.jsoup.nodes.DataNode;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class DatasActions {
	public static void run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		for(Thing child : self.getChilds()) {
			child.doAction("run", actionContext);
		}
	}
	
	public static void iterator(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		int index = 0;
		List<DataNode> nodes = actionContext.getObject("dataNodes");
		Iterator<DataNode> iterator = nodes.iterator();
		while(iterator.hasNext()) {
			DataNode node = iterator.next();
			boolean hasNext = iterator.hasNext();
			
			for(Thing child : self.getChilds()) {
				child.doAction("run", actionContext, "index", index, "hasNext", hasNext, "dataNode", node);
			}
		}
	}
}
