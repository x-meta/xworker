package xworker.utils.example;

import org.xmeta.Thing;
import org.xmeta.World;

/**
 * 从XWorker的示例中创建命令。
 * 
 * @author zyx
 *
 */
public class CreateCommadFromExample {
	
	public static void createCommand(String exampleMenuPath, String parentCommand, String thingManager, String category, String exampleType){
		World world = World.getInstance();
		
		Thing parent = world.getThing(parentCommand);
		Thing example = world.getThing(exampleMenuPath);
		
		for(Thing child : example.getChilds()){
			createCommand(child, parent, thingManager, category, exampleType);
		}
	}
	
	public static void createCommand(Thing menu, Thing parent, String thingManager, String category, String exampleType){
		String extendPath = menu.getStringBlankAsNull("extends");
		
		Thing cmd = new Thing("xworker.command.Word");
		cmd.put("name", menu.get("name"));
		cmd.put("label", menu.get("label"));
		cmd.put("description", menu.get("description"));
		cmd.put("th_createIndex", "true");
		cmd.put("th_registThing", "command|" + parent.getMetadata().getPath());
		
		Thing actions = new Thing("xworker.command.Word/@actions");
		cmd.addChild(actions);
		String path = category + "." + cmd.getMetadata().getName();
		
		if(extendPath == null){
			//是目录,创建索引
			Thing regist = new Thing("xworker.command.CommandActions/@CreateRegistSelector");
			
			regist.set("thingPath", path);
			
			actions.addChild(regist);
		}else{
			//创建示例
			Thing example = new Thing("xworker.command.CommandActions/@CrreateDataObjectSelector1");
			example.put("exampleType", exampleType);
			example.put("examplePath", extendPath);
			
			actions.addChild(example);
		}
		
		cmd.saveAs(thingManager, path);
		System.out.println("示例已创建: " + path);
		
		if(extendPath == null){
			//创建子节点			
			for(Thing child : menu.getChilds()){
				createCommand(child, cmd, thingManager, category + "." + cmd.getMetadata().getName() + "chids", exampleType);
			}
		}			
	}
	
	public static void main(String args[]){
		try{
			World world = World.getInstance();			
			world.init("./xworker/");

			//创建SWT的示例
			createCommand("xworker.example.swt.SWTExamplesMenu", "xworker.things.p2016.p04.p17.ComposteExampleCmd", 
					"xworker_share", "xworker.things.swt", "composite");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
