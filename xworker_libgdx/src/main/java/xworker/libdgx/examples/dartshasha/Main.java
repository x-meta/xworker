package xworker.libdgx.examples.dartshasha;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String args[]){
		try{
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			config.title = "测试";
			config.width = 800;
			config.height = 600;				
			
			new LwjglApplication(new Game(), config);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
