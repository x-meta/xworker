package xworker.eclipse.zest.zestmodel;

import java.util.Map;

import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class ZestNodeTree {
	public static Object create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		ac.put("thing", self);
		
		//Model
		Thing model = (Thing) self.doAction("getZestModel", actionContext);
		if(model != null){
			ac.put("model", model);
			Thing types = model.getThing("NodeTypes@0");
			if(types != null){
				ac.put("nodeThingPath", types.getMetadata().getPath());
			}
		}
		
		//创建SWT
		Thing treeThing = World.getInstance().getThing("xworker.draw2dx.zest.prototype.ZestNodeTreeShell/@tree");
		Object control = treeThing.doAction("create", ac);
		
		//创建子节点
		actionContext.peek().put("parent", control);
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		actionContext.g().put(self.getMetadata().getName(), ac);
		return control;
	}
	
	@SuppressWarnings("unchecked")
	public static void dragStart(ActionContext actionContext){
		//找到树节点对应的事物
		Event event = actionContext.getObject("event");
		Tree tree = (Tree) ((DragSource) event.widget).getControl();
		Map<String, Object> data = (Map<String, Object>) tree.getSelection()[0].getData();
		String path = (String) data.get("dataId");
		Thing thing = World.getInstance().getThing(path);
		
		//如果是目录，则没有拖拽效果
		if(thing == null || "Folder".equals(thing.getThingName())){
			event.doit = false;
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	public static void dragSetData(ActionContext actionContext){
		Event event = actionContext.getObject("event");
		Tree tree = (Tree) ((DragSource) event.widget).getControl();
		Map<String, Object> data = (Map<String, Object>) tree.getSelection()[0].getData();
		String path = (String) data.get("dataId");
		Thing thing = World.getInstance().getThing(path);
		event.data = thing.getMetadata().getPath();		
	}

	public static void dragEnd(ActionContext actionContext){
	
	}
}
