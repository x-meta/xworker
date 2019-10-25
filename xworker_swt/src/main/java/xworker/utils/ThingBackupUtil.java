package xworker.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xmeta.Thing;
import org.xmeta.ThingCoder;
import org.xmeta.ThingMetadata;
import org.xmeta.World;
import org.xmeta.cache.ThingCache;

public class ThingBackupUtil {
	public static void backup(Thing thing) throws IOException {
		thing = thing.getRoot();
		String path = getBackupDirectory(thing);
		File dir = new File(path);
		if(dir.exists() == false) {
			dir.mkdirs();
		}
		
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss.SSS");
		
		FileOutputStream fout = new FileOutputStream(new File(dir, sf.format(new Date())));
		try {
			ThingCoder coder = World.getInstance().getThingCoder("dml_txt");
			coder.encode(thing, fout);
		}finally {
			fout.close();
		}		
	}
	
	public static List<File> getBackupFiles(Thing thing){
		thing = thing.getRoot();
		String path = getBackupDirectory(thing);
		File dir = new File(path);
		List<File> files = new ArrayList<File>();
		if(dir.exists() && dir.isDirectory()) {
			for(File f : dir.listFiles()) {
				if(f.isFile()) {
					files.add(f);
				}
			}
		}
		
		return files;
	}
	
	public static Thing restore(Thing oldThing, String file) throws IOException {
		oldThing = oldThing.getRoot();
		String thingName = oldThing.getMetadata().getPath();
		
		//复制相同的事物
		Thing thing = new Thing(null, null, null, false);
		ThingMetadata metadata = thing.getMetadata();
		metadata.setPath(thingName);		
		metadata.setCategory(oldThing.getMetadata().getCategory());
		metadata.setCoderType(oldThing.getMetadata().getCoderType());
		metadata.setReserve(oldThing.getMetadata().getReserve());
		
		ThingCoder thingCoder = World.getInstance().getThingCoder("dml_txt");
		File thingFile = new File(file);
		FileInputStream fin = new FileInputStream(thingFile);
		try {
			thingCoder.decode(thing, fin, thingFile.lastModified());
		}finally {
			fin.close();
		}
		
		//保存事物
		thing.save();
		
		//清除缓存
		ThingCache.remove(thingName);
		return World.getInstance().getThing(thingName);
	}
	
	public static String getBackupDirectory(Thing thing) {
		String path = World.getInstance().getPath() + "/work/bak/";
		path = path + thing.getMetadata().getThingManager().getName();
		
		return path + "/" + thing.getMetadata().getPath().replace('.', '/') + "_bak_up/";		
	}
}
