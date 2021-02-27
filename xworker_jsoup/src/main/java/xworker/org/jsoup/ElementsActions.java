package xworker.org.jsoup;

import java.util.ListIterator;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ElementsActions {
	public static void run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		for(Thing child : self.getChilds()) {
			child.doAction("run", actionContext);
		}
	}
	
	public static void forms(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Elements elements = actionContext.getObject("elements");
		
		for(Thing child : self.getChilds()) {
			child.doAction("run", actionContext, "forms", elements.forms());
		}
	}
	
	public static void listIterator(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Elements elements = actionContext.getObject("elements");
		int index = self.doAction("getIndex", actionContext);
		ListIterator<Element> iterator = elements.listIterator(index);
		
		while(iterator.hasNext()) {
			Element element = iterator.next();
			boolean hasNext = iterator.hasNext();
			for(Thing child : self.getChilds()) {
				child.doAction("run", actionContext, "element", element, "index", index, "hasNext", hasNext);
			}
			index++;
		}
	}
	
	public static void parents(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Elements elements = actionContext.getObject("elements");
		
		for(Thing child : self.getChilds()) {
			child.doAction("run", actionContext, "elements", elements.parents());
		}
	}
	
	public static void select(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element elements = actionContext.getObject("elements");
		String cssQuery = self.doAction("getCssQuery", actionContext);
		
		for(Thing child : self.getChilds()) {
			child.doAction("run", actionContext, "elements", elements.select(cssQuery));
		}
	}
}
