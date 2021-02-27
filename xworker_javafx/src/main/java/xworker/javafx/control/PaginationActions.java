package xworker.javafx.control;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.util.Callback;

public class PaginationActions {
	@SuppressWarnings("unchecked")
	public static void init(Pagination p, Thing thing, ActionContext actionContext) {
		ControlActions.init(p, thing, actionContext);

		if (thing.valueExists("currentPageIndex")) {
			p.setCurrentPageIndex(thing.getInt("currentPageIndex"));
		}
		if (thing.valueExists("maxPageIndicatorCount")) {
			p.setMaxPageIndicatorCount(thing.getInt("maxPageIndicatorCount"));
		}
		if (thing.valueExists("pageCount")) {
			p.setPageCount(thing.getInt("pageCount"));
		}
		if (thing.valueExists("pageFactory")) {
			Callback<Integer,Node> pageFactory = (Callback<Integer,Node>) UtilData.getData(thing.getString("pageFactory"), actionContext);
			if(pageFactory != null) {
				p.setPageFactory(pageFactory);
			}
		}		
	}
	
	public static Pagination create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Pagination item = new Pagination();
		init(item, self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), item);
		
		actionContext.peek().put("parent", item);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);			
		}
		
		return item;
	}
}
