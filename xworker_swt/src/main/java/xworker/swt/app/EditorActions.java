package xworker.swt.app;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

import org.xmeta.util.ThingLoader;
import xworker.swt.design.Designer;

import java.lang.reflect.InvocationTargetException;

public class EditorActions {
	public static Object getObjectForThingLoader(Thing self, ActionContext actionContext) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		Object objectForThingLoader = self.doAction("getObjectForThingLoader", actionContext);
		if(objectForThingLoader != null && objectForThingLoader.equals(self.getString("objectForThingLoader"))){
			objectForThingLoader = null;
		}
		if(objectForThingLoader == null){
			Class<?> cls = self.doAction("getObjectClassForThingLoader", actionContext);
			if(cls != null){
				objectForThingLoader = cls.getConstructor(new Class<?>[]{}).newInstance();
				String objName = self.getString("objectForThingLoader");
				if(objName != null && !"".equals(objName)) {
					actionContext.g().put(objName, objectForThingLoader);
				}
			}
		}

		return objectForThingLoader;
	}

	public static Object create(ActionContext actionContext) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		Thing self = actionContext.getObject("self");

		Object objectForThingLoader = getObjectForThingLoader(self, actionContext);

		//保存编辑器事物
		actionContext.g().put("editorThing", self);

			//创建编辑器
		Thing editorThing = self.getThing("EditorComposite@0");
		Composite editorComposite = null;
		if(editorThing != null) {
			if(objectForThingLoader == null) {
				editorComposite = editorThing.doAction("create", actionContext);
			}else{
				editorComposite = ThingLoader.load(objectForThingLoader, editorThing, actionContext);
			}
		}

		//创建ActionContainer
		Thing actionsThing = self.getThing("ActionContainer@0");
		if(actionsThing != null) {
			ActionContainer actions = actionContext.getObject("actions");
			if(actions == null) {
				if(objectForThingLoader == null) {
					actions = actionsThing.doAction("create", actionContext);
				}else{
					actions = ThingLoader.load(objectForThingLoader, actionsThing, actionContext);
				}
				actionContext.g().put("actions", actions);
			}else {
				actions.append(actionsThing);
			}
		}

		return editorComposite;
	}

	public static Object createOutline(ActionContext actionContext) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		Thing self = actionContext.getObject("self");

		Object objectForThingLoader = getObjectForThingLoader(self, actionContext);

		ActionContext parentContext = actionContext.getObject("parentContext");
		if(parentContext != null) {
			IEditorContainer editorContainer = parentContext.getObject("editorContainer");
			//创建Outline，如果存在
			Composite outlineParent = editorContainer.getOutlineParent();
			if(outlineParent != null) {
				Thing outlineThing = self.getThing("OutlineComposite@0");
				if(outlineThing != null) {
					actionContext.peek().put("parent", outlineParent);
					if(objectForThingLoader == null) {
						return outlineThing.doAction("create", actionContext);
					}else{
						return ThingLoader.load(objectForThingLoader, outlineThing, actionContext);
					}
				}
			}
		}

		return null;
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
