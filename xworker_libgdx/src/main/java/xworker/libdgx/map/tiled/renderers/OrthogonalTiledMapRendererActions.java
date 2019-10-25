package xworker.libdgx.map.tiled.renderers;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import ognl.OgnlException;
import xworker.libdgx.ConstructException;

public class OrthogonalTiledMapRendererActions {
	public static OrthogonalTiledMapRenderer create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		OrthogonalTiledMapRenderer item = null;
		String constructor = self.getStringBlankAsNull("constructor");
		
		if("map".equals(constructor)){
			TiledMap map = UtilData.getObjectByType(self, "map", TiledMap.class, actionContext);
			item = new OrthogonalTiledMapRenderer(map);
		}else if("map_batch".equals(constructor)){
			TiledMap map = UtilData.getObjectByType(self, "map", TiledMap.class, actionContext);
			Batch batch = UtilData.getObjectByType(self, "batch", Batch.class, actionContext);
			item = new OrthogonalTiledMapRenderer(map, batch);
		}else if("map_unitScale".equals(constructor)){
			TiledMap map = UtilData.getObjectByType(self, "map", TiledMap.class, actionContext);
			item = new OrthogonalTiledMapRenderer(map, self.getFloat("unitScale"));
		}else if("map_unitScale_batch".equals(constructor)){
			TiledMap map = UtilData.getObjectByType(self, "map", TiledMap.class, actionContext);
			Batch batch = UtilData.getObjectByType(self, "batch", Batch.class, actionContext);
			item = new OrthogonalTiledMapRenderer(map, self.getFloat("unitScale"), batch);
		}else{
			throw new ConstructException(self);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		return item;
	}
}
