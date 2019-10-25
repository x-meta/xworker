package xworker.util;

import java.io.File;
import java.util.Iterator;

import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.thingManagers.FileThingManager;

public class GenerateJavaCode {
	static String[] excludes = new String[]{
		"xworker.db.jdbc.DataSource",
		"xworker.db.jdbc.metadata.Columns",
		"xworker.db.jdbc.metadata.TableTypes",
		"xworker.db.jdbc.metadata.Tables",
		"xworker.lang.ClassWrapperImporter",
		"xworker.lang.MetaDescriptor3",
		"xworker.lang.MetaThing",
		"xworker.lang.actions.ActionMenu",
		"xworker.lang.actions.GroovyAction",
		"xworker.lang.actions.JdkScript",
		"xworker.lang.flow.NextFlowSelector",
		"xworker.lang.relation.ThingRelation",
		"xworker.lang.relation.editors.RegistThingCreater",
		"xworker.lang.relation.editors.SelectTargetThingDialog",
		"xworker.lang.relations.FeatureComposite",
		"xworker.lang.relations.MetaThingRelation",
		"xworker.lang.relations.TemplateCompoiste",
		"xworker.lang.relations.ThingNotesViewer",
		"xworker.lang.relations.ThingNotesWebControl",
		"xworker.lang.security.PermissionViewer",
		"xworker.lang.security.SecurityHandlerViewer",
		"xworker.lang.text.JsonDataFormat",
		"xworker.lang.util.ThingTemplate"
	};
		
	public static void main(String args[]){
		try{
			World world = World.getInstance();
			world.init("xworker");
			
			world.addThingManager(new FileThingManager("xworker_core", new File("src/main/resources/"), false));
						
			ThingManager thingManager = world.getThingManager("xworker_core");
			Iterator<Thing> iter = thingManager.iterator("", true);
			while(iter.hasNext()){
				Thing thing = iter.next();
				boolean exclude = false;
				for(String ext : excludes){
					if(ext.equals(thing.getMetadata().getPath())){
						exclude = true;
					}
				}
				if(exclude){
					continue;
				}
				
				//changeToJavaAction("../x-lang/src", thing);
				String root = "./src/test/java/";
				String dist = "./src/main/java/";
				if(GroovyToJava.findGroovyAction(thing).size() > 0){
					String javaName = GroovyToJava.getJavaName(thing);
					String className = thing.getMetadata().getCategory().getName() + "." + javaName;					
					
					if(GroovyToJava.isClassFileExists(root, className)){
						//修改到Java
						GroovyToJava.changeToJavaAction(thing, root);
						
						//移动文件
						GroovyToJava.moveSourceToTarget(thing, root, dist);
						
						System.out.println("Code changed and moved: " + thing.getMetadata().getPath());
					}else{
						//生成Java文件
						GroovyToJava.createJavaCode(thing, root);
						System.out.println("CreateJavaCode: " + thing.getMetadata().getPath());
					}
					break;
				}
			}
			
			System.exit(0);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
