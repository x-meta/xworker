package xworker.org.jsoup;

import java.util.Iterator;
import java.util.List;

import org.jsoup.nodes.TextNode;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class TextActions {
	public static void run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		for(Thing child : self.getChilds()) {
			child.doAction("run", actionContext);
		}
	}
	
	public static void iterator(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		int index = 0;
		List<TextNode> nodes = actionContext.getObject("textNodes");
		Iterator<TextNode> iterator = nodes.iterator();
		while(iterator.hasNext()) {
			TextNode node = iterator.next();
			boolean hasNext = iterator.hasNext();
			
			for(Thing child : self.getChilds()) {
				child.doAction("run", actionContext, "index", index, "hasNext", hasNext, "textNode", node);
			}
		}
	}
}
