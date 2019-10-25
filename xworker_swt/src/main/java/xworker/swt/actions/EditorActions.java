package xworker.swt.actions;

import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import ognl.OgnlException;

public class EditorActions {
	private static Logger logger = LoggerFactory.getLogger(EditorActions.class);
	
	public static Object showEdtiorAt(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Composite target = (Composite) self.doAction("getTarget", actionContext);
		Thing editor = (Thing) self.doAction("getEditor", actionContext);
		
		if(target != null){
			return editor.doAction("create", actionContext, "parent", target);
		}else{
			logger.warn("target is null, thing=" + self.getMetadata().getPath());
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
