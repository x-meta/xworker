package xworker.app.view.extjs.xworker.thingEditor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import ognl.OgnlException;
import xworker.app.model.tree.impl.ThingTreeModelActions;

public class ThingTreeModel {
	public static Object getThingChilds(ActionContext actionContext) throws IOException, OgnlException{
    	Thing self = (Thing) actionContext.get("self");
    	HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
    	Thing thing = World.getInstance().getThing(request.getParameter("thingPath"));
        List<Map<String, Object>> childNodes = new ArrayList<Map<String, Object>>();
        childNodes.add(ThingTreeModelActions.toTreeNode(thing, self, null, null, actionContext));
		//for(Thing child : thing.getChilds()){
		//	childNodes.add(ThingTreeModelActions.toTreeNode(child, self));
	//	}
		
		return childNodes;
    }
	
	public static Object getThingAddChilds(ActionContext actionContext) throws IOException, OgnlException{
    	Thing self = (Thing) actionContext.get("self");
    	HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
    	Thing thing = World.getInstance().getThing(request.getParameter("thingPath"));
        List<Map<String, Object>> childNodes = new ArrayList<Map<String, Object>>();
		for(Thing child : thing.getChilds()){
			childNodes.add(ThingTreeModelActions.toTreeNode(child, self, null, null, actionContext));
		}
		
		return childNodes;
    }
}
