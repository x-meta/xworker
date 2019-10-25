package xworker.doc.thing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.Category;
import org.xmeta.Thing;

public class ThingDocContext {
	List<String> categorys = new ArrayList<String>();
	Map<String, List<ThingDoc>> categoryThings = new HashMap<String, List<ThingDoc>>(); 
	List<String> things = new ArrayList<String>();
	Map<String, ThingDoc> thingCache = new HashMap<String ,ThingDoc>();
	List<ThingDoc> allThingsDoc = new ArrayList<ThingDoc>();
	
	public Map<String, ThingDoc> getAllThings(){
		return thingCache;
	}
	
	public List<ThingDoc> getAllThingDocs(){
		return allThingsDoc;
	}
	
	/**
	 * 事物是否在生成的文档上下文里。
	 * 
	 * @param thing
	 * @return
	 */
	public boolean isThingExists(Thing thing){
		return thingCache.get(thing.getMetadata().getPath()) != null;
	}
	
	public ThingDoc getThingDoc(Thing thing){
		return thingCache.get(thing.getMetadata().getPath());
	}
	
	public String getThingUrl(Thing currentThing, Thing thing){
		ThingDoc cDoc = this.getThingDoc(currentThing);
		ThingDoc tDoc = this.getThingDoc(thing);
		if(cDoc != null && tDoc != null){
			return Doc.getRelatePath(cDoc.getDocFilePath(), tDoc.getDocFilePath());
		}
		return null;
	}
	
	public void putThing(Thing thing){
		if(thing == null){
			return;
		}
		
		//过滤重复的事物
		String thingPath = thing.getMetadata().getPath();
		if(thingCache.get(thingPath) != null){
			return;
		}
		
		System.out.println("Add thing " + thing.getMetadata().getPath());
		//放入到目录下
		Category category = thing.getRoot().getMetadata().getCategory();
		String categoryName = category.getName();
		List<ThingDoc> thingDocs = categoryThings.get(categoryName);
		if(thingDocs == null){
			thingDocs = new ArrayList<ThingDoc>();
			categoryThings.put(categoryName, thingDocs);
			categorys.add(categoryName);
		}
		
		ThingDoc doc = new ThingDoc(this, thing); 
		thingDocs.add(doc);
		things.add(doc.getName());
		
		thingCache.put(thingPath, doc);
		allThingsDoc.add(doc);
		
		for(Thing child : thing.getChilds()){
			if("thing".equals(child.getThingName())){
				putThing(child);
			}
		}
	}
	
	public List<ThingDoc> getThingDocs(String categoryName){
		return categoryThings.get(categoryName);
	}
	
	public List<String> getCategorys(){
		return categorys;
	}
	
	/**
	 * 放入事物结束，在这里做排序等最后的工作。
	 * 
	 */
	public void putFinished(){
		Collections.sort(categorys);
		Collections.sort(allThingsDoc);
		
		for(String key : categoryThings.keySet()){
			Collections.sort(categoryThings.get(key));
		}
	}
}
