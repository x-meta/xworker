package xworker.app.xworker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;

import org.xmeta.Thing;
import org.xmeta.ThingCoder;
import org.xmeta.ThingMetadata;
import org.xmeta.World;
import org.xmeta.XMetaException;
import org.xmeta.thingManagers.FileMonitor;
import org.xmeta.thingManagers.FileThingManager;
import org.xmeta.util.ThingClassLoader;


public class RemoteThingManager extends FileThingManager{
	ThingClassLoader loader = null;
	public RemoteThingManager(String name, File rootFile, ClassLoader classLoader) {
		super(name, rootFile);
		
		loader = new ThingClassLoader(classLoader);		
	}

	@Override
	public Thing doLoadThing(String thingName) {
		try{
			if("xworker.lang.actions.GroovyAction".equals(name)){
				System.out.println("FileTHingManager : groovy action reloaded");
			}
			String thingPath = thingName.replace('.', '/');
			World world = World.getInstance();
			for(ThingCoder thingCoder : world.getThingCoders()){
				File thingFile = new File(new File(this.getFilePath()), thingPath + "." + thingCoder.getType());
				if(!thingFile.exists()){
					byte[] bytes = DeployHttpClientManager.getThing(thingName);
					if(bytes == null){
						return null;
					}
					if(!thingFile.exists()){
						thingFile.getParentFile().mkdirs();
					}
					FileOutputStream fout = new FileOutputStream(thingFile);
					fout.write(bytes);
					fout.close();
				}
				
				if(thingFile.exists()){
					FileInputStream fin = new FileInputStream(thingFile);
					try{
						Thing thing = new Thing(null, null, null, false);
						ThingMetadata metadata = thing.getMetadata();
						metadata.setPath(thingName);
						String category = null;
						String thingFileName = thingName;
						int lastDotIndex = thingName.lastIndexOf(".");
						if(lastDotIndex != -1){
							category = thingName.substring(0, lastDotIndex);
							thingFileName = thingName.substring(lastDotIndex + 1, thingName.length());
						}
						metadata.setCategory(getCategory(category, true));
						metadata.setCoderType(thingCoder.getType());
						metadata.setReserve(thingFileName);
						
						thingCoder.decode(thing, fin, thingFile.lastModified());
						
						FileMonitor.getInstance().addFile(thingName, thing, thingFile, false);
						return thing;
					}finally{
						fin.close();
					}
				}
			}
			
			return null;
		}catch(Exception e){
			throw new XMetaException("load thing from FileThingManager error, managePath=" + this.getFilePath() + ",thing=" + thingName, e);
		}
	}

	@Override
	public ThingClassLoader getClassLoader() {
		return loader;
	}

	@Override
	public URL findResource(String name) {
		URL url = super.findResource(name);
		if(url == null){
			try {
				return DeployHttpClientManager.getResource(name);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}else{
			return url;
		}
	}

}
