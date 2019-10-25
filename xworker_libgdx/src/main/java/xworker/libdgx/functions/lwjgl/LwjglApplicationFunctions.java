package xworker.libdgx.functions.lwjgl;

import org.lwjgl.LWJGLException;
import org.xmeta.ActionContext;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class LwjglApplicationFunctions {
	public static Object createLwjglApplication(ActionContext actionContext) throws LWJGLException{
		ApplicationListener applicationListener = (ApplicationListener) actionContext.get("applicationListener");
		LwjglApplicationConfiguration config = (LwjglApplicationConfiguration) actionContext.get("config");
        
		return new LwjglApplication(applicationListener, config);
	}
}
