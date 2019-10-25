package xworker.swt.nebula;

import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.SWT;
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

public class GalleryActions {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Object obj = SwtUtils.createNoSupportRAP(self, actionContext);
		if(obj != null) {
			return obj;
		}
		
		Action styleAction = World.getInstance().getAction("xworker.swt.widgets.Composite/@scripts/@getStyles");
		int style = (Integer) styleAction.run(actionContext);
		if(self.getBoolean("VIRTUAL")) {
			style = style | SWT.VIRTUAL;
		}
		style = style | SwtUtils.getSWT(self.getStringBlankAsNull("selection"));
		style = style | SwtUtils.getSWT(self.getStringBlankAsNull("scroll"));
		
		Composite parent = actionContext.getObject("parent");
		Gallery gallery = new Gallery(parent, style);
		
		int higherQualityDelay = self.getInt("higherQualityDelay");
		if(higherQualityDelay > 0) {
			gallery.setHigherQualityDelay(higherQualityDelay);
		}	
		gallery.setAntialias(SwtUtils.getSWT(self.getStringBlankAsNull("antialias")));
		gallery.setInterpolation(SwtUtils.getSWT(self.getStringBlankAsNull("interpolation")));
		if(self.getStringBlankAsNull("lowQualityOnUserAction") != null) {
			gallery.setLowQualityOnUserAction(self.getBoolean("lowQualityOnUserAction"));
		}
		
		if(self.getStringBlankAsNull("useControlColors") != null) {
			gallery.setUseControlColors(self.getBoolean("useControlColors"));
		}
		gallery.setVirtualGroups(self.getBoolean("virtualGroups"));
		gallery.setVirtualGroupsCompatibilityMode(self.getBoolean("compatibilityMode"));
		if(self.getStringBlankAsNull("virtualGroupDefaultItemCount") != null) {
			gallery.setVirtualGroupDefaultItemCount(self.getInt("virtualGroupCompatibilityMode"));
		}
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", gallery);
		try{
		    self.doAction("init", actionContext);
		}finally{
		    actionContext.pop();
		}
		
		//创建子节点
		actionContext.peek().put("parent", gallery);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		actionContext.g().put(self.getMetadata().getName(), gallery);
		Designer.attach(gallery, self.getMetadata().getPath(), actionContext);
		return gallery;
	}
	
	public static Object createItem(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Object p = actionContext.getObject("parent");
		GalleryItem item = null;
		if(p instanceof Gallery) {
			item = new GalleryItem((Gallery) p, SWT.NONE);
		}else {
			item = new GalleryItem((GalleryItem) p , SWT.NONE);
		}
		
		String text = self.getString("text", null, actionContext);
		if(text != null) {
			item.setText(text);
		}
		
		actionContext.peek().put("parent", item.getParent());
		Color foreground = (Color) StyleSetStyleCreator.createResource(self.getString("foreground"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(foreground != null){
            item.setForeground(foreground);
        }
        Color background = (Color) StyleSetStyleCreator.createResource(self.getString("background"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(foreground != null){
            item.setForeground(background);
        }
        Image image = (Image) StyleSetStyleCreator.createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            item.setImage(image);
        }
        //字体
        Font font = (Font) StyleSetStyleCreator.createResource(self.getString("font"), 
                "xworker.swt.graphics.Font", "fontData", actionContext);
        if(font != null){
            item.setFont(font);
        }
        
        //创建子节点
        actionContext.peek().put("parent", item);
        for(Thing child : self.getChilds()) {
        	child.doAction("create", actionContext);
        }
        
        item.setExpanded(self.getBoolean("expanded"));
        
        actionContext.g().put(self.getMetadata().getName(), item);
        return item;
	}
}
