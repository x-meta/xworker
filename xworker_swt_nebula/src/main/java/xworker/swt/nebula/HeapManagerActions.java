package xworker.swt.nebula;

import org.eclipse.nebula.widgets.opal.heapmanager.HeapManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;
import xworker.swt.style.StyleSetStyleCreator;

public class HeapManagerActions {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Action styleAction = World.getInstance().getAction("xworker.swt.widgets.Composite/@scripts/@getStyles");
		int style = (Integer) styleAction.run(actionContext);
		Composite parent = actionContext.getObject("parent");
		HeapManager heapManager = new HeapManager(parent, style);
		
		Color barBorderColor = (Color) StyleSetStyleCreator.createResource(self.getString("barBorderColor"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(barBorderColor != null){
        	heapManager.setBarBorderColor(barBorderColor);
        }
        Color barGradientColorMiddleStart = (Color) StyleSetStyleCreator.createResource(self.getString("barGradientColorMiddleStart"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(barGradientColorMiddleStart != null){
        	heapManager.setBarGradientColorMiddleStart(barGradientColorMiddleStart);
        }
        Color barGradientColorTopEnd = (Color) StyleSetStyleCreator.createResource(self.getString("barGradientColorTopEnd"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(barGradientColorTopEnd != null){
        	heapManager.setBarGradientColorTopEnd(barGradientColorTopEnd);
        }
        Color barGradientColorTopStart = (Color) StyleSetStyleCreator.createResource(self.getString("barGradientColorTopStart"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(barGradientColorTopStart != null){
        	heapManager.setBarGradientColorTopStart(barGradientColorTopStart);
        }
        Color barInnerColor = (Color) StyleSetStyleCreator.createResource(self.getString("barInnerColor"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(barInnerColor != null){
        	heapManager.setBarInnerColor(barInnerColor);
        }
        Color barTextColor = (Color) StyleSetStyleCreator.createResource(self.getString("barTextColor"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(barTextColor != null){
        	heapManager.setBarTextColor(barTextColor);
        }
        
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", heapManager);
		try{
		    self.doAction("init", actionContext);
		}finally{
		    actionContext.pop();
		}
		
		//创建子节点
		actionContext.peek().put("parent", heapManager);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
		
		actionContext.g().put(self.getMetadata().getName(), heapManager);
		Designer.attach(heapManager, self.getMetadata().getPath(), actionContext);
		return heapManager;
	}
}
