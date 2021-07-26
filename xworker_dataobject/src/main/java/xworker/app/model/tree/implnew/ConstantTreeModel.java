package xworker.app.model.tree.implnew;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelItem;

import java.util.ArrayList;
import java.util.List;

public class ConstantTreeModel {
    public static TreeModelItem getRoot(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TreeModel treeModel = actionContext.getObject("treeModel");

        Thing root = self.getThing("RootItem@0");
        if(root != null){
            return new TreeModelItem(treeModel, null, root);
        }

        return null;
    }

    public static List<TreeModelItem> getChilds(ActionContext actionContext){
        TreeModel treeModel = actionContext.getObject("treeModel");
        TreeModelItem parentItem = actionContext.getObject("parentItem");

        List<TreeModelItem> items = new ArrayList<>();
        Object obj = parentItem.getSource();
        if(obj instanceof  Thing){
            Thing thing = (Thing) obj;
            if(thing.isThingByName("TreeItem")) {
                for (Thing child : thing.getChilds()) {
                    if("TreeItem".equals(child.getThingName())){
                        items.add(new TreeModelItem(treeModel, parentItem, child));
                    }else if("TreeModelItem".equals(child.getThingName())){
                        for(Thing modelThing : child.getChilds()){
                            try {
                                actionContext.push().put("self", modelThing);
                                TreeModel treeModel1 = TreeModel.create(actionContext);

                                if(treeModel1.isRootVisible()){
                                    items.add(treeModel1.getRoot());
                                }else{
                                    items.addAll(treeModel1.doLoadChilds(treeModel1.getRoot(), null));
                                }
                            }catch(Exception e){
                                actionContext.pop();
                            }
                        }
                    }
                }
            }
        }

        return items;
    }

    public static TreeModelItem getItemById(ActionContext actionContext){
        TreeModel treeModel = actionContext.getObject("treeMode");
        String id = actionContext.getObject("id");

        Thing thing = World.getInstance().getThing(id);
        if(thing != null){
            return new TreeModelItem(treeModel, null, thing);
        }else{
            return null;
        }
    }
}
