package xworker.libdgx.functions.utils;

import org.xmeta.ActionContext;

import com.badlogic.gdx.utils.Scaling;

public class ScalingFunctions {
	public static Scaling fill(ActionContext actionContext){
		return Scaling.fill;
	}
	
	public static Scaling fillX (ActionContext actionContext){
		return Scaling.fillX ;
	}
	
	public static Scaling fillY(ActionContext actionContext){
		return Scaling.fillY;
	}
	
	public static Scaling fit(ActionContext actionContext){
		return Scaling.fit;
	}
	
	public static Scaling none(ActionContext actionContext){
		return Scaling.none;
	}
	
	public static Scaling stretch(ActionContext actionContext){
		return Scaling.stretch;
	}
	
	public static Scaling stretchX(ActionContext actionContext){
		return Scaling.stretchX;
	}
	
	public static Scaling stretchY(ActionContext actionContext){
		return Scaling.stretchY;
	}
}
