package xworker.app.model.tree.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Category;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;

import ognl.OgnlException;
import xworker.app.model.tree.impl.ThingRegistTreeModelActions.Group;

public class RegistedDescriptorsTreeModel {
	/**
	 * 获取子节点列表。
	 * 
	 * @param actionContext
	 * @return
	 * @throws IOException
	 * @throws OgnlException 
	 */
	public static Object getChilds(ActionContext actionContext) throws IOException, OgnlException{
		Thing self = (Thing) actionContext.get("self");
		World world = World.getInstance();
    	Thing coreThings = world.getThing("xworker.ide.config.Things");
    	
    	Group root = new Group();
    	
    	Map<String, Group> groupMap = new HashMap<String, Group>();
    	initThings(root, coreThings, groupMap);

    	for(ThingManager thingManager : world.getThingManagers()){
    	    Category rootCategory = thingManager.getCategory(null);
    	    for(Category lv1Category : rootCategory.getCategorys()){
    	        for(Category lv2Category : lv1Category.getCategorys()){
    	            String project = lv1Category.getSimpleName() + "." + lv2Category.getSimpleName();
    	            Thing things = world.getThing(project + ".config.Things");
    	            if(things != null){
    	                 if(project.equals("xworker.ide")){                    
    	                 }else{
    	                     initThings(root, things, groupMap);
    	                 }
    	            }
    	        }
    	    }
    	}

    	return root.toTreeNode(self).get("childs");
    }
	
	public static void initThings(Group group, Thing things, Map<String, Group> groupMap){
	    for(Thing child : things.getAllChilds()){
	        if("Category".equals(child.getThingName())){
	            Group childGroup = group.addThing(groupMap, child, child.getMetadata().getPath());
	            
	            initThings(childGroup, child, groupMap);
	        }
	    }	    
	}
}
