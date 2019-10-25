package xworker.libdgx.files;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class FileHandleActions {
	public static FileHandle create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String type = self.getString("type");
		FileHandle file = null;
		if("Internal".equals(type)){
			file = Gdx.files.internal(self.getString("fileName"));
		}else if("Absolute".equals(type)){
			file = Gdx.files.absolute(self.getString("fileName"));
		}else if("Classpath".equals(type)){
			file = Gdx.files.classpath(self.getString("fileName"));
		}else if("External".equals(type)){
			file = Gdx.files.external(self.getString("fileName"));
		}else if("Local".equals(type)){
			file = Gdx.files.local(self.getString("fileName"));
		}else{
			file = Gdx.files.internal(self.getString("fileName"));
		}
		
		actionContext.getScope(0).put(self.getMetadata().getName(), file);
		return file;
	}
}
