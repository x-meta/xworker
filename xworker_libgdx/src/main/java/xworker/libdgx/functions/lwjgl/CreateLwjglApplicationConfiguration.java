package xworker.libdgx.functions.lwjgl;

import org.xmeta.ActionContext;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class CreateLwjglApplicationConfiguration {
	public static Object createLwjglApplicationConfiguration(ActionContext actionContext){
		Number width = (Number) actionContext.get("width");
		Number height = (Number) actionContext.get("height");
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = (String) actionContext.get("title");
		cfg.width = width.intValue();
		cfg.height = height.intValue();
		cfg.forceExit = false;
		
		return cfg;
	}
}
