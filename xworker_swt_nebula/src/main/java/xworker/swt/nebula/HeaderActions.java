package xworker.swt.nebula;

import org.eclipse.nebula.widgets.opal.header.Header;
import org.eclipse.swt.graphics.Color;
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

public class HeaderActions {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Object obj = SwtUtils.createNoSupportRAP(self, actionContext);
		if(obj != null) {
			return obj;
		}
		
		Action styleAction = World.getInstance().getAction("xworker.swt.widgets.Composite/@scripts/@getStyles");
		int style = (Integer) styleAction.run(actionContext);
		Composite parent = actionContext.getObject("parent");
		Header header = new Header(parent, style);
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", header);
		try{
		    self.doAction("init", actionContext);
		}finally{
		    actionContext.pop();
		}
		
		String title = self.getString("title", null, actionContext);
		if(title != null) {
			header.setTitle(title);
		}
		String description = self.getString("description", null, actionContext);
		if(description != null) {
			header.setDescription(description);
		}
		
		actionContext.peek().put("parent", header);
		Color titleColor = (Color) StyleSetStyleCreator.createResource(self.getString("titleColor"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(titleColor != null){
            header.setTitleColor(titleColor);
        }
        Color gradientStart = (Color) StyleSetStyleCreator.createResource(self.getString("gradientStart"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(gradientStart != null){
            header.setGradientStart(gradientStart);
        }
        Color gradientEnd = (Color) StyleSetStyleCreator.createResource(self.getString("gradientEnd"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(gradientEnd != null){
            header.setGradientEnd(gradientEnd);
        }
        Color separatorColor = (Color) StyleSetStyleCreator.createResource(self.getString("separatorColor"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(separatorColor != null){
            header.setSeparatorColor(separatorColor);
        }
        Image image = (Image) StyleSetStyleCreator.createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            header.setImage(image);
        }
        //字体
        Font titleFont = (Font) StyleSetStyleCreator.createResource(self.getString("titleFont"), 
                "xworker.swt.graphics.Font", "fontData", actionContext);
        if(titleFont != null){
            header.setTitleFont(titleFont);
        }
		
		//创建子节点
		actionContext.peek().put("parent", header);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		actionContext.g().put(self.getMetadata().getName(), header);
		Designer.attach(header, self.getMetadata().getPath(), actionContext);
		return header;
	}
}
