package xworker.swt.nebula;

import org.eclipse.nebula.effects.stw.TransitionManager;
import org.eclipse.nebula.effects.stw.Transitionable;
import org.eclipse.nebula.effects.stw.transitions.CubicRotationTransition;
import org.eclipse.nebula.effects.stw.transitions.FadeTransition;
import org.eclipse.nebula.effects.stw.transitions.SlideTransition;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TabFolder;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.style.StyleSetStyleCreator;

public class TransitionManagerActions {
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Object parent = actionContext.get("parent");		
		TransitionManager manager = null;
		if(parent instanceof CTabFolder) {
			manager = new TransitionManager((CTabFolder) parent); 
		}else if(parent instanceof TabFolder) {
			manager = new TransitionManager((TabFolder) parent);
		}else if(parent instanceof Transitionable) {
			manager  = new TransitionManager((Transitionable) parent);
		}else {
			return;
		}
		
		Color background = (Color) StyleSetStyleCreator.createResource(self.getString("background"), 
                "xworker.swt.graphics.Color", "rgb", actionContext);
        if(background != null){
            manager.setBackground(background);
        }
        Image image = (Image) StyleSetStyleCreator.createResource(self.getString("backgroundImage​"), 
                "xworker.swt.graphics.Image", "imageFile", actionContext);
        if(image != null){
            manager.setBackgroundImage(image);
        }
        
        actionContext.g().put(self.getMetadata().getName(), manager);
        
        //创建子节点
        actionContext.peek().put("parent", manager);
        for(Thing child : self.getChilds()) {
        	child.doAction("create", actionContext);
        }        
	}
	
	public static void createCubicRotationTransition(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		TransitionManager manager = actionContext.getObject("parent");
		
		CubicRotationTransition t = new CubicRotationTransition(manager);
		manager.setTransition(t);
		if(self.getStringBlankAsNull("fps") != null) {
			t.setFPS(self.getLong("fps"));
		}
		if(self.getStringBlankAsNull("totalTransitionTime​") != null) {
			t.setTotalTransitionTime(self.getLong("totalTransitionTime​"));
		}
		if(self.getStringBlankAsNull("quality​") != null) {
			t.setQuality(self.getDouble("quality​"));
		}
	}
	
	public static void createFadeTransition(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		TransitionManager manager = actionContext.getObject("parent");
		
		FadeTransition t = new FadeTransition(manager);
		manager.setTransition(t);
		if(self.getStringBlankAsNull("fps") != null) {
			t.setFPS(self.getLong("fps"));
		}
		if(self.getStringBlankAsNull("totalTransitionTime​") != null) {
			t.setTotalTransitionTime(self.getLong("totalTransitionTime​"));
		}
		if(self.getStringBlankAsNull("fadeInStart​​") != null) {
			t.setFadeInStart(self.getDouble("fadeInStart​​"));
		}
		if(self.getStringBlankAsNull("fadeInStop​​​") != null) {
			t.setFadeInStop(self.getDouble("fadeInStop​​​"));
		}
		if(self.getStringBlankAsNull("fadeOutStart​​​") != null) {
			t.setFadeOutStart(self.getDouble("fadeOutStart​​​"));
		}
		if(self.getStringBlankAsNull("fadeOutStop​​​") != null) {
			t.setFadeOutStop(self.getDouble("fadeOutStop​​​"));
		}
	}
	
	public static void createSlideTransition(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		TransitionManager manager = actionContext.getObject("parent");
		
		SlideTransition t = new SlideTransition(manager);
		manager.setTransition(t);
		if(self.getStringBlankAsNull("fps") != null) {
			t.setFPS(self.getLong("fps"));
		}
		if(self.getStringBlankAsNull("totalTransitionTime​") != null) {
			t.setTotalTransitionTime(self.getLong("totalTransitionTime​"));
		}
	}
}
