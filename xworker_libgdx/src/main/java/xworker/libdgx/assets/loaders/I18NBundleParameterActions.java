package xworker.libdgx.assets.loaders;

import java.util.Locale;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.assets.loaders.I18NBundleLoader.I18NBundleParameter;

public class I18NBundleParameterActions {
	public static I18NBundleParameter create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		I18NBundleParameter item = null;
		String locale = self.getStringBlankAsNull("locale");
		String encoding = self.getStringBlankAsNull("encoding");
		
		if(locale != null && encoding != null){
			item = new I18NBundleParameter(Locale.forLanguageTag(locale), encoding);
		}else if(locale != null){
			item = new I18NBundleParameter(Locale.forLanguageTag(locale));
		}else{
			item = new I18NBundleParameter();
		}
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		return item;
		
	}
}
