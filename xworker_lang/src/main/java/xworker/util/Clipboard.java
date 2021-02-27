package xworker.util;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.Thing;

public class Clipboard {
	private static int maxClips = 20;
	private static List<Thing> clips = new ArrayList<Thing>();
	
	public static void add(Thing dataObject){
		if(clips.size() > maxClips){
			clips.remove(0);
		}
		
		clips.add(dataObject);
	}
	
	public static Thing get(){
		if(clips.size() != 0){
			return clips.get(clips.size() - 1);
		}else{
			return null;
		}
	}
	
	public static Thing get(int index){
		if(index < clips.size()){
			return clips.get(index);
		}else{
			return null;
		}
	}
	
	public static List<Thing> getClips(){
		return clips;
	}

}
