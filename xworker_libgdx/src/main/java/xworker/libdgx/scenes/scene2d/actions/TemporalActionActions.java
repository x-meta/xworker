package xworker.libdgx.scenes.scene2d.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import xworker.libdgx.ConstantsUtils;

public class TemporalActionActions {
	public static void init(Thing self, TemporalAction action, ActionContext actionContext){
		if(self.getStringBlankAsNull("duration") != null){
			action.setDuration(self.getFloat("duration"));
		}
		
		String interpolation = self.getStringBlankAsNull("interpolation");
		if(interpolation != null){
			Interpolation i = ConstantsUtils.getInterpolation(interpolation);
			if(i != null){
				action.setInterpolation(i);
			}
		}
		
		if(self.getStringBlankAsNull("reverse") != null){
			action.setReverse(self.getBoolean("reverse"));
		}
		
		if(self.getStringBlankAsNull("time") != null){
			action.setTime(self.getFloat("time"));
		}
	}
}
