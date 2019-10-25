package xworker.libdgx.graphics.g2d;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import ognl.OgnlException;
import xworker.game.ChineseChars;

public class FreeTypeActions {
	public static BitmapFont create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		FreeTypeFontParameter param = new FreeTypeFontParameter();
		FileHandle fileHandle = UtilData.getObjectByType(self, "ttfFile", FileHandle.class, actionContext);
		FreeTypeFontGenerator g = new FreeTypeFontGenerator(fileHandle);
		param.size = self.getInt("size");
		param.characters = ChineseChars.CHINESE_CHARS;
		param.flip = self.getBoolean("flip");
		BitmapFont font = g.generateFont(param);
		g.dispose();
		actionContext.getScope(0).put(self.getMetadata().getName(), font);
		
		return font;
	}
}
