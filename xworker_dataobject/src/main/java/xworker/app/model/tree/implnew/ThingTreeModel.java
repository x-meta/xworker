package xworker.app.model.tree.implnew;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelItem;
import xworker.util.XWorkerUtils;

import java.util.ArrayList;
import java.util.List;

public class ThingTreeModel {
    public static TreeModelItem getRoot(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        TreeModel treeModel = actionContext.getObject("treeModel");
        Thing thing = self.doAction("getThing", actionContext);
        if(thing != null){
            TreeModelItem item = new TreeModelItem(treeModel, null);
            item.setSource(thing);
            item.setText(thing.getMetadata().getLabel());
            item.setIcon(XWorkerUtils.getThingIcon(thing));
            item.setDataId(thing.getMetadata().getPath());
            item.setId(treeModel.getThing().getMetadata().getPath() + "|" + thing.getMetadata().getPath());
            return item;
        }

        return null;
    }

    public static List<TreeModelItem> getChilds(ActionContext actionContext){
        TreeModel treeModel = actionContext.getObject("treeModel");
        TreeModelItem parentItem = actionContext.getObject("parentItem");
        Object obj = parentItem.getSource();
        List<TreeModelItem> items = new ArrayList<>();

        if(obj instanceof  Thing){
            for(Thing child : ((Thing) obj).getChilds()){
                TreeModelItem item = new TreeModelItem(treeModel, parentItem);
                item.setSource(child);
                item.setText(child.getMetadata().getLabel());
                item.setIcon(XWorkerUtils.getThingIcon(child));
                item.setDataId(child.getMetadata().getPath());
                item.setId(treeModel.getThing().getMetadata().getPath() + "|" + child.getMetadata().getPath());

                items.add(item);
            }
        }

        return items;
    }

    public static TreeModelItem getItemById(ActionContext actionContext){
        TreeModel treeModel = actionContext.getObject("treeMode");
        Thing thing = World.getInstance().getThing(actionContext.getObject("id"));
        if(thing != null){
            TreeModelItem item = new TreeModelItem(treeModel, null);
            item.setSource(thing);
            item.setText(thing.getMetadata().getLabel());
            item.setIcon(XWorkerUtils.getThingIcon(thing));
            item.setDataId(thing.getMetadata().getPath());
            item.setId(treeModel.getThing().getMetadata().getPath() + "|" + thing.getMetadata().getPath());
            return item;
        }else{
            return null;
        }
    }
}
