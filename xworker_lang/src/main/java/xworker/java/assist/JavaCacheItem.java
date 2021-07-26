package xworker.java.assist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Java缓存条目。
 * 
 * @author Administrator
 *
 */
public class JavaCacheItem implements Comparable<JavaCacheItem>{
	public static byte CLASS = 1;
	public static byte PACKAGE = 0;
	public static byte ROOT = 2;
	
	public byte type;
	public String path;
	List<JavaCacheItem> items = new ArrayList<>();
	JavaCacheItem parentItem = null;
	
	public JavaCacheItem(String path, byte type){
		this.path = path;
		this.type = type;		
	}

	public boolean isClass(){
		return type == CLASS;
	}

	public boolean isPackage(){
		return type == PACKAGE;
	}

	public boolean isRoot(){
		return type == ROOT;
	}

	public String getName(){
		return path;
	}

	public String getSimpleName(){
		if(path == null){
			return path;
		}

		int index = path.lastIndexOf(".");
		if(index != -1){
			return path.substring(index + 1, path.length());
		}else{
			return path;
		}
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

	/**
	 * 添加一个子条目。
	 *
	 * @param item 条目
	 */
	public void addItem(JavaCacheItem item){
		items.add(item);
	}

	/**
	 * 返回所有的子条目。
	 *
	 * @return 子条目列表
	 */
	public List<JavaCacheItem> getItems(){
		return items;
	}

	public void sort(){
		Collections.sort(items);
	}

	public JavaCacheItem getParentItem() {
		return parentItem;
	}

	public void setParentItem(JavaCacheItem parentItem) {
		this.parentItem = parentItem;
	}
}
