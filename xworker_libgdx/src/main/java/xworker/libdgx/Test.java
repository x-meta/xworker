package xworker.libdgx;

import org.xmeta.ActionContext;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import xworker.libdgx.functions.StageApplication;

public class Test {
	public static void run(ActionContext actionContext){
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "xworker-gdx";
		//cfg.useGL20 = false;
		cfg.width = 480;
		cfg.height = 320;
		cfg.forceExit = false;
		
		new LwjglApplication(new StageApplication(400, 300, true), cfg);
	}
	
	public static void main(String args[]){
		try{
			run(new ActionContext());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
