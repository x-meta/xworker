package xworker.libdgx.graphics;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.graphics.Color;

public class ColorActions {
	public static Color create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Color color = null;
		if(self.getStringBlankAsNull("rgba8888") != null){
			color = new Color(self.getInt("rgba8888"));
		}else{
			color = new Color(self.getInt("r"), self.getInt("g"), self.getInt("b"), self.getInt("a"));
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), color);
		
		return color;
	}
}
