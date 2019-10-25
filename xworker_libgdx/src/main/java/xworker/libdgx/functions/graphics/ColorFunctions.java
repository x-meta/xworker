package xworker.libdgx.functions.graphics;

import org.xmeta.ActionContext;

import com.badlogic.gdx.graphics.Color;

public class ColorFunctions {
	public static Object black(ActionContext actionContext){
		return Color.BLACK;
	}
	
	public static Object blue(ActionContext actionContext){
		return Color.BLUE;
	}
	
	public static Object clear(ActionContext actionContext){
		return Color.CLEAR;
	}
	
	public static Object cyan(ActionContext actionContext){
		return Color.CYAN;
	}
	
	public static Object darkGray(ActionContext actionContext){
		return Color.DARK_GRAY;
	}
	
	public static Object gray(ActionContext actionContext){
		return Color.GRAY;
	}
	
	public static Object green(ActionContext actionContext){
		return Color.GREEN;
	}
	
	public static Object lightGray(ActionContext actionContext){
		return Color.LIGHT_GRAY;
	}
	
	public static Object magenta(ActionContext actionContext){
		return Color.MAGENTA;
	}
	
	public static Object orange(ActionContext actionContext){
		return Color.ORANGE;
	}
	
	public static Object pink(ActionContext actionContext){
		return Color.PINK;
	}
	
	public static Object red(ActionContext actionContext){
		return Color.RED;
	}
	
	public static Object white(ActionContext actionContext){
		return Color.WHITE;
	}
	
	public static Object yellow(ActionContext actionContext){
		return Color.YELLOW;
	}
	
	public static Object createColorWithColor(ActionContext actionContext){
		Color color = (Color) actionContext.get("color");
		return new Color(color);
	}
	
	public static Object createColor(ActionContext actionContext){
		return new Color();
	}
	
	public static Object createColorWithRGBA(ActionContext actionContext){
		Number r = (Number) actionContext.get("r");
		Number g = (Number) actionContext.get("g");
		Number b = (Number) actionContext.get("b");
		Number a = (Number) actionContext.get("a");
		
		return new Color(r.floatValue(), g.floatValue(), b.floatValue(), a.floatValue());
	}
	
	public static Object createColorWithRgb8888(ActionContext actionContext){
		Number rgb8888 = (Number) actionContext.get("rgb8888");
		return new Color(rgb8888.intValue());
	}
}
