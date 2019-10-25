package xworker.libdgx.utils.viewport;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import ognl.OgnlException;
import xworker.libdgx.ConstructException;
import xworker.libdgx.utils.ScalingActions;

public class ViewportActions {
	public static ExtendViewport createExtendViewport(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String constructor = self.getString("constructor");
		ExtendViewport viewport = null;
		if("minWorldWidth_minWorldHeight".equals(constructor)){
			viewport = new ExtendViewport(self.getFloat("minWorldWidth"), self.getFloat("minWorldHeight"));
		}else if("minWorldWidth_minWorldHeight_camera".equals(constructor)){
			Camera camera = (Camera) actionContext.get("camera");
			viewport = new ExtendViewport(self.getFloat("minWorldWidth"), self.getFloat("minWorldHeight"), camera);
		}else if("minWorldWidth_minWorldHeight_maxWorldWidth_maxWorldHeight".equals(constructor)){
			viewport = new ExtendViewport(self.getFloat("minWorldWidth"), self.getFloat("minWorldHeight"), 
					self.getFloat("maxWorldWidth"), self.getFloat("maxWorldHeight"));
		}else if("minWorldWidth_minWorldHeight_maxWorldWidth_maxWorldHeight_camera".equals(constructor)){
			Camera camera = (Camera) actionContext.get("camera");
			viewport = new ExtendViewport(self.getFloat("minWorldWidth"), self.getFloat("minWorldHeight"), 
					self.getFloat("maxWorldWidth"), self.getFloat("maxWorldHeight"), camera);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), viewport);
		return viewport;
	}
	
	public static FillViewport createFillViewport(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String constructor = self.getString("constructor");
		FillViewport viewport = null;
		if("worldWidth_worldHeight".equals(constructor)){
			viewport = new FillViewport(self.getFloat("worldWidth"), self.getFloat("worldHeight"));
		}else if("worldWidth_worldHeight_camera".equals(constructor)){
			Camera camera = (Camera) actionContext.get("camera");
			viewport = new FillViewport(self.getFloat("worldWidth"), self.getFloat("worldHeight"), camera);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), viewport);
		return viewport;
	}
	
	public static FitViewport createFitViewport(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String constructor = self.getString("constructor");
		FitViewport viewport = null;
		if("worldWidth_worldHeight".equals(constructor)){
			viewport = new FitViewport(self.getFloat("worldWidth"), self.getFloat("worldHeight"));
		}else if("worldWidth_worldHeight_camera".equals(constructor)){
			Camera camera = (Camera) actionContext.get("camera");
			viewport = new FitViewport(self.getFloat("worldWidth"), self.getFloat("worldHeight"), camera);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), viewport);
		return viewport;
	}
	
	public static ScalingViewport createScalingViewport(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String constructor = self.getString("constructor");
		ScalingViewport viewport = null;
		Scaling scaling = ScalingActions.getScaling("scaling");
		if("worldWidth_worldHeight".equals(constructor)){
			viewport = new ScalingViewport(scaling, self.getFloat("worldWidth"), self.getFloat("worldHeight"));
		}else if("worldWidth_worldHeight_camera".equals(constructor)){
			Camera camera = (Camera) actionContext.get("camera");
			viewport = new ScalingViewport(scaling, self.getFloat("worldWidth"), self.getFloat("worldHeight"), camera);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), viewport);
		return viewport;
	}
	
	public static StretchViewport createStretchViewport(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String constructor = self.getString("constructor");
		StretchViewport viewport = null;
		if("worldWidth_worldHeight".equals(constructor)){
			viewport = new StretchViewport(self.getFloat("worldWidth"), self.getFloat("worldHeight"));
		}else if("worldWidth_worldHeight_camera".equals(constructor)){
			Camera camera = (Camera) actionContext.get("camera");
			viewport = new StretchViewport(self.getFloat("worldWidth"), self.getFloat("worldHeight"), camera);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), viewport);
		return viewport;
	}
	
	public static ScreenViewport createScreenViewport(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		String constructor = self.getString("constructor");
		ScreenViewport viewport = null;
		if("default".equals(constructor)){
			viewport = new ScreenViewport();
		}else if("camera".equals(constructor)){
			Camera camera = UtilData.getObjectByType(self, "camera", Camera.class, actionContext);
			viewport = new ScreenViewport(camera);
		}else{
			throw new ConstructException(self);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), viewport);
		return viewport;
	}
}
