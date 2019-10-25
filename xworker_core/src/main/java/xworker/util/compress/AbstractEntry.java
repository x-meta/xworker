package xworker.util.compress;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class AbstractEntry implements CompressEntry{
	Thing thing;
	ActionContext actionContext;
	
	public AbstractEntry(Thing thing, ActionContext actionContext){
		this.thing = thing; 
		this.actionContext = actionContext;
	}
	
	@Override
	public String getName() {
		return (String) thing.doAction("getName", actionContext);
	}

	@Override
	public void write(OutputStream output) throws IOException {
		thing.doAction("write", actionContext, "output", output);
	}

	@Override
	public long getLastModified() {
		return (Long) thing.doAction("getLastModified", actionContext);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<CompressEntry> getChildsIterator() {
		List<CompressEntry> childs = (List<CompressEntry>) thing.doAction("getChilds", actionContext);
		if(childs != null) {
			return childs.iterator();
		}
		return null;
	}
	
	@Override
	public long getSize() {
		return (Long) thing.doAction("getSize", actionContext);
	}
	
	@Override
	public void close() {
		
	}
	
	@Override
	public void open() {
		
	}
	
	@Override
	public boolean isDirectory() {
		return false;
	}

	public static ThingEntry create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		return new ThingEntry(self, actionContext);
	}

	@Override
	public long getCRC32() {
		return (Long) thing.doAction("getCRC32", actionContext);
	}

	@Override
	public int getMethod() {
		Integer method = (Integer) thing.doAction("getMethod", actionContext);
		if(method != null) {
			return method;
		}else {
			return -1;
		}
	}

}
