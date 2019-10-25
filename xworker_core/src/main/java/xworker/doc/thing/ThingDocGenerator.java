package xworker.doc.thing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import xworker.util.UtilTemplate;

public class ThingDocGenerator {
	public static void generateDoc(List<String> thingManagers, String outputDir) throws Throwable{
		ThingDocContext context = new ThingDocContext();
		
		for(String name : thingManagers){
			ThingManager thingManager = World.getInstance().getThingManager(name);
			if(thingManager != null){
				Iterator<Thing> iter = thingManager.iterator("", true);
				while(iter.hasNext()){
					Thing thing = iter.next();			
					if("thing".equals(thing.getThingName())){
						context.putThing(thing);
					}					
				}
			}
		}
		
		context.putFinished();
		
		Map<String, Object> templateContext = new HashMap<String, Object>();
		templateContext.put("context", context);
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		templateContext.put("date", sf.format(new Date()));
		
		//生成基本的html
		generateBaseHtml(templateContext, outputDir);
		
		//生成package-frame		
		generatePackageFrame(context, templateContext, outputDir);
		
		//生成所有事物
		generateThing(context, templateContext, outputDir);
		
		//拷贝一些静态资源文件
		copyRes(outputDir);
	}
	
	public static void copyRes(String outputDir) throws IOException{
		String worldPath = World.getInstance().getPath();		
		
		FileUtils.copyFile(new File(worldPath + "/webroot/images/file/open.gif"), new File(outputDir + "/images/file/open.gif"));
		FileUtils.copyFile(new File(worldPath + "/webroot/images/file/javaDoc.gif"), new File(outputDir + "/images/file/javaDoc.gif"));
		FileUtils.copyFile(new File(worldPath + "/webroot/js/xworker/InnerBrowserUtil.js"), new File(outputDir + "/js/xworker/InnerBrowserUtil.js"));
	}
	
	public static void generateThing(ThingDocContext context, Map<String, Object> tmpContext, String outputDir) throws Throwable{
		Map<String, ThingDoc> allThings = context.getAllThings();
		for(String key : allThings.keySet()){
			ThingDoc doc = allThings.get(key);
			tmpContext.put("thing", doc.getThing());
			tmpContext.put("thingDoc", doc);
			
			generateFile(tmpContext, "/xworker/doc/thing/thingDoc.ftl", outputDir + "/" + doc.getDocFilePath());
		}
	}
	
	public static void generatePackageFrame(ThingDocContext context, Map<String, Object> tmpContext, String outputDir) throws Throwable{
		for(String category : context.getCategorys()){
			tmpContext.put("pkgName", category);
			String relatePath = "";
			String cs[] = category.split("[.]");
			for(int i=0; i<cs.length; i++){
				relatePath = "../" + relatePath;
			}
			tmpContext.put("relatePath", relatePath);
			
			//过滤掉那些不是根目录的事物
			List<ThingDoc> things = context.getThingDocs(category);
			for(int i=0; i<things.size(); i++){
				if(!things.get(i).isRootThing()){
					things.remove(i);
					i--;
				}
			}
			tmpContext.put("things", things);
			generateFile(tmpContext, "/xworker/doc/thing/package-frame.ftl", 
					outputDir + "/" + category.replace('.', '/') + "/package-frame.html");
			generateFile(tmpContext, "/xworker/doc/thing/package-summary.ftl", 
					outputDir + "/" + category.replace('.', '/') + "/package-summary.html");
		}
	}
	
	public static void generateBaseHtml(Object context, String outputDir) throws Throwable{
		generateFile(context, "/xworker/doc/thing/stylesheet.ftl", outputDir + "/stylesheet.css");
		generateFile(context, "/xworker/doc/thing/index.ftl", outputDir + "/index.html");
		generateFile(context, "/xworker/doc/thing/overview-frame.ftl", outputDir + "/overview-frame.html");		
		generateFile(context, "/xworker/doc/thing/allclasses-frame.ftl", outputDir + "/allclasses-frame.html");
		generateFile(context, "/xworker/doc/thing/overview-summary.ftl", outputDir + "/overview-summary.html");
	}
	
	public static void generateFile(Object context, String templatePath, String filePath) throws Throwable{
		System.out.println("generate " + filePath);
		File file = new File(filePath);
		if(!file.exists()){
			file.getParentFile().mkdirs();
		}
		
		Writer writer = new FileWriter(file);
		try{
			UtilTemplate.process(context, templatePath, "freemarker", writer);
		}finally{
			writer.close();
		}
	}
	
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
