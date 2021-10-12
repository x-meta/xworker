package xworker.dataObject;

import org.xmeta.Thing;
import org.xmeta.cache.ThingEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataObjectMetadataCache {
    private static final Map<String, DataObjectMetadataCache> dataObjectMetadataCacheMap = new HashMap<>();
    final Map<String, Thing> attributes = new HashMap<>();
    final Map<String, Thing> relationThings = new HashMap<>();
    final Map<String, List<Thing>> attributeRelationThings = new HashMap<>();
    List<Thing> attributeList = null;
    List<Thing> thingList = null;
    Thing[] keys = null;

    /** 描述者的引用实体 */
    private final ThingEntry descriptorEntry;

    private DataObjectMetadataCache(Thing descriptor){
        descriptorEntry = new ThingEntry(descriptor);
    }

    public static DataObjectMetadataCache getInstance(Thing descriptor){
        if(descriptor == null){
            return null;
        }

        DataObjectMetadataCache cache;
        String path = descriptor.getMetadata().getPath();
        synchronized (dataObjectMetadataCacheMap){
            cache = dataObjectMetadataCacheMap.get(path);
            if(cache == null){
                cache = new DataObjectMetadataCache(descriptor);
                dataObjectMetadataCacheMap.put(path, cache);

                cache.init();
            }else if(cache.descriptorEntry.isChanged()){
                cache.init();
            }
        }

        return cache;
    }

    private void init(){
        Thing descriptor = descriptorEntry.getThing();
        if(descriptor != null){
            Long lastInitTime = (Long) descriptor.getData("xworker_dataObject_cache_lastInitTime");
            if(lastInitTime == null || lastInitTime != descriptor.getMetadata().getLastModified()){
                attributes.clear();
                relationThings.clear();
                attributeRelationThings.clear();

                int keyIndex = 0;
                Thing[] keys_ = new Thing[10];
                attributeList = descriptor.getAllChilds("attribute");
                thingList = descriptor.getAllChilds("thing");

                //属性
                for(Thing thing : attributeList){
                    attributes.put(thing.getString("name"), thing);
                    if(thing.getBoolean("key")){
                        keys_[keyIndex] = thing;
                        keyIndex ++;
                    }
                }

                //事物
                for(Thing thing : thingList){
                    relationThings.put(thing.getString("name"), thing);

                    //设置关联
                    String localVar = thing.getString("localAttributeName");
                    Thing attr = attributes.get(localVar);
                    if(attr != null){
                        List<Thing> rthings = attributeRelationThings.computeIfAbsent(localVar, k -> new ArrayList<>());
                        rthings.add(thing);
                    }
                }

                Thing[] ks = new Thing[keyIndex];
                System.arraycopy(keys_, 0, ks, 0, keyIndex);
                keys = ks;
            }
        }
    }

    public Thing getDescriptor(){
        if(descriptorEntry.isChanged()){
            init();
        }

        return descriptorEntry.getThing();
    }
}
