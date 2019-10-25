package xworker.app.xworker;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmeta.Thing;
import org.xmeta.World;

public class XWorkerDeployer {
	public static void checkFile(String filePath){
		File file = new File(filePath);
		if(!file.exists()){
			file.mkdirs();
		}
	}
	public static void main(Object classLoader){
		try{
			World world = World.getInstance();			
			world.setMode(World.MODE_WORKING);
			
			//检查目录是否存在
			checkFile("./xworker/classes/");
			checkFile("./xworker/lib/");
			checkFile("./xworker/databases/");
			checkFile("./xworker/xworker_remote/things/");
			checkFile("./xworker/projects/_local/things/_local/xworker/config/");
			
			
			List<File> files = new ArrayList<File>();			
			files.add(new File("./xworker/classes/"));
			
			File libs = new File("./xworker/lib/");
			for(File child : libs.listFiles()){
				if(child.isFile()){
					files.add(child);
				}
			}
			
			URL[] urls = new URL[files.size()];
			for(int i=0; i<files.size(); i++){
				urls[i] = files.get(i).toURI().toURL();
			}
			
			ClassLoader loader = (ClassLoader) classLoader;
			//RemoteClassLoader loader = new RemoteClassLoader(world.getClassLoader(), urls);		
			world.init("./xworker/", loader);
						
			world.addThingManager(new RemoteThingManager("xworker", new File("./xworker/xworker_remote/"), loader));
			
			//Thing thing = world.getThing("xworker.example.swt.SWTExamplesMain");
			Thing thing = XWorkerDeployConfig.getThing();
			if(thing == null){
				System.out.println("Please set thingPath in ./deploy.properties");;
				return;
			}
			thing.doAction(XWorkerDeployConfig.getAction());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
