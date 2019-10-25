package xworker.libdgx.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class PoolManager {
	private static Pool<Vector2> vector2Pool = new Pool<Vector2>(){
		@Override
		protected Vector2 newObject() {
			return new Vector2(0, 0);
		}		
	};
	
	public static Vector2 obtainVector2(float x, float y){
		Vector2 v = vector2Pool.obtain();
		v.set(x, y);
		return v;
	}
	
	public static <T> void free(T t){
		if(t instanceof Vector2){
			vector2Pool.free((Vector2) t);
		}
	}
	
	public static void free(Object... objs){
		for(Object obj : objs){
			free(obj);
		}
	}
}
