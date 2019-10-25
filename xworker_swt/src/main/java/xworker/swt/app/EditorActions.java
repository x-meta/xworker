package xworker.swt.app;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

import xworker.swt.design.Designer;

public class EditorActions {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		//保存编辑器事物
		actionContext.g().put("editorThing", self);
		
			//创建编辑器
		Thing editorThing = self.getThing("EditorComposite@0");		
		Composite editorComposite = null;
		if(editorThing != null) {		
			editorComposite = editorThing.doAction("create", actionContext);
		}
		
		//创建ActionContainer
		Thing actionsThing = self.getThing("ActionContainer@0");
		if(actionsThing != null) {
			ActionContainer actions = actionContext.getObject("actions");
			if(actions == null) {
				actions = actionsThing.doAction("create", actionContext);
				actionContext.g().put("actions", actions);
			}else {
				actions.append(actionsThing);
			}
		}
		
		ActionContext parentContext = actionContext.getObject("parentContext");
		if(parentContext != null) {
			IEditorContainer editorContainer = parentContext.getObject("editorContainer");
			//创建Outline，如果存在
			Composite outlineParent = editorContainer.getOutlineParent();
			if(outlineParent != null) {
				Thing outlineThing = self.getThing("OutlineComposite@0");
				if(outlineThing != null) {
					actionContext.peek().put("parent", outlineParent);
					outlineThing.doAction("create", actionContext);
				}
			}
		}
		
		return editorComposite;
	}
		
	public static void editorChanged(ActionContext actionContext) {
		ActionContext parentContext = actionContext.getObject("parentContext");
		if(parentContext != null) {
			IEditorContainer editorContainer = parentContext.getObject(IEditorContainer.EDITOR_CONTAINER);
			if(editorContainer != null) {
				editorContainer.editorModified((ActionContainer) actionContext.getObject("actions"));				
			}
		}
	}
	
	public static Object createAppEditor(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Thing editor = self.doAction("getEditor", actionContext);
		if(editor != null) {
			ActionContext ac = new ActionContext();
			ac.put("parentContext", actionContext);
			ac.put("parent", actionContext.getObject("parent"));
			
			actionContext.g().put(self.getMetadata().getName(), ac);
			Designer.pushCreator(self);
			Control composite = null;
			try{
				composite = editor.doAction("create", ac);
			}finally{
				Designer.popCreator();
			}
			
			//创建子节点
			actionContext.peek().put("parent", composite);
			for(Thing child : self.getChilds()){
				child.doAction("create", actionContext);
			}
			
			if(composite != null) {
				Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
			}
			return composite;
		}
		
		return null;
	}
}
