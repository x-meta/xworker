package xworker.swt.nebula;

import org.eclipse.nebula.widgets.pgroup.PGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;
import xworker.swt.style.StyleSetStyleCreator;
import xworker.swt.util.SwtUtils;

public class PGroupActions {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Object obj = SwtUtils.createNoSupportRAP(self, actionContext);
		if(obj != null) {
			return obj;
		}
		
		Action styleAction = World.getInstance().getAction("xworker.swt.widgets.Composite/@scripts/@getStyles");
		int style = (Integer) styleAction.run(actionContext);
		style = style | SwtUtils.getSWT(self.getString("SMOOTH"));
		
		Composite parent = actionContext.getObject("parent");
		PGroup pgroup = new PGroup(parent, style);
		
		String text = self.getString("text", null, actionContext);
		if(text != null) {
			pgroup.setText(text);
		}
		
		actionContext.peek().put("parent", pgroup);		
        Image image = (Image) StyleSetStyleCreator.createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            pgroup.setImage(image);
        }
        //字体
        Font titleFont = (Font) StyleSetStyleCreator.createResource(self.getString("titleFont"), 
                "xworker.swt.graphics.Font", "fontData", actionContext);
        if(titleFont != null){
            pgroup.setFont(titleFont);
        }
        
        int imagePosition = SwtUtils.getSWT(self.getString("imagePosition"));
        if(self.getBoolean("imagePositionTop")) {
        	imagePosition = imagePosition | SWT.TOP;
        }
        if(imagePosition != SWT.NONE) {
        	pgroup.setImagePosition(imagePosition);
        }
        
        int linePosition = SwtUtils.getSWT(self.getString("linePosition"));
        if(linePosition != SWT.NONE) {
        	pgroup.setLinePosition(linePosition);
        }
        
        int togglePosition = SwtUtils.getSWT(self.getString("togglePosition"));
        if(togglePosition != SWT.NONE) {
        	pgroup.setTogglePosition(togglePosition);
        }
        
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", pgroup);
		try{
		    self.doAction("init", actionContext);
		}finally{
		    actionContext.pop();
		}
		
		//创建子节点
		actionContext.peek().put("parent", pgroup);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		actionContext.g().put(self.getMetadata().getName(), pgroup);
		Designer.attach(pgroup, self.getMetadata().getPath(), actionContext);
		return pgroup;
	}
}
