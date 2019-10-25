package xworker.doc.thing;

import org.xmeta.World;
import org.xmeta.util.UtilString;

public class GenerateThingDoc {
	public static void main(String args[]){
		try{
			World world = World.getInstance();			
			world.init("./xworker");
			
			long user = 10l * 365 * 1024 * 1024 * 1024;
			System.out.println(UtilString.getSizeInfo(user));
					
			/**
			//初始化explorer之外的项目
			Thing project = world.getThing("_local.xworker.worldExplorer.ProjectSet");
			project.doAction("run");
			
			//需要过滤的
			String[] excludes = new String[]{"xworker_example", "xworker_ide", "xworker_orgweb"};
			List<String> thingManagers = new ArrayList<String>();
			for(ThingManager thingManager : world.getThingManagers()){
				String name = thingManager.getName();
				boolean exclude = false;
				for(String ext : excludes){
					if(ext.equals(name)){
						exclude = true;
						break;
					}
				}
				if(exclude){
					continue;
				}
				
				if(name.startsWith("xworker_")){
					thingManagers.add(name);
				}
			}
			
			generateDoc(thingManagers, "d:/dist/thingdoc");*/
		}catch(Throwable e){
			e.printStackTrace();
		}
	}
}
