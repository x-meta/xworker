package xworker.ide.command;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.xwidgets.SelectContent;
import xworker.util.UtilData;

public class ThingEditorCommand {
	public static List<SelectContent> list_nodesContents(ActionContext actionContext){
		List<SelectContent> list = new ArrayList<SelectContent>();
		
		String text = (String) actionContext.get("text");
		if(text == null){
			text = "";
		}
		
		text = text.toLowerCase();
		String txts[] = text.split("[ ]");
		Thing thing = (Thing) actionContext.get("currentThing");
		if(thing != null){
			Thing root = thing.getRoot();
			thingToSelectContent(root, list, txts, "");			
		}
		return list;
	}
	
	public static void thingToSelectContent(Thing thing, List<SelectContent> contents, String text[], String ident){		
		String label = ident + thing.getMetadata().getLabel() + "(" + thing.getThingName() + ")";
		label = label.toLowerCase();
		if(UtilData.indexHas(label, text)){
			SelectContent content = new SelectContent(label, thing);
			contents.add(content);
		}
		
		for(Thing child : thing.getChilds()){
			thingToSelectContent(child, contents, text, ident + "  ");
		}
	}

	
}
