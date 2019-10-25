package xworker.libdgx.tools.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResourceInfo {
	long id;
	String name;
	String path;
	String type;
	Date lastModified;
	long parentTreeId;
	long treeId;
	String description;	
	List<ResourceInfo> childs = new ArrayList<ResourceInfo>();
}
