package xworker.ide.utils;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class ThingGuideActions {
	public static void create(ActionContext actionContext){
		Thing editThing = actionContext.getObject("thing");
		Thing guideThing = actionContext.getObject("self");
		new ThingGuide(editThing, guideThing, actionContext);
	}
	
	public static void nextButtonSelection(ActionContext actionContext){
		ThingGuide thingGuide = actionContext.getObject("thingGuide");
		thingGuide.finishCurrentNode();
	}
	
	public static void finishButtonSelection(ActionContext actionContext){
		ThingGuide thingGuide = actionContext.getObject("thingGuide");
		thingGuide.finishCurrentNode();
	}
	
	public static Thing getNextNode(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing nextNode = World.getInstance().getThing(self.getStringBlankAsNull("nextNode"));
		if(nextNode != null){
			return nextNode;
		}
		
		//首先取下级节点，如果存在
		List<Thing> nodes = self.getChilds("ThingGuideNode");
		if(nodes != null && nodes.size() > 0){
			return nodes.get(0);
		}
		
		//其次取平级或父级的下一个节点
		return getNextNode(self);
	}
		
	/**
	 * 取相对于指定节点的下一个节点。
	 * 
	 * @param node
	 * @return
	 */
	public static Thing getNextNode(Thing node){
		Thing parent = node.getParent();
		if(parent == null){
			return null;
		}
		
		//首先取指定节点的下一个节点
		List<Thing> nodes = parent.getChilds("ThingGuideNode");
		for(int i=0; i<nodes.size(); i++){
			if(nodes.get(i) == node){
				if(i < nodes.size() - 1){
					return nodes.get(i + 1);
				}
			}
		}
		
		//递归取父节点
		if("ThingGuide".equals(parent.getThingName())){
			//如果父节点是ThingGuide，就不再递归取上级父节点了
			return null;
		}
		
		return getNextNode(parent);
	}
}
