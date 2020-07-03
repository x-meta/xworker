package xworker.app.model.tree.impl;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingClassStructureTreeModel {
	public static Object getRoot(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Thing thing = self.doAction("getRootThing", actionContext);
		//复制根节点
		Thing root = new Thing();
		root.getAttributes().putAll(thing.getAttributes());
		root.put("__path__", thing.getMetadata().getPath());
		
		//上下文，用于避免重复和递归
		Map<String, Thing> context = new HashMap<String, Thing>();
		initClassStructure(thing, root, context);
		
		return ThingTreeModelActions.toTreeNode(root, self, null, null, actionContext);		
	}
	
	public static void initExtends(Thing thing, Thing model, Map<String, Thing> context) {
		for(Thing desc : thing.getExtends()) {
			//复制节点
			Thing node =  new Thing();
			node.getAttributes().putAll(desc.getAttributes());
			model.addChild(node);
			node.put("__path__", desc.getMetadata().getPath());
			
			if(context.get(desc.getMetadata().getPath()) == null) {
				initExtends(desc, node, context);
			}
		}
	}
	
	public static void initClassStructure(Thing thing, Thing model, Map<String, Thing> context) {		
		context.put(thing.getMetadata().getPath(), thing);
				
		if(thing.getDescriptors().size() > 0) {
			Thing classes = new Thing();
			classes.put("name", "Classes");
			classes.put("label", "类");
			classes.put("en_label", "Classes");
			
			model.addChild(classes);
			for(Thing desc : thing.getDescriptors()) {
				//复制节点
				Thing node =  new Thing();
				node.getAttributes().putAll(desc.getAttributes());
				classes.addChild(node);
				node.put("__path__", desc.getMetadata().getPath());
						
				initExtends(desc, node, context);
			}
		}
		
		if( thing.getExtends().size() > 0) {
			Thing exts = new Thing();
			exts.put("name", "Classes");
			exts.put("label", "继承");
			exts.put("en_label", "Extends");
			
			model.addChild(exts);
			for(Thing desc : thing.getExtends()) {
				//复制节点
				Thing node =  new Thing();
				node.getAttributes().putAll(desc.getAttributes());
				exts.addChild(node);
				node.put("__path__", desc.getMetadata().getPath());
				
				initExtends(desc, node, context);
			}
		}
	}
}
