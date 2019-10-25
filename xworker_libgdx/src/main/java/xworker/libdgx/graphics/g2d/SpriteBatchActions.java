package xworker.libdgx.graphics.g2d;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import ognl.OgnlException;

public class SpriteBatchActions {
	public static SpriteBatch create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		SpriteBatch item = null;
		String constructor = self.getString("constructor");
		if("default".equals(constructor)){
			item = new SpriteBatch();
		}else if("size".equals(constructor)){
			int size = self.getInt("size");
			item = new SpriteBatch(size);
		}else if("size_defaultShader".equals(constructor)){
			int size = self.getInt("size");
			ShaderProgram defaultShader = UtilData.getObjectByType(self, "defaultShader", ShaderProgram.class, actionContext);
			item = new SpriteBatch(size, defaultShader);
		}else{
			throw new ActionException("No matched constructors, thing=" + self.getMetadata().getPath());
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		return item;
	}
}
