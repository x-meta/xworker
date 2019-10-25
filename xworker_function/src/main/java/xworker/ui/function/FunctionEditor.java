package xworker.ui.function;

import org.xmeta.Thing;

public class FunctionEditor {
	private static Thing clipboardThing;
	
	public static void setClipboardThing(Thing thing){
		clipboardThing = thing;
	}
	
	public static Thing getClipboardThing(){
		return clipboardThing;
	}
}
