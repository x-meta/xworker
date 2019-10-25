package xworker.libdgx.utils;

import com.badlogic.gdx.utils.Scaling;

public class ScalingActions {
	public static Scaling getScaling(String scaling){
		if("fill".equals(scaling)){
			return Scaling.fill;
		}else if("fillX".equals(scaling)){
			return Scaling.fillX;
		}else if("fillY".equals(scaling)){
			return Scaling.fillY;
		}else if("fit".equals(scaling)){
			return Scaling.fit;
		}else if("none".equals(scaling)){
			return Scaling.none;
		}else if("stretch".equals(scaling)){
			return Scaling.stretch;
		}else if("stretchX".equals(scaling)){
			return Scaling.stretchX;
		}else if("stretchY".equals(scaling)){
			return Scaling.stretchY;
		}else{
			return Scaling.none;
		}		
	}
}
