package xworker.doc.thing;

import org.xmeta.Thing;

public class Doc implements Comparable<Doc>{
	Thing thing;
	Thing owner;
	ThingDocContext context;
	
	public Doc(ThingDocContext context, Thing owner, Thing thing){
		this.owner = owner;
		this.thing = thing;
		this.context = context;
	}
	
	public Thing getThing(){
		return thing;
	}
	
	public String getCategoryName(){
		return thing.getRoot().getMetadata().getCategory().getName();
	}
	
	public String getRelationPath(){
		Thing root = thing.getRoot();
		String path[] = root.getMetadata().getCategory().getName().split("[.]");
		String str = "";
		for(int i=0; i<path.length ; i++){
			str = "../" + str;
		}
		
		return str;
	}
	
	/**
	 * 获取生成文件后的html文档的绝对路径。
	 * 
	 * @return
	 */
	public String getDocFilePath(){
		Thing root = thing.getRoot();
		String path = root.getMetadata().getPath();
		if(path == null){
			path = "";
		}
		
		path = path.replace('.', '/');
		
		if(root != thing){
			path = path + "." + thing.getMetadata().getPath().hashCode();
		}
		
		path = path + ".html";
		
		return path;
	}
	
	public static String getRelatePath(String path, String rpath){
		String[] rps = rpath.split("[/]");
		String[] ps = path.split("[/]");
		
		int index = 0;
		for(int i=0;i<rps.length - 1;i++){
			if(rps[i].equals(ps[i])){
				index++;
			}else{
				break;
			}
		}
		
		String str = "";
		for(int i=index; i<ps.length - 1; i++){
			str = str + "../";
		}
		
		for(int i=index; i<rps.length; i++){
			if(i == rps.length - 1){
				str = str + rps[i];
			}else{
				str = str + rps[i] + "/"; 
			}
		}
		
		return str;
	}
	/**
	 * 返回指定事物和本事物之间的相对路径。
	 * 
	 * @param rthing
	 * @return
	 */
	public String getRelatePath(Thing rthing){
		ThingDoc rthingDoc = context.getThingDoc(rthing);
		String rpath = rthingDoc.getDocFilePath();
		String path = getDocFilePath();
		
		return getRelatePath(path, rpath);
	}
	
	public String getUrl(Thing thing){
		if(context == null){
			return "do?sc=xworker.ide.worldExplorer.swt.http.ThingDoc&thing=" + thing.getMetadata().getPath();
		}else if(context.isThingExists(thing)){
			//事物也会生成文档
			return getRelatePath(thing);
		}
		
		return null;
	}
	
	public boolean urlExists(Thing thing){
		return getUrl(thing) != null;
	}
	
	/**
	 * 是否是自己定义的属性、动作或子事物。
	 * 
	 * @return
	 */
	public boolean isDefinedBySelf(){
		Thing parent = thing.getParent();
		if(parent != null){
			return parent == owner;
		}

		return false;
	}
	
	public boolean isRootThing(){
		return thing.getParent() == null;
	}
	
	/**
	 * 获取定义该事物的事物。
	 * 
	 * @return
	 */
	public Thing getDefiendThing(){
		return thing.getParent();
	}
	
	public String getDefiendThingUrl(){
		Thing d = getDefiendThing();
		if(d == null){
			return "";
		}
		
		String name = d.getMetadata().getName();
		String label = d.getMetadata().getLabel();
		if(!name.equals(label)){
			label = name + "(" + label + ")";
		}
		if(d != owner){
			String url = context.getThingUrl(owner, d);
			if(url != null){
				label = "<a href=\"" + url + "\"><b>" + label + "</b></a>";
			}
		}
		
		return label;
	}
	
	public String getLabel(){
		String name = thing.getMetadata().getName();
		String label = thing.getMetadata().getLabel();
		if(name.equals(label)){
			return name;
		}else{
			return name + "(" + label + ")";
		}
	}
	
	public String getName(){
		String name = null;
		Thing parent = thing;
	
		while(parent != null){
	
			String theName = null;
			if(parent.getParent() == null){
				String path = parent.getMetadata().getPath();
				int index  = path.lastIndexOf(".");
				if(index != -1){
					theName = path.substring(index + 1, path.length());
				}else{
					theName = path;
				}
			}else{
				theName = parent.getMetadata().getName();
			}
			
			if(name != null){
				name = theName + "." + name;
			}else{
				name = theName;
			}
			
			parent = parent.getParent();
		}
		
		return name;
	}
	
	public String getSimpleDescription(){
		String description = thing.getMetadata().getDescription();
		if(description != null){
			return getFirstLine(description);
		}else{
			return "";
		}
	}
	
	public String getDescription(){
		String description = thing.getMetadata().getDescription();
		if(description != null){
			return description;
		}else{
			return "";
		}
	}
	
	
	/**
	 * 取文档的第一行。
	 * 
	 * @param descritpion
	 * @return
	 */
	private String getFirstLine(String description){
		if(description == null){
			return "";
		}
		
		//取段落
		description = description.trim();
		description = subBefore(description, "<p/>");
		description = subBefore(description, "<P/>");
		description = subBefore(description, "<p />");
		description = subBefore(description, "<P />");
		description = subBefore(description, "</p>");
		description = subBefore(description, "</P>");
		description = subBefore(description, "<br/>");
		description = subBefore(description, "<br />");
		description = subBefore(description, "<BR/>");
		description = subBefore(description, "<BR />");
		description = subBefore(description, "<Br/>");
		description = description.replaceAll("(<p>)", "");
		description = description.replaceAll("(<P>)", "");
		
		return description;		
	}
	
	private String subBefore(String str, String p){
		int index = str.indexOf(p);
		if(index != -1){
			return str.substring(0, index);
		}else{
			return str;
		}
	}

	@Override
	public int compareTo(Doc o) {
		return getLabel().compareTo(o.getLabel());
	}
}
