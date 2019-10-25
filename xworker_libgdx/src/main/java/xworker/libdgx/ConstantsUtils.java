package xworker.libdgx;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

public class ConstantsUtils {
	public static int getAlign(String align){
		if("top".equals(align)){
			return Align.top;
		}else if("center".equals(align)){
			return Align.center;
		}else if("bottom".equals(align)){
			return Align.bottom;
		}else if("left".equals(align)){
			return Align.left;
		}else if("right".equals(align)){
			return Align.right;
		}
		
		return Align.center;
	}
	
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
	
	public static Interpolation getInterpolation(String value){
		if("bounce".equals(value)){
			return Interpolation.bounce;
		}else if("bounceIn".equals(value)){
			return Interpolation.bounceIn;
		}else if("bounceOut".equals(value)){
			return Interpolation.bounceOut;
		}else if("circle".equals(value)){
			return Interpolation.circle;
		}else if("circleIn".equals(value)){
			return Interpolation.circleIn;
		}else if("circleOut".equals(value)){
			return Interpolation.circleOut;
		}else if("elastic".equals(value)){
			return Interpolation.elastic;
		}else if("elasticIn".equals(value)){
			return Interpolation.elasticIn;
		}else if("elasticOut".equals(value)){
			return Interpolation.elasticOut;
		}else if("exp10".equals(value)){
			return Interpolation.exp10;
		}else if("exp10In".equals(value)){
			return Interpolation.exp10In;
		}else if("exp10Out".equals(value)){
			return Interpolation.exp10Out;
		}else if("exp5".equals(value)){
			return Interpolation.exp5;
		}else if("exp5In".equals(value)){
			return Interpolation.exp5In;
		}else if("exp5Out".equals(value)){
			return Interpolation.exp5Out;
		}else if("fade".equals(value)){
			return Interpolation.fade;
		}else if("linear".equals(value)){
			return Interpolation.linear;
		}else if("pow2".equals(value)){
			return Interpolation.pow2;
		}else if("pow2In".equals(value)){
			return Interpolation.pow2In;
		}else if("pow2Out".equals(value)){
			return Interpolation.pow2Out;
		}else if("pow3".equals(value)){
			return Interpolation.pow3;
		}else if("pow3In".equals(value)){
			return Interpolation.pow3In;
		}else if("pow3Out".equals(value)){
			return Interpolation.pow3Out;
		}else if("pow4".equals(value)){
			return Interpolation.pow4;
		}else if("pow4In".equals(value)){
			return Interpolation.pow4In;
		}else if("pow4Out".equals(value)){
			return Interpolation.bounce;
		}else if("bounce".equals(value)){
			return Interpolation.pow4Out;
		}else if("pow5".equals(value)){
			return Interpolation.pow5;
		}else if("pow5In".equals(value)){
			return Interpolation.pow5In;
		}else if("pow5Out".equals(value)){
			return Interpolation.pow5Out;
		}else if("sine".equals(value)){
			return Interpolation.sine;
		}else if("sineIn".equals(value)){
			return Interpolation.sineIn;
		}else if("sineOut".equals(value)){
			return Interpolation.sineOut;
		}else if("swing".equals(value)){
			return Interpolation.swing;
		}else if("swingIn".equals(value)){
			return Interpolation.swingIn;
		}else if("swingOut".equals(value)){
			return Interpolation.swingOut;
		}


		return null;
	}
	
	public static Touchable getTouchable(String value){
		if("childrenOnly".equals(value)){
			return Touchable.childrenOnly;
		}else if("disabled".equals(value)){
			return Touchable.disabled;
		}else if("enabled".equals(value)){
			return Touchable.enabled;
		}
		
		return null;
	}
	
	public static TextureFilter getTextureFilter(String value){
		if("Linear".equals(value)){
			return TextureFilter.Linear;
		}else if("MipMap".equals(value)){
			return TextureFilter.MipMap;
		}else if("MipMapLinearLinear".equals(value)){
			return TextureFilter.MipMapLinearLinear;
		}else if("MipMapLinearNearest".equals(value)){
			return TextureFilter.MipMapLinearNearest;
		}else if("MipMapNearestLinear".equals(value)){
			return TextureFilter.MipMapNearestLinear;
		}else if("MipMapNearestNearest".equals(value)){
			return TextureFilter.MipMapNearestNearest;
		}else if("Nearest".equals(value)){
			return TextureFilter.Nearest;
		}
		
		return null;
	}
	
	public static TextureWrap getTextureWrap(String value){
		if("ClampToEdge".equals(value)){
			return TextureWrap.ClampToEdge;
		}else if("MirroredRepeat".equals(value)){
			return TextureWrap.MirroredRepeat;
		}else if("Repeat".equals(value)){
			return TextureWrap.Repeat;
		
		}
		return null;
	}
	
	public static Format getFormat(String value){
		if("Alpha".equals(value)){
			return Format.Alpha;
		}else if("Intensity".equals(value)){
			return Format.Intensity;
		}else if("LuminanceAlpha".equals(value)){
			return Format.LuminanceAlpha;
		}else if("RGB565".equals(value)){
			return Format.RGB565;
		}else if("RGB888".equals(value)){
			return Format.RGB888;
		}else if("RGBA4444".equals(value)){
			return Format.RGBA4444;
		}else if("RGBA8888".equals(value)){
			return Format.RGBA8888;
		}
		return null;
	}
}
