package xworker.doc.document;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class Document extends DocumentNode{
	public static final byte TRACE = 0;
	public static final byte DEBUG = 1;
	public static final byte INFO = 2;
	public static final byte WARNING = 3;
	public static final byte DANGER = 4;
	
	byte level = INFO;
	
	public Document(Thing thing, ActionContext actionContext) {
		super(thing, actionContext);
	}
	
	public String toString() {
		return toString(level);
	}

	public byte getLevel() {
		return level;
	}

	public void setLevel(byte level) {
		this.level = level;
	}	
}
