package xworker.ide.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Category;
import org.xmeta.Index;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.index.CategoryIndex;
import org.xmeta.util.UtilThing;

public class ThingLibs {
	private static Logger logger = LoggerFactory.getLogger(ThingLibs.class);
	private static List<Lib> reigstLibs = new ArrayList<Lib>();
	
	public static List<Lib> getAllLibs(){
		World world = World.getInstance();
		List<Lib> libs = new ArrayList<Lib>();
		
		//码农的世界的共享库
		Thing mnuser = world.getThing("_local.manong.User");
		if(mnuser != null){
			String categoryName = "_share." + mnuser.getString("userName") + ".sharedThings";
			ThingManager th = world.getThingManager("_share");
			if(th != null){
				if(th.getCategory(categoryName) == null){
					th.createCategory(categoryName);
				}
			}
			addLib(libs, "_share", categoryName, world.getThing("xworker.ide.config.i18ns.ThingLibI18n/@manongWorld").getMetadata().getLabel());
		}

		//本地事物的库
		addLib(libs, "_local", "_local.thinglibs", world.getThing("xworker.ide.config.i18ns.ThingLibI18n/@localLiberty").getMetadata().getLabel());
		
		for(Lib lib : reigstLibs){
			libs.add(lib);
		}
		
		Thing libConfig = UtilThing.getThingIfNotExistsCreate("_local.xworker.config.ThingLib", "_local", "xworker.ide.config.ThingLib");
		for(Thing lib : libConfig.getChilds("Lib")){
			addLib(libs, lib.getString("thingManager"), lib.getString("category"), lib.getMetadata().getLabel());
		}
		
		return libs;
	}
	
	public static void addLib(List<Lib> libs, String thingManager, String category, String label){
		Lib lib = getLib(thingManager, category, label);
		if(lib != null){
			libs.add(lib);
		}
	}
	
	public static void registLib(String thingManager, String category, String label){
		Lib lib = getLib(thingManager, category, label);
		if(lib != null){
			reigstLibs.add(lib);
		}
	}
	
	public static Lib getLib(String thingManager, String category, String label){
		Index index = getIndex(thingManager, category);
		if(index != null){
			return new Lib(index, label);
		}else{
			return null;
		}
	}
	
	public static Index getIndex(String thingManager, String category){
		ThingManager manager = World.getInstance().getThingManager(thingManager);
		if(manager != null){
			Category cat = manager.getCategory(category);
			if(cat == null){
				//先尝试创建
				try{
					manager.createCategory(category);
				}catch(Exception e){					
				}
				cat = manager.getCategory(category);
			}
			
			if(cat != null){
				return new CategoryIndex(null, cat);
			}
		}
		
		logger.info("Index not found, " + thingManager + ":" + category);
		return null;
	}
	
	public static class Lib{
		Index index;
		String label;
		
		public Lib(Index index, String label){
			this.index = index;
			this.label = label;
		}

		public Index getIndex() {
			return index;
		}

		public void setIndex(Index index) {
			this.index = index;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}
		
		public Index getTodayIndex(){
			SimpleDateFormat sf = new SimpleDateFormat("'p'yyyy.'p'MM.'p'dd");
			Category cat = (Category) index.getIndexObject();
			
			String path = cat.getName() + "." + sf.format(new Date());
			
			if(cat.getThingManager().getCategory(path) == null){
				cat.getThingManager().createCategory(path);
			}
			
			Category catToday = cat.getThingManager().getCategory(path);
			return new CategoryIndex(null, catToday);
		}
	}
}
