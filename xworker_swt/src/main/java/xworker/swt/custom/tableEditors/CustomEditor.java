package xworker.swt.custom.tableEditors;

import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.annotation.ActionClass;
import org.xmeta.util.ActionContainer;

import xworker.swt.custom.ItemEditor;

@ActionClass(creator="createInstance")
public class CustomEditor extends AbstractTableEditor{
	ActionContainer actions;
	Control control;

	@Override
	public Object create(ActionContext actionContext) {
		ItemEditor editor = actionContext.getObject("editor");
		if(editor == null) {
			return null;
		}
		
		Thing attribute = editor.getColumnThing(editor.getCurrentColumn());
		if(attribute == null) {
			return null;
		}
		
		Thing editorThing = attribute.getThing("SwtObject@0/ItemEditor@0");
		if(editorThing == null) {
			return null;
		}
		
		if(editorThing.getChilds().size() > 0) {
			control = editorThing.getChilds().get(0).doAction("create", actionContext);
			actions = actionContext.getObject("actions");
			return control;
		}	
		
		return null;
	}

	@Override
	public void setValue(Object value, ActionContext actionContext) {
		if(actions != null) {
			actions.doAction("setValue", actionContext, "value", value);
		}		
	}

	@Override
	public Object getValue(ActionContext actionContext) {		
		if(actions != null) {
			return actions.doAction("getValue", actionContext);
		}else {
			return null;
		}
	}

	@Override
	public void doDispose() {
		if(control != null) {
			control.dispose();
		}
	}
	
	public static CustomEditor createInstance(ActionContext actionContext) {
		return ItemEditorUtils.createInstance(CustomEditor.class, actionContext);
	}

}
