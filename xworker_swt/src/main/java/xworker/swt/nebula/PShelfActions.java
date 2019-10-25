package xworker.swt.nebula;

import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;
import xworker.swt.style.StyleSetStyleCreator;
import xworker.swt.util.SwtUtils;

public class PShelfActions {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Object obj = SwtUtils.createNoSupportRAP(self, actionContext);
		if(obj != null) {
			return obj;
		}
		
		Action styleAction = World.getInstance().getAction("xworker.swt.widgets.Composite/@scripts/@getStyles");
		int style = (Integer) styleAction.run(actionContext);
		style = style | SwtUtils.getSWT(self.getString("SIMPLE"));
		
		Composite parent = actionContext.getObject("parent");
		PShelf pshelf = new PShelf(parent, style);
		
		if(self.getAttribute("animationSpeed") != null) {
			pshelf.setAnimationSpeed(self.getDouble("animationSpeed"));
		}		
		pshelf.setRedrawOnAmination(self.getBoolean("redrawOnAnimation"));
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", pshelf);
		try{
		    self.doAction("init", actionContext);
		}finally{
		    actionContext.pop();
		}
		
		//创建子节点
		actionContext.peek().put("parent", pshelf);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		actionContext.g().put(self.getMetadata().getName(), pshelf);
		Designer.attach(pshelf, self.getMetadata().getPath(), actionContext);
		return pshelf;
	}
	
	public static Object createItem(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		PShelf pshelf = actionContext.getObject("parent");
		PShelfItem item = new PShelfItem(pshelf, SWT.NONE);
		item.getBody().setData(Designer.DATA_DESIGNER_REAL_PARENT, pshelf);
		String text = self.getStringBlankAsNull("text");
		if(text != null) {
			item.setText(text);
		}
		
		actionContext.peek().put("parent", item.getBody());
		Image image = (Image) StyleSetStyleCreator.createResource(self.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            item.setImage(image);
        }
        
        if(item.getBody().getLayout() == null) {
        	//为确保可以显示，创建一个FillLayout
        	item.getBody().setLayout(new FillLayout());
        }
        
        actionContext.peek().put("parent", item.getBody());
        for(Thing child : self.getChilds()) {
        	child.doAction("create", actionContext);
        }
        
        //item的body也绑定pshelf的事物，否则无法进入动态设计
        Designer.attach(item.getBody(), (String) pshelf.getData(Designer.DATA_THING), actionContext);
        Designer.attach(item, self.getMetadata().getPath(), actionContext);
        actionContext.g().put(self.getMetadata().getName(), item);
        
        return item;
	}
	
	public static PShelfItem createItem(ActionContext actionContext, Thing itemThing, int index) {
		PShelf pshelf = actionContext.getObject("parent");
		PShelfItem item = new PShelfItem(pshelf, SWT.NONE, index);
		item.getBody().setData(Designer.DATA_DESIGNER_REAL_PARENT, pshelf);
		String text = itemThing.getStringBlankAsNull("text");
		if(text != null) {
			item.setText(text);
		}
		
		actionContext.peek().put("parent", item.getBody());
		Image image = (Image) StyleSetStyleCreator.createResource(itemThing.getString("image"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            item.setImage(image);
        }
        
        if(item.getBody().getLayout() == null) {
        	//为确保可以显示，创建一个FillLayout
        	item.getBody().setLayout(new FillLayout());
        }
        
        actionContext.peek().put("parent", item.getBody());
        for(Thing child : itemThing.getChilds()) {
        	child.doAction("create", actionContext);
        }
        
        Designer.attach(item.getBody(), (String) pshelf.getData(Designer.DATA_THING), actionContext);
        Designer.attach(item, itemThing.getMetadata().getPath(), actionContext);
        actionContext.g().put(itemThing.getMetadata().getName(), item);
        return item;
	}
}
