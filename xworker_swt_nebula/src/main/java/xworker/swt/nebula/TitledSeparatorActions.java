package xworker.swt.nebula;

import org.eclipse.nebula.widgets.opal.titledseparator.TitledSeparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.DesignTools;
import xworker.swt.design.Designer;
import xworker.swt.style.StyleSetStyleCreator;
import xworker.swt.util.SwtUtils;

public class TitledSeparatorActions {
	static {
		DesignTools.registUnInsertable(TitledSeparator.class);
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Object obj = SwtUtils.createNoSupportRAP(self, actionContext);
		if(obj != null) {
			return obj;
		}
		
		Action styleAction = World.getInstance().getAction("xworker.swt.widgets.Composite/@scripts/@getStyles");
		int style = (Integer) styleAction.run(actionContext);
		Composite parent = actionContext.getObject("parent");
		TitledSeparator separator = new TitledSeparator(parent, style);
		
		String text = self.getString("text", null, actionContext);
		if(text != null) {
			separator.setText(text);
		}
		
		actionContext.peek().put("parent", separator);		
        Image image = (Image) StyleSetStyleCreator.createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
        	separator.setImage(image);
        }
        
        int alignment = SwtUtils.getSWT(self.getString("alignment"));
        if(alignment != SWT.NONE) {
        	separator.setAlignment(alignment);
        }
                
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", separator);
		try{
		    self.doAction("init", actionContext);
		}finally{
		    actionContext.pop();
		}
		
		//创建子节点
		actionContext.peek().put("parent", separator);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		actionContext.g().put(self.getMetadata().getName(), separator);
		Designer.attach(separator, self.getMetadata().getPath(), actionContext);
		return separator;
	}
}
