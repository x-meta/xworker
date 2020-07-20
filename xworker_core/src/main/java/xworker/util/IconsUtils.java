package xworker.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;

/**
 * 和XWorker的/webroot/icons/相关的工具。
 * @author zyx
 *
 */
public class IconsUtils {

	public static List<Icon> getIcons(String thingManager){
		ThingManager tm = World.getInstance().getThingManager(thingManager);
		if(tm == null) {
			return Collections.emptyList();
		}else {
			Map<String, String> context = new HashMap<String, String>();
			Iterator<Thing> iter = tm.iterator(null, true);
			List<Icon> icons = new ArrayList<Icon>();
			
			while(iter.hasNext()) {
				Thing thing = iter.next();
				
				initIcons(thing, icons, context);
			}
			
			return icons;
		}
	}
	
	public static void initIcons(Thing thing, List<Icon> icons, Map<String, String> context) {
		String path = thing.getMetadata().getPath();
		if(context.get(path) == null) {
			context.put(path, path);
		}else {
			return;
		}
		
		initIcon(thing, icons, context);
		
		for(Thing desc : thing.getDescriptors()) {
			initIcon(desc, icons, context);
		}
		
		for(Thing ext : thing.getExtends()) {
			initIcon(ext, icons, context);
		}
		
		for(Thing child : thing.getChilds()) {
			initIcons(child, icons, context);
		}
	}	
	
	private static void initIcon(Thing thing,  List<Icon> icons, Map<String, String> context) {
		String icon = getIcon(thing.getString("icon"));
		if(icon != null && context.get(icon) == null) {
			File file = new File(World.getInstance().getPath() + "/webroot/" + icon);
			if(file.exists()) {
				context.put(icon, icon);
				
				icons.add(new Icon(icon, file));
			}
		}
		
		String image = getIcon(thing.getString("image"));
		if(image != null && context.get(image) == null) {
			File file = new File(World.getInstance().getPath() + "/webroot/" + image);
			if(file.exists()) {
				context.put(image, image);
				
				icons.add(new Icon(image, file));
			}
		}
	}
	
	private static String getIcon(String icon) {
		if(icon == null) {
			return null;
		}
		
		if(icon.startsWith("icons")) {
			return icon;
		}else if(icon.startsWith("/icons")) {
			return icon.substring(1, icon.length());
		}else {
			return null;
		}
	}
	
	public static class Icon{
		
		/** 图标的名称，相对于webroot/icons的目录，前面不带/ */
		public String name;
		
		/** 图标对应的文件 */
		public File file;
		
		public Icon(String name , File file) {
			this.name = name;
			this.file = file;
		}
		
	}
}
