package xworker.swt.xwidgets;

import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.ActionContainer;
import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class CodeViewer {
	ActionContainer actions;
	Thing self;
	Control control;
	String codeType;
	String codeName;
	
	public CodeViewer(Thing self, ActionContainer actions, Control control){
		this.self = self;
		this.actions = actions;
		this.control = control;
	}
	
	public void setCode(String codeName, String codeType, String code){
		if(codeName == null || code == null){
			return;
		}
		if(codeType == null){
			codeType = codeName;
		}
		
		this.codeName = codeName;
		this.codeType = codeType;
		actions.doAction("setCode", null, "codeType", codeType, "codeName", codeName, "code", code);
	}
	
	public void setCode(String code) {
		if(codeName == null || code == null){
			return;
		}
		if(codeType == null){
			codeType = codeName;
		}
		
		actions.doAction("setCode", null, "codeType", codeType, "codeName", codeName, "code", code);
	}
		
	public ActionContainer getActions() {
		return actions;
	}

	public Thing getSelf() {
		return self;
	}

	public Control getControl() {
		return control;
	}

	public static Object create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Thing prototype = null;
		if(SwtUtils.isRWT()){
			prototype = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.CodeViewerRWT/@composite");
		}else{
			prototype = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.CodeViewerSWT/@text");
		}
		
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		ac.put("parentContext", actionContext);
		ac.put("codeViewerThing", self);
		
		Designer.pushCreator(self);
		Control obj = null;
		try{
			obj = prototype.doAction("create", ac);
		}finally{
			Designer.popCreator();
		}
		//创建子节点
		actionContext.peek().put("parent", obj);
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		Designer.attachCreator(obj, self.getMetadata().getPath(), actionContext);
		CodeViewer viewer = new CodeViewer(self, (ActionContainer) ac.getObject("actions"), (Control) obj);
		
		//设置初始的文本,RWT下会用异步的方式重置
		if(SwtUtils.isRWT() == false){
			String codeName = self.doAction("getCodeName", actionContext);
			String codeType = self.doAction("getCodeType", actionContext);
			String code = self.doAction("getCode", actionContext);
			viewer.setCode(codeName, codeType, code);
		}
		
		//保存变量
		actionContext.g().put(self.getMetadata().getName(), viewer);
		
		return obj;
	}
}
