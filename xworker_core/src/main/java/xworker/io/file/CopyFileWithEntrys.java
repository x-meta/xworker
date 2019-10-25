package xworker.io.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;
import xworker.util.compress.CompressEntry;

public class CopyFileWithEntrys {
	private static final String TAG = "CopyFileWithEntrys";
	
	public static void run(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		
		File targetDir = self.doAction("getTargetDir", actionContext);
		
		Map<String, Object> context = new HashMap<String, Object>();
		for(Thing entrys : self.getChilds("Entrys")){
			for(Thing entryThing : entrys.getChilds()){
				CompressEntry entry = entryThing.doAction("create", actionContext);
				if(entry == null){
					Executor.debug(TAG, "Entry is null, Entry=" + entryThing.getMetadata().getPath());
				}else if(entry.getName() == null){
					Executor.debug(TAG, "Entry name is null, ignore it but search childs Entry=" + entryThing.getMetadata().getPath());
				}
										
				writeEntry(entry, targetDir, context);
			}
		}
	}
	
	private static void writeEntry(CompressEntry entry, File outDir, Map<String, Object> context) throws IOException{
		if(entry == null){
			return;
		}
		
		entry.open();
		try {
			if(entry.getName() != null){				
				String name = entry.getName();
				//去除/或\斜杠
				if(name.startsWith("/") || name.startsWith("\\")){
					name = name.substring(1, name.length());
				}
				name = name.replace('\\', '/');
				if(entry.isDirectory()) {
					if(!name.endsWith("/")) {
						name = name + "/";
					}
				}
				
				if(context.get(name) != null) {
					Executor.warn(TAG, "duplicate entry, ignore it, name=" + name);
				}else {
					context.put(name, name);
					if(entry.isDirectory()) {
						File file = new File(outDir, name);
						file.mkdirs();
					}else {				
						File out = new File(outDir, name);
						if(out.getParentFile().exists() == false) {
							out.getParentFile().mkdirs();
						}
						FileOutputStream fout = new FileOutputStream(out);
						try {
							entry.write(fout);
							if(entry.getLastModified() > 0) {
								out.setLastModified(entry.getLastModified());
							}
						}finally {
							fout.close();
						}
					}					
				}
			}
			
			Iterator<CompressEntry> iter = entry.getChildsIterator();
			if(iter != null){
				while(iter.hasNext()) {
				    CompressEntry childEntry = iter.next();
					writeEntry(childEntry, outDir, context);
				}
			}
		}finally {
			entry.close();
		}
	}
}
