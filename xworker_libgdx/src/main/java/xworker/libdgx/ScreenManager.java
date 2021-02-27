package xworker.libdgx;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

import xworker.lang.executor.Executor;

/**
 * 由于LwjglApplication似乎只能在一个VM中启动一个，因此创建一个默认的一直执行的默认应用
 * 
 * @author Administrator
 *
 */
public class ScreenManager {
	private static final String TAG = ScreenManager.class.getName();
	
	private static LwjglApplication app;
	private static Shell shell;
	private static ActionContext actionContext;
	
	private static Map<String, Screen> screens = new HashMap<String, Screen>();
	
	public static void init(Shell shell, LwjglApplication app, ActionContext actionContext){
		ScreenManager.shell = shell;
		ScreenManager.app = app;
		ScreenManager.actionContext = actionContext;
	}
	
	public static void init(){
		if(shell == null || shell.isDisposed()){
			actionContext = new ActionContext();
			app = null;
			final Thing shellThing = World.getInstance().getThing("xworker.libgdx.LwjglApplicationShell");
			new Thread(new Runnable(){
				public void run(){
					Display display = new Display ();
					actionContext.put("parent", display);
			        
					try{
				        Shell shell = (Shell) shellThing.doAction("create", actionContext);
				        if(shellThing.getBoolean("visible", true)){
				        	shell.open ();
				        }
				        
				        ScreenManager.shell = shell;
				        ScreenManager.app = (LwjglApplication) actionContext.get("lwjgl");
				        if(Gdx.input.getInputProcessor() == null){
					        InputMultiplexer multiplexer = new InputMultiplexer(); // 多输入接收器			        
					        Gdx.input.setInputProcessor(multiplexer);
				        }
					    while (!shell.isDisposed ()) {
					        if (!display.readAndDispatch ()) display.sleep ();
					    }
					}catch(Exception e){
						Executor.error(TAG, "start libgdx error");
					}
				    display.dispose ();
				}
			}).start(); 
			
			while(app == null){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void setScreen(String thingPath){
		Thing thing = World.getInstance().getThing(thingPath);
		
		setScreen(thing);
	}
	
	public static void setScreen(final Thing screenThing){
		ScreenManager.init();
		
		Runnable run = new Runnable(){
			public void run(){
				try{
					String path = screenThing.getMetadata().getPath();
					XWorkerScreen screen = (XWorkerScreen) ScreenManager.getScreen(path);
					boolean recreate = true;
					if(screen != null && screen.lastModified == screenThing.getMetadata().getLastModified()){
						//recreate = false;
					}
					if(screen != null && screen.isDisposed()){
						recreate = true;
					}
					
					if(recreate){
						screen = new XWorkerScreen(screenThing);
					}
					
					ScreenManager.setScreen(path, screen);
				}catch(Exception e){
					Executor.error(TAG, "set screen error, thing=" + screenThing.getMetadata().getPath(), e);
				}
			}
		};
		app.postRunnable(run);
	}
	
	public static XWorkerScreen getCurrentScreen(){
		GameDefault game = (GameDefault) app.getApplicationListener();
		return (XWorkerScreen) game.getScreen();		
	}
	
	/**
	 * 设置应用或游戏。
	 * 
	 * @param path
	 * @param screen
	 */
	public static void setScreen(String path, final XWorkerScreen screen){
		//默认应该是它
		GameDefault game = (GameDefault) app.getApplicationListener();
		XWorkerScreen oldScreen = (XWorkerScreen) game.getScreen();		
		if(oldScreen != null && oldScreen.getCurrentStage() != null){
			InputProcessor inp = Gdx.input.getInputProcessor();
			if(inp instanceof InputMultiplexer){
				((InputMultiplexer) inp).removeProcessor(oldScreen.getCurrentStage());			
			}
		}
				
		game.setScreen(screen);
		if(screen.getCurrentStage() != null){
			InputProcessor pro = Gdx.input.getInputProcessor();
			if(pro instanceof InputMultiplexer){
				((InputMultiplexer) pro).addProcessor(screen.getCurrentStage());
			}
		}

		//初始化菜单
		final Menu menu = (Menu) actionContext.get("menu");		
		if(menu != null && !menu.isDisposed()){
			menu.getDisplay().asyncExec(new Runnable(){
				public void run(){
					for(MenuItem item : menu.getItems()){
						item.dispose();
					}
					
					screen.initMenu(menu);
					
					//改变屏幕大小
					screen.init(shell);
				}
			});		
		}
		
		//默认销毁先前的Screen
		if(oldScreen != null && oldScreen != screen){
			if(oldScreen instanceof XWorkerScreen){
				disposeScreen((XWorkerScreen) oldScreen);
			}else{
				oldScreen.dispose();
			}
		}
		
		screens.put(path, screen);
	}
	
	/**
	 * 移除并销毁一个Screen。
	 * 
	 * @param path
	 */
	public static void disposeScreen(String path){
		Screen old = screens.get(path);
		if(old != null){
			old.dispose();
			
			screens.remove(path);
		}
	}
	
	public static void disposeScreen(XWorkerScreen screen){
		String path = screen.getThing().getMetadata().getPath();
		screens.remove(path);
		screen.dispose();
	}
	
	public static Screen getScreen(String path){
		return screens.get(path);
	}
}
