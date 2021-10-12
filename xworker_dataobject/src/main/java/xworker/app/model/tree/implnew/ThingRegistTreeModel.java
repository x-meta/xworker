package xworker.app.model.tree.implnew;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelItem;
import xworker.app.model.tree.TreeModelUtils;
import xworker.util.ThingGroup;
import xworker.util.XWorkerUtils;

import java.util.ArrayList;
import java.util.Collections;
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
        return TreeModelUtils.toItem(treeModel, parentItem, thingGroup);
    }

    //xworker.app.model.tree.implnew.ThingRegistTreeModel/@actions/@createBySources
    public static List<TreeModelItem> createBySources(ActionContext actionContext){
        return Collections.emptyList();
    }
}
