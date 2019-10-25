package xworker.swt.component;

import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.Designer;

public class Component {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ActionContext newActionContext = new ActionContext();
		newActionContext.put("parent", actionContext.get("parent"));
		
		//创建组件
		Thing compositeThing = self.doAction("getComposite", actionContext);		
		if(compositeThing != null) {
			Object obj = null;
			Designer.pushCreator(compositeThing);
			try {
				obj = compositeThing.doAction("create", newActionContext);
				if(obj instanceof Control) {
					Designer.attachCreator((Control) obj, self.getMetadata().getPath(), actionContext);
				}
			}finally {
				Designer.popCreator();
			}			
		}
		
		//创建ActionContainer
		Thing actionContainer = self.doAction("getActionContainer", actionContext);
		if(actionContainer != null) {
			return actionContainer.doAction("create", newActionContext);
		}else {
			return null;
		}
	}
}
