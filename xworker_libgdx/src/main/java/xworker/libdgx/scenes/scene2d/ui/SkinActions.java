package xworker.libdgx.scenes.scene2d.ui;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import ognl.OgnlException;
import xworker.libdgx.ConstructException;

public class SkinActions {
	/**
	 * 把一个 Skin转化为 JSON字符串。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String toJson(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String json = "{";
		Thing resources = self.getThing("resources@0");
		if(resources != null){
			json = json + "\n    resources: {";
			boolean first = true;
			for(Thing child : resources.getChilds()){
				if(first){
					first = false;
					json = json + "\n";
				}else{
					json = json + ",\n";
				}
				json = json + childToJson(child);
			}
			json = json + "\n    }";
		}
		
		Thing styles = self.getThing("styles@0");
		if(styles != null){
			if(resources != null){
				json = json + ",";
			}
			json = json + "\n    styles: {";
			boolean first = true;
			for(Thing child : styles.getChilds()){
				if(first){
					first = false;
					json = json + "\n";
				}else{
					json = json + ",\n";
				}
				json = json + childToJson(child);
			}
			json = json + "\n    }";		
		}
		
		return json + "\n}";
	}
	

	public static String childToJson(Thing child){
		String json = "        " + child.getString("className") + ": {";
		boolean first = true;
		for(Thing c : child.getChilds()){
			if(first){
				first = false;
				json = json + "\n";
			}else{
				json = json + ",\n";
			}
			json = json + "            " + c.getString("name") + ": {";
			List<Thing> attributes = c.getAllAttributesDescriptors();
			boolean hasAttr = false;
			for(Thing attr : attributes){
				if(attr.getMetadata().getPath().startsWith("xworker.gdx.scenes.scene2d.ui.Skin")){
					String name = attr.getString("name");
					if("label".equals(name) || "name".equals(name)){
						continue;
					}
					
					String value = c.getStringBlankAsNull(name);
					if(value != null){
						if(hasAttr){
							json = json + ",";
						}else{
							hasAttr = true;
						}
						json = json + name + ":" + value;
					}
				}
			}
			json = json + "}";
		}
		return json;
	}
	
	public static Skin create(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		String constructor = self.getString("constructor");
		
		Skin skin = null;
		if("default".equals(constructor)){
			skin = new Skin();
		}else if("skinFile".equals(constructor)){
			FileHandle skinFile = UtilData.getObjectByType(self, "skinFile", FileHandle.class, actionContext);
			skin = new Skin(skinFile);
		}else if("skinFile_atlas".equals(constructor)){
			FileHandle skinFile = UtilData.getObjectByType(self, "skinFile", FileHandle.class, actionContext);
			TextureAtlas atlas = (TextureAtlas) actionContext.get("atlas");			
			skin = new Skin(skinFile, atlas);
		}else if("atlas".equals(constructor)){
			TextureAtlas atlas = (TextureAtlas) actionContext.get("atlas");			
			skin = new Skin(atlas);
		}else{
			throw new ConstructException(self);
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), skin);
		return skin;
	}	
}
