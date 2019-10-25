package xworker.swt.xwidgets;

import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import xworker.lang.actions.ActionContainer;
import xworker.swt.util.ThingCompositeCreator;

public class GroovyConsole {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		//创建控件
		ThingCompositeCreator creator = new ThingCompositeCreator(self, actionContext);
		if(self.getBoolean("readOnly")) {
			creator.setCompositeThing(World.getInstance().getThing("xworker.swt.xwidgets.prototypes.GroovyConsole/@mainComposite1"));
		}else {
			creator.setCompositeThing(World.getInstance().getThing("xworker.swt.xwidgets.prototypes.GroovyConsole/@mainComposite"));
		}
		Composite composite = (Composite) creator.create();
				
		ActionContext ac = creator.getNewActionContext();
		
		//初始化变量上下文
		ActionContainer actions = ac.getObject("actions");
		actions.doAction("doInit", ac);
		
		String code = self.getString("code");
		code = UtilString.getString(code, actionContext);
		if(code != null) {
			actions.doAction("setCode", ac, "code", code);
		}
		
		//保存变量
		actionContext.g().put(self.getMetadata().getName(), actions);
		
		return composite;
	}
}
