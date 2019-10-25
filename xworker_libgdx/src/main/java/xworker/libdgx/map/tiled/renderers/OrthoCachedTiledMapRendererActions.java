package xworker.libdgx.map.tiled.renderers;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;

import ognl.OgnlException;
import xworker.libdgx.ConstructException;

public class OrthoCachedTiledMapRendererActions {
	public static OrthoCachedTiledMapRenderer create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		OrthoCachedTiledMapRenderer item = null;
		String constructor = self.getStringBlankAsNull("constructor");
		
		if("map".equals(constructor)){
			TiledMap map = UtilData.getObjectByType(self, "map", TiledMap.class, actionContext);
			item = new OrthoCachedTiledMapRenderer(map);
		}else if("map_unitScale".equals(constructor)){
			TiledMap map = UtilData.getObjectByType(self, "map", TiledMap.class, actionContext);
			item = new OrthoCachedTiledMapRenderer(map, self.getFloat("unitScale"));
		}else if("map_unitScale_cacheSize".equals(constructor)){
			TiledMap map = UtilData.getObjectByType(self, "map", TiledMap.class, actionContext);
			item = new OrthoCachedTiledMapRenderer(map, self.getFloat("unitScale"), self.getInt("cacheSize"));
		}else{
			throw new ConstructException(self);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		return item;
	}
}
