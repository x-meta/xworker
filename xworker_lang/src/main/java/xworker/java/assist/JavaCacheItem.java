package xworker.java.assist;

/**
 * Java缓存条目。
 * 
 * @author Administrator
 *
 */
public class JavaCacheItem implements Comparable<JavaCacheItem>{
	public static byte CLASS = 1;
	public static byte PACKAGE = 0;
	
	public byte type;
	public String path;
	
	public JavaCacheItem(String path, byte type){
		this.path = path;
		this.type = type;		
	}

	public boolean isClass(){
		return type == CLASS;
	}
	
	@Override
	public int compareTo(JavaCacheItem o) {
		if(type != o.type){
			return type - o.type;
		}else{		
			return path.compareTo(o.path);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof JavaCacheItem){
			JavaCacheItem o = (JavaCacheItem) obj;
			return type == o.type && path.equals(o.path);
		}else{
			return false;
		}
	}
	
	
}
