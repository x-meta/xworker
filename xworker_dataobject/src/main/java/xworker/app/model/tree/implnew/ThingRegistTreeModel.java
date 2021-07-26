package xworker.app.model.tree.implnew;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelItem;
import xworker.util.ThingGroup;
import xworker.util.XWorkerUtils;

import java.util.ArrayList;
import java.util.List;

public class ThingRegistTreeModel {
    public static TreeModelItem getRoot(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TreeModel treeModel = actionContext.getObject("treeModel");

        Thing registThing = self.doAction("getRegistThing", actionContext);
        String registType = self.doAction("getRegistType", actionContext);
        List<String> keys = self.doAction("getKeys", actionContext);
        Boolean parent = self.doAction("isParent", actionContext);
        Boolean noDescriptor = self.doAction("isNoDescriptor", actionContext);

        List<Thing> things = XWorkerUtils.searchRegistThings(registThing, registType, keys, parent, noDescriptor, actionContext);
        List<ThingGroup> groups = XWorkerUtils.getThingGroups(things);

        //实际没有根节点
        TreeModelItem rootItem =  new TreeModelItem(treeModel, null);
        List<TreeModelItem> items = new ArrayList<>();
        for(ThingGroup childGroup : groups){
            items.add(toTreeModelItem(treeModel, rootItem, childGroup));
        }

        rootItem.setItems(items);

        return rootItem;
    }

    public static List<TreeModelItem> getChilds(ActionContext actionContext){
        TreeModelItem parentItem = actionContext.getObject("parentItem");
        return parentItem.getItems();
    }

    private static TreeModelItem toTreeModelItem(TreeModel treeModel, TreeModelItem parentItem, ThingGroup thingGroup){
        TreeModelItem item = new TreeModelItem(treeModel, parentItem);
        String id = treeModel.getThing().getMetadata().getPath() + "|";
        if(thingGroup.getThing() == null){
            //是目录
            item.setSource(thingGroup);
            item.setText(thingGroup.getGroup());
            item.setId(id + thingGroup.getGroup());
            item.setDataId(thingGroup.getGroup());
            item.setIcon("icons/folder.png");
        }else{
            Thing thing = thingGroup.getThing();
            item.setSource(thing);
            item.setText(thing.getMetadata().getLabel());
            item.setIcon(XWorkerUtils.getThingIcon(thing));
            item.setDataId(thing.getMetadata().getPath());
            item.setId(id + thing.getMetadata().getPath());
        }

        List<TreeModelItem> items = new ArrayList<>();
        for(ThingGroup childGroup : thingGroup.getChilds()){
            items.add(toTreeModelItem(treeModel, item, childGroup));
        }

        item.setItems(items);

        return item;
    }
}
