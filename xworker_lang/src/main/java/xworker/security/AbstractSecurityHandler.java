package xworker.security;

import org.xmeta.Thing;
import org.xmeta.cache.ThingEntry;

public abstract class AbstractSecurityHandler implements SecurityHandler{
	ThingEntry entry = null;
	
	public AbstractSecurityHandler(Thing thing){
		entry = new ThingEntry(thing);
	}

	@Override
	public Thing getThing() {
		return entry.getThing();
	}

}
