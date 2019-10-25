package xworker.java.assist;

import java.util.ArrayList;
import java.util.List;

/**
 * 按照树形结构组成的Package。
 * 
 * @author Administrator
 *
 */
public class PackageTree {
	public String name;
	List<PackageTree> childs = new ArrayList<PackageTree>();
	PackageTree parent = null;
	
	public PackageTree(String name){
		this.name = name;
	}
	
	public PackageTree(String name, PackageTree parent){
		this.name = name;
		this.parent = parent;
	}
	
	public void addPackage(String pks){
		PackageTree tree = this;
		
		for(String name : pks.split("[.]")){
			boolean have = false;
			for(PackageTree child : tree.childs){
				if(child.name.equals(name)){
					tree = child;
					have = true;
					break;
				}
			}
			
			if(!have){
				PackageTree child = new PackageTree(name, tree);
				tree.childs.add(child);
				tree = child;
			}
		}
	}
}
