package xworker.libdgx.utils;

import com.badlogic.gdx.utils.Align;

public class AlignActions {
	public static int getAlign(String name){
		if("bottom".equals(name)){
			return Align.bottom;
		}else if("bottomLeft".equals(name)){
			return Align.bottomLeft;
		}else if("bottomRight".equals(name)){
			return Align.bottomRight;
		}else if("center".equals(name)){
			return Align.center;
		}else if("left".equals(name)){
			return Align.left;
		}else if("right".equals(name)){
			return Align.right;
		}else if("top".equals(name)){
			return Align.top;
		}else if("topLeft".equals(name)){
			return Align.topLeft;
		}else if("topRight".equals(name)){
			return Align.topRight;
		}else{
			return Align.center;
		}	
	}
}
