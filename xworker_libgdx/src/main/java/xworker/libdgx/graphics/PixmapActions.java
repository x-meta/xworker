package xworker.libdgx.graphics;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;

import ognl.OgnlException;
import xworker.libdgx.ConstructException;

public class PixmapActions {
	public static Pixmap create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Pixmap item = null;
		String constructor = self.getString("constructor");
		if("encodedData_offset_len".equals(constructor)){
			byte[] encodedData = (byte[]) actionContext.get("encodedData");
			int offset = self.getInt("offset");
			int len = self.getInt("len");
			item = new Pixmap(encodedData, offset, len);
		}else if("file".equals(constructor)){
			FileHandle file = UtilData.getObjectByType(self, "file", FileHandle.class, actionContext);
			item = new Pixmap(file);
		}else if("pixmap".equals(constructor)){
			Gdx2DPixmap pixmap = UtilData.getObjectByType(self, "pixmap", Gdx2DPixmap.class, actionContext);
			item = new Pixmap(pixmap);
		}else if("width_height_format".equals(constructor)){
			item = new Pixmap(self.getInt("width"), self.getInt("height"), getFormat(self.getString("format")));
		}else{
			throw new ConstructException(self);
		}
		
		if(self.getStringBlankAsNull("color") != null){
			Color color = UtilData.getObjectByType(self, "color", Color.class, actionContext);
			if(color != null){
				item.setColor(color);
			}
		}
		actionContext.getScope(0).put(self.getMetadata().getName(), item);
		return item;
	}
	
	public static Pixmap.Format getFormat(String format){
		if("Alpha".equals(format)){
			return Pixmap.Format.Alpha;
		}else if("Intensity".equals(format)){
			return Pixmap.Format.Intensity;
		}else if("LuminanceAlpha".equals(format)){
			return Pixmap.Format.LuminanceAlpha;
		}else if("RGB565".equals(format)){
			return Pixmap.Format.RGB565;
		}else if("RGB888".equals(format)){
			return Pixmap.Format.RGB888;
		}else if("RGBA4444".equals(format)){
			return Pixmap.Format.RGBA4444;
		}else if("RGBA8888".equals(format)){
			return Pixmap.Format.RGBA8888;
		}else {
			return Pixmap.Format.RGB888;
		} 
	}
}
