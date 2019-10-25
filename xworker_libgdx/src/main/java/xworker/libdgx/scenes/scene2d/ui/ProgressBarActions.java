package xworker.libdgx.scenes.scene2d.ui;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import ognl.OgnlException;

public class ProgressBarActions {
	public static ProgressBar create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		ProgressBar item = null;
		String constructor = self.getString("constructor");
		float min = self.getFloat("min");
		float max = self.getFloat("max");
		float stepSize = self.getFloat("stepSize");
		boolean vertical = self.getBoolean("vertical");
		
		if("style".equals(constructor)){
			ProgressBarStyle style = UtilData.getObjectByType(self, "style", ProgressBarStyle.class, actionContext);
			item = new ProgressBar(min, max, stepSize, vertical, style);
		}else if("skin".equals(constructor)){
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			item = new ProgressBar(min, max, stepSize, vertical, skin);
		}else if("skin_styleName".equals(constructor)){
			String styleName = self.getString("styleName");
			Skin skin = UtilData.getObjectByType(self, "skin", Skin.class, actionContext);
			item = new ProgressBar(min, max, stepSize, vertical, skin, styleName);
		}

		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		init(self, item, actionContext);
		
		return item;
	}
	
	public static void init(Thing self, ProgressBar item , ActionContext actionContext) throws OgnlException{
		WidgetActions.init(self, item, actionContext);
		
		if(self.getStringBlankAsNull("animateDuration") != null){
			item.setAnimateDuration(self.getFloat("animateDuration", 0, actionContext));
		}
		
		if(self.getStringBlankAsNull("disabled") != null){
			item.setDisabled(self.getBoolean("disabled", false, actionContext));
		}
	}
}
