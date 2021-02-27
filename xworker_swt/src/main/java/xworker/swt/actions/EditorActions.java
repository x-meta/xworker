package xworker.swt.actions;

import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import ognl.OgnlException;
import xworker.lang.executor.Executor;

public class EditorActions {
	private static final String TAG = EditorActions.class.getName();
	
	public static Object showEdtiorAt(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Composite target = (Composite) self.doAction("getTarget", actionContext);
		Thing editor = (Thing) self.doAction("getEditor", actionContext);
		
		if(target != null){
			return editor.doAction("create", actionContext, "parent", target);
		}else{
			Executor.warn(TAG, "target is null, thing=" + self.getMetadata().getPath());
			return null;
		}
	}
	
	public static Thing getEditor(ActionContext actionContext) throws OgnlException{
		Thing self = actionContext.getObject("self");
		
		return UtilData.getThing(self, "editor", "Editor@0", actionContext);
	}
	
	public static Composite getTarget(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.getObject("self");
		
		return UtilData.getObjectByType(self, "target", Composite.class, actionContext);
	}
}
