package xworker.util.compress;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class GroupEntry implements CompressEntry{
	Thing thing;
	ActionContext actionContext;
	List<CompressEntry> childs = new ArrayList<CompressEntry>();
	
	public GroupEntry(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
		
		for(Thing child : thing.getChilds()){
			CompressEntry entry = child.doAction("create", actionContext);
			if(entry != null){
				childs.add(entry);
			}
		}
	}
	
	@Override
	public String getName() {
		return null;
	}
	
	@Override
	public void write(OutputStream output) throws IOException {
		
	}
	
	@Override
	public long getLastModified() {
		return 0;
	}
	
	@Override
	public long getSize() {
		return 0;
	}
	
	@Override
	public Iterator<CompressEntry> getChildsIterator() {
		return childs.iterator();
	}
	
	public static GroupEntry create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		return new GroupEntry(self, actionContext);
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

	@Override
	public long getCRC32() {
		return 0;
	}

	@Override
	public int getMethod() {
		return -1;
	}
}
