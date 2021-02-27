package xworker.org.jsoup;

import java.util.Iterator;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.nodes.FormElement;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class FormActions {
	public static void formElementIterator(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		int index = 0;
		List<FormElement> forms = actionContext.getObject("forms");
		Iterator<FormElement> iterator = forms.iterator();
		while(iterator.hasNext()) {
			FormElement node = iterator.next();
			boolean hasNext = iterator.hasNext();
			
			for(Thing child : self.getChilds()) {
				child.doAction("run", actionContext, "index", index, "hasNext", hasNext, "form", node);
			}
		}
	}
	
	public static void elements(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		FormElement element = actionContext.getObject("form");
		
		for(Thing child : self.getChilds()) {
			child.doAction("run", actionContext, "elements", element.elements());
		}
	}
	
	public static void submit(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		FormElement element = actionContext.getObject("form");
		
		Connection connection = element.submit();
		ConnectionActions.initConnection(connection, self, actionContext);
	}
}
