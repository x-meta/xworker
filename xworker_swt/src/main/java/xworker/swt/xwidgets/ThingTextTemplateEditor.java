package xworker.swt.xwidgets;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import xworker.swt.design.Designer;

public class ThingTextTemplateEditor {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Thing template = self.doAction("getThingTextTemplate", actionContext);
		String codeType = null;
		if( template != null) {
			codeType = template.doAction("getCodeType", actionContext);
		}
		
		//创建界面
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		ac.put("parentContext", actionContext);
		ac.put("thing", new Thing(template.getMetadata().getPath()));
		ac.put("codeType", codeType);
		
		Thing prototype = null;
		if("form".equals(self.getString("style"))) {
			prototype = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.ThingTextTemplateEditorShell/@formComposite");
		}else {
			prototype = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.ThingTextTemplateEditorShell/@mainComposite");
		}
		Composite composite = null;
		
		Designer.pushCreator(self);
		try{
			composite = prototype.doAction("create", ac);
			Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		}finally{
			Designer.popCreator();
		}
		
		//创建子节点
		actionContext.peek().put("parent", composite);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		//保存变量，保存StyledText
		
		actionContext.g().put(self.getMetadata().getName(), composite);
		Designer.attach(composite, self.getMetadata().getPath(), actionContext);
		
		StyledText codeEditor = ac.getObject("codeEditor");
		if(codeEditor != null) {
			codeEditor.setText(UtilString.getString("lang:d=请先编辑数据&en=Please edit data first", actionContext));
		}
		
		return composite;
	}
}
