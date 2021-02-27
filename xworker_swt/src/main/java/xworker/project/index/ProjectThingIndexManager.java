package xworker.project.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Category;
import org.xmeta.Thing;
import org.xmeta.cache.ThingEntry;

import xworker.lang.executor.Executor;

/**
 * 这个功能应该已经取消了，由ThingUtils自动刷新缓存代替了。
 * 
 * @author zyx
 * @deprecated
 *
 */
public class ProjectThingIndexManager {
	private static final String TAG = ProjectThingIndexManager.class.getName();
	
	/**
	 * 项目索引管理者的缓存。
	 */
	private static Map<String, ProjectThingIndexManager> projectThingIndexManagers = new HashMap<String, ProjectThingIndexManager>();
	
	/**
	 * 由于缓存的刷新可能是非常频繁的，如果目录没有设置需要缓存，那么每次都会查找，为避免查找损耗增加此缓存。
	 */
	private static Map<String, String> categoryCach = new HashMap<String, String>(); 
	
	/**
	 * 索引事物。
	 * 
	 * @param thing
	 * @return
	 */
	public static boolean indexThing(Thing thing){
		ProjectThingIndexManager manager = getProjectThingIndexManager(thing);
		if(manager != null){
			manager.updateIndex(thing, true);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 删除事物对应的索引。
	 * 
	 * @param thing
	 */
	public static void removeIndex(Thing thing){
		ProjectThingIndexManager manager = getProjectThingIndexManager(thing);
		if(manager != null){
			manager.removeCahce(thing);
		}
	}
	
	/**
	 * 通过描述者获取缓存的事物。
	 * 
	 * @param descriptor
	 * @param currentThing
	 * @param actionContext
	 * @return
	 */
	public static List<Thing> getThingsByDescriptor(String descriptor, Thing currentThing, ActionContext actionContext){
		ProjectThingIndexManager manager = getProjectThingIndexManager(currentThing);
		if(manager != null){
			List<Thing> thingList = new ArrayList<Thing>();
			List<String> thingPathList = manager.thingIndexsByDescriptor.get(descriptor);
			if(thingPathList != null){
				for(String path : thingPathList){
					ThingEntry entry = manager.indexedThings.get(path);
					if(entry != null){
						Thing thing = entry.getThing();
						if(thing != null && !thing.getMetadata().isRemoved()){
							thingList.add(thing);
						}
					}
				}
			}
			return thingList;
		}else{
			return null;
		}
	}
	
	/**
	 * 给定事物查看是否存在项目索引。
	 * 
	 * @param thing
	 * @return
	 */
	public static ProjectThingIndexManager getProjectThingIndexManager(Thing thing){
		if(thing == null){
			return null;
		}
		
		//寻找当前目录或父目录下是否定义了ProjectThingIndex
		Category category = thing.getMetadata().getCategory();
		ProjectThingIndexManager manager = getProjectThingIndexThing(category);
		if(manager == null && "ProjectIndex".equals(thing.getRoot().getMetadata().getName())){
			manager = new ProjectThingIndexManager(thing);
			projectThingIndexManagers.put(category.getName(), manager);
		}
		
		return manager;
	}
	
	private static ProjectThingIndexManager getProjectThingIndexThing(Category category){
		String path = category.getName();
		ProjectThingIndexManager manager = projectThingIndexManagers.get(path);
		if(manager == null && categoryCach.get(path) == null){
			categoryCach.put(path, path);
			
			Thing thing = category.getThing("ProjectIndex");
			if(thing != null){
				manager = new ProjectThingIndexManager(thing);
				projectThingIndexManagers.put(path, manager);
				return manager;
			}			
		}else{
			return manager;
		}				
		
		Category parent = category.getParent();
		if(parent != null){
			return getProjectThingIndexThing(parent);
		}else{
			return null;
		}
	}
	
	/** 事物索引，按照描述者建立的事物索引 */
	private Map<String, List<String>> thingIndexsByDescriptor = new HashMap<String, List<String>>();
	
	/** 已经做了索引的事物 ，包含附加的事物 */;
	private Map<String, ThingEntry> indexedThings = new HashMap<String, ThingEntry>();
	
	/** 一个事物的附加事物索引，保存附加事物的路径，事物改变时依靠他删除已有的索引 */
	private Map<String, List<String>> addtionalThings = new HashMap<String, List<String>>();
	
	private Thing projectThingIndexThing;
		
	public ProjectThingIndexManager(Thing projectThingIndexThing){
		this.projectThingIndexThing = projectThingIndexThing;
		
		ProjectThingIndexThread.startIndex(projectThingIndexThing.getRoot().getMetadata().getCategory());
	}
	
	public Thing getProjectThingIndexThing() {
		return projectThingIndexThing;
	}

	@SuppressWarnings("unchecked")
	public void updateIndex(Thing thing, boolean checkAdditionalThings){
		String path = thing.getMetadata().getPath();
		
		//移除更多事物缓存
		List<String> addThings = addtionalThings.get(thing.getMetadata().getPath());
		if(addThings != null){
			for(String addThing : addThings){
				indexedThings.remove(addThing);
			}
		}
		
		//事物本身按照描述者的索引
		List<Thing> descriptors = thing.getAllDescriptors();
		for(Thing desc : descriptors){
			String descPath = desc.getMetadata().getPath();
			List<String> things = thingIndexsByDescriptor.get(descPath);
			if(things == null){
				things = new ArrayList<String>();
				thingIndexsByDescriptor.put(descPath, things);
			}
			
			if(!things.contains(path)){
				things.add(thing.getMetadata().getPath());
			}
		}
		
		//附加事物
		try{
			List<Thing> addtionalThingList = (List<Thing>) thing.doAction("getIDEAdditionalThings");
			if(addtionalThingList != null){
				addThings = new ArrayList<String>();
				for(Thing addThing : addtionalThingList){
					updateIndex(addThing, false);
					addThings.add(addThing.getMetadata().getPath());
				}
				
				addtionalThings.put(thing.getMetadata().getPath(), addThings);
			}
		}catch(Exception e){
			Executor.warn(TAG, 	"getIDEAdditionalThings error, should return List<Thing>", e);
		}
		
		//子事物
		for(Thing child : thing.getChilds()){
			updateIndex(child, checkAdditionalThings);
		}
		
		ThingEntry entry = new ThingEntry(thing);
		indexedThings.put(thing.getMetadata().getPath(), entry);
	}
	
	/**
	 * 移除相关缓存。
	 * 
	 * @param thing
	 */
	public void removeCahce(Thing thing){
		//移除更多事物缓存
		List<String> addThings = addtionalThings.get(thing.getMetadata().getPath());
		if(addThings != null){
			for(String addThing : addThings){
				indexedThings.remove(addThing);
			}
		}
		
		indexedThings.remove(thing.getMetadata().getPath());
		
		for(Thing child : thing.getChilds()){
			removeCahce(child);
		}
	}
}
