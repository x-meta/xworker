package xworker.org.jsoup;

import java.util.List;

import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ElementActions {
	public static void doElements(Thing self, ActionContext actionContext, Elements elements) {
		for(Thing child : self.getChilds()) {
			child.doAction("run", actionContext, "elements", elements);
		}
	}
	
	public static void allElements(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		doElements(self, actionContext, element.getAllElements());
	}
	
	public static void elementsByAttribute(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		String key = self.doAction("getKey", actionContext);
		doElements(self, actionContext, element.getElementsByAttribute(key));
	}
	
	public static void elementsByAttributeStarting(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		String keyPrefix = self.doAction("getKeyPrefix", actionContext);
		
		doElements(self, actionContext, element.getElementsByAttributeStarting(keyPrefix));
	}
	
	public static void elementsByAttributeValue(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		String key = self.doAction("getKey", actionContext);
		String value = self.doAction("getValue", actionContext);
				
		doElements(self, actionContext, element.getElementsByAttributeValue(key, value));
	}
	
	public static void elementsByAttributeValueContaining(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		String key = self.doAction("getKey", actionContext);
		String match = self.doAction("getMatch", actionContext);
		
		doElements(self, actionContext, element.getElementsByAttributeValueContaining(key, match));
	}
	
	public static void elementsByAttributeValueEnding(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		String key = self.doAction("getKey", actionContext);
		String valueSuffix = self.doAction("getValueSuffix", actionContext);
		
		doElements(self, actionContext, element.getElementsByAttributeValueEnding(key, valueSuffix));
	}
	
	public static void elementsByAttributeValueMatching(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		String key = self.doAction("getKey", actionContext);
		String regex = self.doAction("getRegex", actionContext);
		
		doElements(self, actionContext, element.getElementsByAttributeValueMatching(key, regex));
	}
	
	public static void elementsByAttributeValueNot(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		String key = self.doAction("getKey", actionContext);
		String value = self.doAction("getValue", actionContext);
		
		doElements(self, actionContext, element.getElementsByAttributeValueNot(key, value));
	}
	
	public static void elementsByAttributeValueStarting(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		String key = self.doAction("getKey", actionContext);
		String valuePrefix = self.doAction("getValuePrefix", actionContext);
		
		doElements(self, actionContext, element.getElementsByAttributeValueStarting(key, valuePrefix));
	}
	
	public static void elementsByClass(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		String className = self.doAction("getClassName", actionContext);
				
		doElements(self, actionContext, element.getElementsByClass(className));
	}
	
	public static void ElementsByIndexEquals(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		int index = self.doAction("getIndex", actionContext);
		
		doElements(self, actionContext, element.getElementsByIndexEquals(index));
	}
	
	public static void ElementsByIndexGreaterThan(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		int index = self.doAction("getIndex", actionContext);
		
		doElements(self, actionContext, element.getElementsByIndexGreaterThan(index));
	}
	
	public static void elementsByIndexLessThan(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		int index = self.doAction("getIndex", actionContext);
				
		doElements(self, actionContext, element.getElementsByIndexLessThan(index));
	}
	
	public static void ElementsByTag(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		String tagName = self.doAction("getTagName", actionContext);
		
		doElements(self, actionContext, element.getElementsByTag(tagName));
	}
	
	public static void elementsContainingOwnText(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		String searchText = self.doAction("getSearchText", actionContext);
		
		doElements(self, actionContext, element.getElementsContainingOwnText(searchText));
	}
	
	public static void elementsContainingText(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		String searchText = self.doAction("getSearchText", actionContext);
		
		doElements(self, actionContext, element.getElementsContainingText(searchText));
	}
	
	public static void elementsMatchingOwnText(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		String regex = self.doAction("getRegex", actionContext);
		
		doElements(self, actionContext, element.getElementsMatchingOwnText(regex));
	}
	
	public static void elementsMatchingText(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		String searchText = self.doAction("getSearchText", actionContext);
		
		doElements(self, actionContext, element.getElementsMatchingText(searchText));
	}
	
	public static void elementById(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		String id = self.doAction("getId", actionContext);

		for(Thing child : self.getChilds()) {
			child.doAction("run", actionContext, "element", element.getElementById(id));
		}
	}
	
	public static void lastElementSibling(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");

		for(Thing child : self.getChilds()) {
			child.doAction("run", actionContext, "element", element.lastElementSibling());
		}
	}
	
	public static void nextElementSibling(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");

		for(Thing child : self.getChilds()) {
			child.doAction("run", actionContext, "element", element.nextElementSibling());
		}
	}
	
	public static void parent(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");

		for(Thing child : self.getChilds()) {
			child.doAction("run", actionContext, "element", element.parent());
		}
	}
	
	public static void parents(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		
		doElements(self, actionContext, element.parents());
	}
	
	public static void select(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		String cssQuery = self.doAction("getCssQuery", actionContext);
		doElements(self, actionContext, element.select(cssQuery));
	}
	
	public static void siblingElements(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		
		doElements(self, actionContext, element.siblingElements());
	}
	
	public static void textNodes(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		List<TextNode> textNodes = element.textNodes();
		for(Thing child : self.getChilds()) {
			child.doAction("run", actionContext, "textNodes", textNodes);
		}
	}
	
	public static void children(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		
		doElements(self, actionContext, element.children());
	}
	
	public static void dataNodes(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");
		
		List<DataNode> dataNodes = element.dataNodes();
		for(Thing child : self.getChilds()) {
			child.doAction("run", actionContext, "dataNodes", dataNodes);
		}
	}
	
	public static void firstElementSibling(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Element element = actionContext.getObject("element");

		for(Thing child : self.getChilds()) {
			child.doAction("run", actionContext, "element", element.firstElementSibling());
		}
	}
	
	public static void run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		for(Thing child : self.getChilds()) {
			child.doAction("run", actionContext);
		}
	}
}
