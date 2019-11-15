package xworker.swt.app.editors;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilFile;

import xworker.swt.app.IEditor;
import xworker.swt.xworker.Colorer;

public class FileEditor {
	public static Map<String, Object> createDataParams(ActionContext actionContext) throws IOException{
		Object data = actionContext.get("data");
		if(data instanceof File) {
			Map<String, Object> params = new HashMap<String, Object>();
			File file = (File) data;
			if(file.isDirectory()) {
				return null;
			}else {
				String subfix = file.getName();
				int index = subfix.lastIndexOf(".");
				if(index != -1) {
					subfix = subfix.substring(index + 1, subfix.length()).toLowerCase();
				}else {
					params.put(IEditor.EDITOR_THING, World.getInstance().getThing("xworker.swt.app.editors.SystemFileEditor"));
					params.put(IEditor.EDITOR_ID, "File:" + file.getCanonicalPath());
					params.put("fileName", file.getAbsolutePath());
					return params;
				}
				
				//是否是文本文件
				boolean isText = false;
				if("txt".equals(subfix)) {
					isText = true;
					
				}else {					
					for(String type : Colorer.getSupportCodeTypes()){
					    if(type.toLowerCase().equals(subfix)) {
					    	isText = true;
					    	break;
					    }
					}
				}
				
				if(isText) {
					params.put(IEditor.EDITOR_THING, World.getInstance().getThing("xworker.swt.app.editors.FileTextEditor"));
					params.put("file", file);
					params.put(IEditor.EDITOR_ID, "File:" + file.getCanonicalPath());
					return params;
				}
				
				//是否是事物模型
				if(World.getInstance().isThingFile(file.getName())) {
					String path = UtilFile.getFilePath(file.getAbsolutePath());
					Thing thing = World.getInstance().getThing(path);
					if(thing != null) {
						params.put(IEditor.EDITOR_THING, World.getInstance().getThing("xworker.swt.app.editors.ThingEditor"));
						params.put("thing", thing);
						params.put(IEditor.EDITOR_ID, "Thing:" + path);
						return params;
					}
				}
				
				//默认使用系统编辑器打开
				params.put(IEditor.EDITOR_THING, World.getInstance().getThing("xworker.swt.app.editors.SystemFileEditor"));
				params.put(IEditor.EDITOR_ID, "File:" + file.getCanonicalPath());
				params.put("fileName", file.getAbsolutePath());
				return params;
			}
		}else {
			return null;
		}
	}
}
