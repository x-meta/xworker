package xworker.swt.util;

import java.io.File;
import java.util.Iterator;

import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.thingManagers.FileThingManager;

import xworker.util.GroovyToJava;

public class SWTGroovyToJava {
	static String[] excludes = new String[]{
		"xworker.app.view.swt.app.tpls.ApplicationTmpl",
		"xworker.app.view.swt.app.workbentch.WorkbentchPrototype",
		"xworker.app.view.swt.data.DataStoreAction",
		"xworker.app.view.swt.data.DataStoreActions",
		"xworker.app.view.swt.data.DateToDateTimeDataStore",
		"xworker.app.view.swt.data.RowVsColDataStore",
		"xworker.app.view.swt.dialogs.DataObjectInputDialog",
		"xworker.app.view.swt.widgets.form.AttributeSelectModel",
		"xworker.app.view.swt.widgets.table.DataObjectGridRowEditor",
		"xworker.app.view.swt.widgets.table.EditableTable",
		"xworker.app.view.swt.widgets.tree.XTree",
		"xworker.dataObject.swt.AttributeMappingEditor",
		"xworker.dataObject.swt.AttributeRelationEditor",
		"xworker.dataObject.swt.DataObjectEditor",
		"xworker.dataObject.swt.ExtendAttributeEditor",
		"xworker.dataObject.swt.GenerateSimpleSQLDialog",
		"xworker.dataObject.swt.ImportAttributeDialog",
		"xworker.dataObject.swt.SelectAttributeDialog",
		"xworker.dataObject.swt.SelectDBTableNameDialog",
		"xworker.swt.help.base.ShellExample",
		"xworker.swt.util.subtitle.SubtitleToolItem",
		"xworker.swt.widgets.tpls.ShellAppTpl",
		"xworker.swt.xwidgets.DemoAutoDemo",
		"xworker.swt.xwidgets.DemoTabFolder",
		"xworker.swt.xworker.attributeEditor.CodeAssist",
		"xworker.swt.xworker.attributeEditor.CodeAssistImporter",
		"xworker.swt.xworker.attributeEditor.CodeAssistTypeSetter",
		"xworker.swt.xworker.attributeEditor.openWins.ClassSelector",
		"xworker.swt.xworker.attributeEditor.openWins.MethodSelector",
		"xworker.swt.xworker.prototype.ThingRegistSelector"
		
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
