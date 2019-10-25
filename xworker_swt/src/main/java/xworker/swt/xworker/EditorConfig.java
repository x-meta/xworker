package xworker.swt.xworker;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.cache.ThingEntry;

public class EditorConfig {
	static ThingEntry editorConfig = new ThingEntry(World.getInstance().getThing("xworker.swt.xworker.attributeEditor.EditorConfig"));	
	static Map<String, ThingEntry> editors = new HashMap<String, ThingEntry>();
	static {
		init();
	}
	
	private static void init() {
		Thing cfg = editorConfig.getThing();
		for(Thing child : cfg.getChilds()) {
			Thing swtEditor = World.getInstance().getThing(child.getString("default"));
			if(swtEditor != null) {
				editors.put(child.getMetadata().getName(), new ThingEntry(swtEditor));
				
			}
		}
	}
	
	public static Thing getAttributeEditor(String type) {
		if(editorConfig.isChanged()) {
			init();
		}
		
		ThingEntry entry = editors.get(type);
		if(entry != null) {
			return entry.getThing();
		}else {
			return null;
		}		
	}
	
	public static void registAttributeEditor(String type, Thing editorThing) {
		editors.put(type, new ThingEntry(editorThing));
	}
}
