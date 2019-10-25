package xworker.ide.index;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.ai.wordsegment.jcseg.JcsegActions;
import xworker.lang.text.Stemmer;

public class ThingIndexUtils {
	/**
	 * 返回给定事物的关键字列表。
	 * 
	 * @param thing
	 * @return
	 */
	public static List<String> getKeywords(Thing thing){
		List<String> keys = new ArrayList<String>();
		
		//用户设置的关键字
		String keywords = thing.getStringBlankAsNull("th_keywords");
        if(keywords != null){
            for(String keyword : keywords.split("[,]")){    
            	initKeys(keys, keyword);                         
             }
        }
         //插入名字和标签关键字
        initKeys(keys, thing.getMetadata().getName());
        initKeys(keys, thing.getStringBlankAsNull("label"));
        initKeys(keys, thing.getStringBlankAsNull("en_label"));
        initKeys(keys, thing.getStringBlankAsNull("zh_label"));
        initKeys(keys,thing.getStringBlankAsNull("group"));
        initKeys(keys,thing.getStringBlankAsNull("zh_group"));
        initKeys(keys,thing.getStringBlankAsNull("en_group"));
        intHtmlDocKeys(keys, thing.getStringBlankAsNull("description"));
        intHtmlDocKeys(keys, thing.getStringBlankAsNull("en_description"));
        intHtmlDocKeys(keys, thing.getStringBlankAsNull("zh_description"));
        
        //插入描述者的关键字
        initKeys(keys, "实例");
        initKeys(keys, "instance");
        List<Thing> descriptors = thing.getAllDescriptors();
        for(int i=0; i<descriptors.size() -1 ; i++){
        	Thing desc = descriptors.get(i);
        	initKeys(keys, desc.getMetadata().getName());
        	initKeys(keys, desc.getStringBlankAsNull("label"));
            initKeys(keys, desc.getStringBlankAsNull("en_label"));
            initKeys(keys, desc.getStringBlankAsNull("zh_label"));
        }
        
        //继承事物
        List<Thing> extens = thing.getAllExtends();
        if(extens.size() > 0){
        	initKeys(keys, "继承");
            initKeys(keys, "extend");
	        for(int i=0; i<extens.size() ; i++){
	        	Thing desc = extens.get(i);
	        	initKeys(keys, desc.getMetadata().getName());
	        	initKeys(keys, desc.getStringBlankAsNull("label"));
	            initKeys(keys, desc.getStringBlankAsNull("en_label"));
	            initKeys(keys, desc.getStringBlankAsNull("zh_label"));
	        }
        }
        
        //关联事物
        String regist = thing.getStringBlankAsNull("th_registThing");
        if(regist != null){
        	String[] regs = regist.split("[,]");
        	for(String reg : regs){        		
        		String rs[] = reg.split("[|]");
        		if(rs.length == 2){
        			initKeys(keys, rs[0]);
        			Thing rthing = World.getInstance().getThing(rs[1]);
        			if(rthing != null){
        				initKeys(keys, rthing.getMetadata().getName());
        	        	initKeys(keys, rthing.getStringBlankAsNull("label"));
        	            initKeys(keys, rthing.getStringBlankAsNull("en_label"));
        	            initKeys(keys, rthing.getStringBlankAsNull("zh_label"));
        			}
        		}
        	}
        }
        
                 
        //插入路径
        for(String path : thing.getMetadata().getPath().split("[.]")){
            for(String p : path.split("[@]")){
                for(String pp : p.split("[/]")){
                	initKeys(keys, pp);

                }
            }
        }
        
        return keys;
	}
	
	public static void  intHtmlDocKeys(List<String> keys, String html){
		if(html == null || "".equals(html.trim())){
			return;
		}
		
		Document doc = Jsoup.parse(html);
		String text = doc.text();
		try{
			//进行分词
			String ks[] = JcsegActions.getSegment(text);
			
			//找出频率高的20个词
			List<String> list = findHeighFreqWords(ks);
			if(list != null){
				for(String key : list){
					initKeys(keys, key);
				}
			}
		}catch(Exception e){			
		}
	}
	
	public static List<String> findHeighFreqWords(String ks[]){
		if(ks == null || ks.length == 0){
			return null;
		}
		
		Map<String, Integer> keys = new HashMap<String, Integer>();
		for(String key : ks){
			if("".equals(key) || key.length() == 1){
				continue;
			}
			
			Integer i = keys.get(key);
			if(i == null){
				keys.put(key, 1);
			}else{
				keys.put(key, i + 1);
			}
		}
		
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		for(String key : keys.keySet()){
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("key", key);
			data.put("count", keys.get(key));
			datas.add(data);
		}
		Collections.sort(datas, new Comparator<Map<String, Object>>(){
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				Integer i1 = (Integer) o1.get("count");
				Integer i2 = (Integer) o2.get("count");
				return i2.compareTo(i1);
			}
		});
		int i =0;
		List<String> list = new ArrayList<String>();
		for(Map<String, Object> data : datas){
			String key = (String) data.get("key"); 
			list.add(key);
			//System.out.println(key + ":" + data.get("count"));
			i++;
			if(i > 20){
				break;
			}
		}
		
		return list;
	}
	
	private static void initKeys(List<String> keys, String key){
		if(key == null || "".equals(key.trim())){
			return;
		}
		
		key = key.trim();
		try{
			//对英文大小写分词
			for(String k : splitEnglishWordsByCap(key)){
				addKeys(keys, getLimitStr(k, 20));
			}
			
			String ks[] = JcsegActions.getSegment(key);
			for(String k : ks){
				//加入分割的关键字
				addKeys(keys, getLimitStr(k, 20));
			}
			
			if(ks.length > 1){
				//把整个关键字也加入，如果已经分割的话
				addKeys(keys, getLimitStr(key, 20));
			}
		}catch(Exception e){
			addKeys(keys, getLimitStr(key, 20));
		}
	}
	
	private static void addKeys(List<String> keys, String key){
		key = key.toLowerCase();
		key = Stemmer.stem(key);
		if("".equals(key)){
			return;
		}else if(!keys.contains(key)){
			keys.add(key);
		}
	}
	
	private static String getLimitStr(String str, int length){
	    while(str.getBytes().length > length){
	        str = str.substring(0, str.length() - 1);
	    }
	    	  
	    return str;
	}
	
	/**
	 * 按照英文的大写字母分词，比如CreateShell分解为create和shell。
	 * 
	 * @param word
	 * @return
	 */
	public static List<String> splitEnglishWordsByCap(String word){
		if(word == null){
			return Collections.emptyList();
		}
		
		List<String> words = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();		
		for(char ch : word.toCharArray()){
			if(Character.isUpperCase(ch)){
				if(sb.length() != 0){
					words.add(sb.toString());
					
					//新的词
					sb = new StringBuilder();
				}				
			}
			sb.append(Character.toLowerCase(ch));
		}
		if(sb.length() > 0){
			words.add(sb.toString());
		}
		
		return words;
	}
	
	public static void main(String args[]){
		try{
			
			World world = World.getInstance();
			world.init("xworker");
			
			/*
			//测试HTML的关键字
			URLConnection uc= new URL("http://www.xworker.org/").openConnection();
			
					
			String html = (String) IOUtils.toString(uc.getInputStream());
			List<String> list = new ArrayList<String>();
			intHtmlDocKeys(list, html);
			System.out.println(list);
			*/
			
			System.out.println(splitEnglishWordsByCap("ByteCountToDisplaySize"));
			System.out.println(splitEnglishWordsByCap("中国人民"));
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}