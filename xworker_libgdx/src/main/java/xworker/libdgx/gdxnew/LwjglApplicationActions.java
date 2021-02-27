package xworker.libdgx.gdxnew;

import java.awt.Canvas;
import java.awt.Frame;

import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import xworker.lang.executor.Executor;
import xworker.libdgx.GameDefault;

public class LwjglApplicationActions {
	private static final String TAG = LwjglApplicationActions.class.getName();
	
	/**
	 * 创建LwjglApplication。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static LwjglApplication create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//配置
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = self.getString("title");
		cfg.width = self.getInt("width");
		cfg.height = self.getInt("height");
		cfg.useGL30 = self.getBoolean("useGL30");
		cfg.samples = self.getInt("samples");
		cfg.fullscreen = self.getBoolean("fullscreen");
		LwjglApplicationConfiguration.disableAudio = self.getBoolean("disableAudio");
		cfg.vSyncEnabled = self.getBoolean("vSyncEnabled");
		cfg.audioDeviceSimultaneousSources = self.getInt("audioDeviceSimultaneousSources");
		cfg.audioDeviceBufferSize = self.getInt("audioDeviceBufferSize");
		cfg.audioDeviceBufferCount = self.getInt("audioDeviceBufferCount");
		cfg.foregroundFPS = self.getInt("foregroundFPS");
		cfg.backgroundFPS = self.getInt("backgroundFPS");
		cfg.resizable = self.getBoolean("resizable");
		cfg.forceExit = self.getBoolean("forceExit");
		
		//canvas
		Canvas canvas = null;
		Composite composite = null;
		String canvasVar = self.getStringBlankAsNull("canvasVar");
		if(canvasVar != null){
			canvas = (Canvas) actionContext.get("canvasVar");
		}else if(self.getBoolean("isSWT")){
			composite = (Composite) actionContext.get("parent");
			Frame frame = SWT_AWT.new_Frame(composite);
			canvas = new Canvas();
			frame.add(canvas);
		}
		
		//ApplicationListener
		ApplicationListener appListener = (ApplicationListener) self.doAction("getApplicationListener", actionContext);
		if(appListener == null){
			//throw new ActionException("ApplicationListener is null, path=" + self.getMetadata().getPath());
			appListener = new GameDefault();
		}
		
		LwjglApplication lwjgl = null;
		if(canvas != null){
			lwjgl = new LwjglApplication(appListener, cfg, canvas);
		}else{
			lwjgl = new LwjglApplication(appListener, cfg);
		}
		
		if(composite != null){
			composite.addDisposeListener(new AppDisposer(lwjgl));
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), lwjgl);
		return lwjgl;
	}
	
	/**
	 * 获取ApplicationListener。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static ApplicationListener getApplicationListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String applicationListenerVar = self.getStringBlankAsNull("applicationListenerVar");
		if(applicationListenerVar != null){
			return (ApplicationListener) actionContext.get(applicationListenerVar);
		}
		
		Thing appThing = null;
		String applicationListenerThing = self.getStringBlankAsNull("applicationListenerThing");
		if(applicationListenerThing != null){
			appThing = World.getInstance().getThing(applicationListenerThing);
		}
		
		if(appThing != null){
			return (ApplicationListener) appThing.doAction("create");
		}
		
		return null;
	}

     static	class AppDisposer  implements DisposeListener{
    	 LwjglApplication lwjgl1;

    	 public AppDisposer( LwjglApplication lwjgl1){
    		 this.lwjgl1 = lwjgl1;
    	 }
		@Override
		public void widgetDisposed(DisposeEvent arg0) {
			try{
				//lwjgl1.getApplicationListener().dispose();
				lwjgl1.exit();
			}catch(Exception e){
				Executor.error(TAG, "Application dispose error", e);
			}
		}
    	 
    	 
	}
}
