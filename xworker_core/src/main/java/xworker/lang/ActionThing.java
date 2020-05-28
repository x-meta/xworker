package xworker.lang;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.util.XWorkerUtils;

public class ActionThing {
	public static Object run(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		if(self == null) {
			throw new ActionException("ActionThing not set as self action!");
		}
		
		if("edit".equals(self.getString("mode")) && XWorkerUtils.getIde() == null){
			Thing shell = World.getInstance().getThing("xworker.ide.worldexplorer.swt.editor.ThingEditorShell");
			shell.doAction("run", actionContext, "thing", self);
			return null;
		}
		
		if(self.getBoolean("disabled")) {
			//未激活状态，不执行
			return null;
		}
		
		Object result = null;
        actionContext.peek().setVarScopeFlag(); //设置局部变量范围的标识
        for(Thing action : self.getChilds()){      
            result = action.getAction().run(actionContext, null, true);
    
            if(ActionContext.RETURN == actionContext.getStatus() || 
                ActionContext.CANCEL == actionContext.getStatus() || 
                ActionContext.BREAK == actionContext.getStatus() || 
                ActionContext.EXCEPTION == actionContext.getStatus()){
                break;
            }
        } 
        
        return result;
	}
}
