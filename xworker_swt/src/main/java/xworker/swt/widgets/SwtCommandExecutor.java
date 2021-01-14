package xworker.swt.widgets;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.command.CommandExecutor;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

public class SwtCommandExecutor {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ThingCompositeCreator cc = SwtUtils.createCompositeCreator(self, actionContext);
		cc.setCompositeThing(World.getInstance().getThing("xworker.lang.command.CommandExecutor/@mainTabFolder"));
		
		Object composite = cc.create();
		ActionContext ac = cc.getNewActionContext();
		
		//删除不必要的按钮
		if(self.getBoolean("commandEditable") == false) {
			((Control) ac.get("editDomainButton")).dispose();
			((Control) ac.get("editcommandButton")).dispose();
		}
		
		Button closeButton = ac.getObject("closeButton");
		if(closeButton != null) {
			closeButton.dispose();
		}				
		
		Thing commandDomain = self.doAction("getCommandDomain", actionContext);
		ActionContext domainContext = self.doAction("getDomainContext", actionContext);
		
		CommandExecutor ce = new CommandExecutor(commandDomain, ac, domainContext);
		ac.g().put("executor", ce);
		if(commandDomain != null) {
			//初始化
			ce.reset();
		}
		
		actionContext.g().put(self.getMetadata().getName(), ce);
		
		return composite;
	}
}
