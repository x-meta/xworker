package xworker.ide.index.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.xmeta.Thing;
import org.xmeta.World;

import redis.clients.jedis.Jedis;

/**
 * 使用Redis建立事物的索引。
 * 
 * @author Administrator
 *
 */
public class RedisIndexManager {
	/**
	 * 获取事物的注册事物列表。
	 * 
	 * @param thing
	 * @param registType
	 * @return
	 */
	public static List<Thing> getRgistThings(Thing thing, String registType){
		Jedis jedis = getDb();
		String key = thing.getMetadata().getPath() + "-" + registType;
		Set<String> thingPaths = jedis.smembers(key);
		
		return getSetThings(thingPaths);
	}
	
	/**
	 * 根据关键字查找事物列表。
	 * 
	 * @param keys
	 * @return
	 */
	public static List<Thing> searchThingByKeys(String ... keys){
		Jedis jedis = getDb();
		for(int i=0; i<keys.length; i++){
			keys[i] = "keyword-" + keys[i];
		}
		
		Set<String> thingPaths = jedis.sinter(keys);
		return getSetThings(thingPaths);
	}
	
	/**
	 * 返回Set中的事物列表。
	 * 
	 * @param thingPaths
	 * @return
	 */
	private static List<Thing> getSetThings(Set<String> thingPaths){
		List<Thing> things = new ArrayList<Thing>();
		World world = World.getInstance();
		for(String thingPath : thingPaths){
			Thing t = world.getThing(thingPath);
			if(t != null){
				things.add(t);
			}
		}
		
		return things;
	}
	
	/**
	 * 为一个事物创建索引，其中这个事物应该是根节点。
	 * 
	 * @param thing
	 */
	public static void index(Thing thing){
		Jedis jedis = getDb();

		String path = thing.getMetadata().getPath();
		
		//检查索引是否是最新的
		String lastIndexTime = jedis.get(path + "-lastModified");
		if(lastIndexTime.equals(String.valueOf(thing.getMetadata().getLastModified()))){
			return;
		}
		
		//开始建立数据库索引
		indexChild(thing, jedis, true);
	}
	
	/**
	 * 索引子事物。
	 * 
	 * @param thing
	 * @param jedis
	 * @param isRoot
	 */
	@SuppressWarnings("unchecked")
	private static void indexChild(Thing thing, Jedis jedis, boolean isRoot){
		//根事物可指定要创建索引的才会创建索引
		if(isRoot || thing.getBoolean("th_createIndex")){
			List<String> keys = getKeyWords(thing);
			//更新关键字
			updateKeys(thing, jedis, keys);
			
			//更新关系索引
			updateRegists(thing ,jedis, true);
			
		}else{
			//更新关键字，等价于移除已有的关键字
			updateKeys(thing, jedis, Collections.EMPTY_LIST);
			
			//更新关系索引
			updateRegists(thing ,jedis, false);
		}		
	}
	
	/**
	 * 更新注册索引。
	 * 
	 * @param thing
	 * @param jedis
	 */
	private static void updateRegists(Thing thing, Jedis jedis, boolean createIndex){
		String key_thing_regist = thing.getMetadata().getPath() + "-registors";
		String path = thing.getMetadata().getPath();
		String oldRegists = jedis.get(key_thing_regist);
		String newRegists = thing.getStringBlankAsNull("th_registThing");
		
		if(oldRegists != null){
			//先移除旧的
			for(String oldRegist : oldRegists.split("[,]")){
				String olds[] = oldRegist.split("[|]");
				jedis.srem(olds[1] + "-" + olds[0], path);
			}
		}
		
		if(createIndex && newRegists != null){
			//添加新的索引
			for(String regist : newRegists.split("[,]")){
				String rs[] = regist.split("[|]");
				jedis.sadd(rs[1] + "-" + rs[0], path);
			}
			
			jedis.set(key_thing_regist, newRegists);
		}else{
			jedis.del(key_thing_regist);
		}
	}
	
	/**
	 * 更新事物的关键字索引。
	 * 
	 * @param thing
	 * @param jedis
	 * @param newKeys
	 */
	private static void updateKeys(Thing thing, Jedis jedis, List<String> newKeys){
		String path = thing.getMetadata().getPath();
		String key_thing_words = thing.getMetadata().getPath() + "-keyWords";		
		String oldKeys = jedis.get(key_thing_words);
		if(oldKeys != null){
			for(String key : oldKeys.split("[,]")){
				if(!newKeys.contains(key)){
					//从关键字列表中移除
					jedis.srem("keyword-" + key, path);
				}else{
					newKeys.remove(key);
				}
			}
			
			//newKeys剩下的是新的关键字
			for(String key : newKeys){
				jedis.sadd("keyword-" + key, path);
			}
		}
		
		if(newKeys.size() > 0){
			String keys = null;
			for(String key : newKeys){
				if(keys == null){
					keys = key;
				}else{
					keys = keys + "," + key;
				}
			}
			jedis.set(key_thing_words, keys);
		}else{
			jedis.del(key_thing_words);
		}
	}
	
	/**
	 * 返回一个事物的所有关键字列表。
	 * 
	 * @param thing
	 * @return
	 */
	public static List<String> getKeyWords(Thing thing){
		List<String> keys = new ArrayList<String>();
		
		String path = thing.getMetadata().getPath();
		
		//排除子节点的路径，只保留包名和事物名，包名和事物名也作为索引
		int index = path.indexOf("/");
		if(index != -1){
			path = path .substring(0, index);
		}
		
		for(String key : path.split("[.]")){
			addKey(keys, key);
		}
		
		//事物的名字和标签也作为key
		addKey(keys, thing.getString("name"));
		addKey(keys, thing.getString("label"));
		
		//添加事物的关键字
		String th_keywords = thing.getStringBlankAsNull("th_keywords");
		if(th_keywords != null){
			for(String key : th_keywords.split("[,]")){
				addKey(keys, key);
			}
		}
		
		return keys;
	}
	
	/**
	 * 添加key到列表。
	 * 
	 * @param keys
	 * @param key
	 */
	private static void addKey(List<String> keys, String key){
		if(key != null && !"".equals(key) && !keys.contains(key)){
			keys.add(key);
		}
	}
	
	/**
	 * 获取Redis的数据库连接。
	 * 
	 * @return
	 */
	public static Jedis getDb(){
		return null;
	}
}
