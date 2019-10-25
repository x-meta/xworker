package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;

import ognl.OgnlException;

public class SliderActions {
	public static Slider create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		String constructor = self.getString("constructor");
		Slider slider = null;
		
		float min = self.getFloat("min");
		float max = self.getFloat("max");
		float stepSize = self.getFloat("stepSize");
		boolean vertical = self.getBoolean("vertical");
		if("min_max_stepSize_vertical_skin".equals(constructor)){
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			slider = new Slider(min, max, stepSize, vertical, skin);
		}else if("min_max_stepSize_vertical_skin_styleName".equals(constructor)){
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			String styleName = self.getString("styleName");
			slider = new Slider(min, max, stepSize, vertical, skin, styleName);
		}else if("min_max_stepSize_vertical_style".equals(constructor)){
			Slider.SliderStyle style = UtilData.getObjectByType(self, "style", SliderStyle.class, actionContext);
			slider = new Slider(min, max, stepSize, vertical, style);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), slider);
		init(self, slider, actionContext);
		
		return slider;
	}
	
	public static void init(Thing self, Slider item, ActionContext actionContext) throws OgnlException{
		WidgetActions.init(self, item, actionContext);
		
		if(self.getStringBlankAsNull("value") != null){
			item.setValue(self.getFloat("value", 0, actionContext));
		}
		
	}
}
