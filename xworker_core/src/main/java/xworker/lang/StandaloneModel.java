package xworker.lang;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.util.XWorkerUtils;

public class StandaloneModel {
	public static void run(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
				
		boolean edit = "edit".equals(self.getString("mode"));
		//XMeta执行时传入的参数
		if(edit == false && actionContext.get("_args_") != null) {
			String[] args = actionContext.getObject("_args_");
			for(int i=0; i<args.length; i++) {
				if("mode=edit".equals(args[i])) {
					edit = true;
					break;
				}
			}
		}
		
		//如果是编辑模式，进入编辑器
		if(edit && XWorkerUtils.getIde() == null){
			Thing shell = World.getInstance().getThing("xworker.ide.worldexplorer.swt.editor.ThingEditorShell");
			shell.doAction("run", actionContext, "thing", self);
			return;
		}
		
		
		//运行app
		Thing app = self.doAction("getApp", actionContext);
		if(app != null) {
			app.doAction("run", actionContext);
		}
		
		//运行子节点
		for(Thing child : self.getChilds()) {
			child.doAction("run", actionContext);
		}
	}
}
