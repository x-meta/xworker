package xworker.libdgx.map.tiled.renderers;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricStaggeredTiledMapRenderer;

import ognl.OgnlException;
import xworker.libdgx.ConstructException;

public class IsometricStaggeredTiledMapRendererActions {
	public static BatchTiledMapRenderer create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		IsometricStaggeredTiledMapRenderer item = null;
		String constructor = self.getStringBlankAsNull("constructor");
		
		if("map".equals(constructor)){
			TiledMap map = UtilData.getObjectByType(self, "map", TiledMap.class, actionContext);
			item = new IsometricStaggeredTiledMapRenderer(map);
		}else if("map_batch".equals(constructor)){
			TiledMap map = UtilData.getObjectByType(self, "map", TiledMap.class, actionContext);
			Batch batch = UtilData.getObjectByType(self, "batch", Batch.class, actionContext);
			item = new IsometricStaggeredTiledMapRenderer(map, batch);
		}else if("map_unitScale".equals(constructor)){
			TiledMap map = UtilData.getObjectByType(self, "map", TiledMap.class, actionContext);
			item = new IsometricStaggeredTiledMapRenderer(map, self.getFloat("unitScale"));
		}else if("map_unitScale_batch".equals(constructor)){
			TiledMap map = UtilData.getObjectByType(self, "map", TiledMap.class, actionContext);
			Batch batch = UtilData.getObjectByType(self, "batch", Batch.class, actionContext);
			item = new IsometricStaggeredTiledMapRenderer(map, self.getFloat("unitScale"), batch);
		}else{
			throw new ConstructException(self);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		return item;
	}
}
