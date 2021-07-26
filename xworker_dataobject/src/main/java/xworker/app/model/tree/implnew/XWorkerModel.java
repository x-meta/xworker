package xworker.app.model.tree.implnew;

import org.xmeta.*;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelItem;
import xworker.util.UtilFileIcon;
import xworker.util.XWorkerUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XWorkerModel {
    public static TreeModelItem getRoot(ActionContext actionContext) throws IOException {
        Thing self = actionContext.getObject("self");

        String rootIndexType = self.doAction("getRootIndexType", actionContext);
        String rootIndexPath = self.doAction("getRootIndexPath", actionContext);


        Index index = Index.getIndex(rootIndexType, rootIndexPath);
        if(index == null){
            index = Index.getInstance();
        }

        TreeModel treeModel = actionContext.getObject("treeModel");
        return toTreeModelItem(treeModel, null, index);
    }

    public static List<TreeModelItem> getChilds(ActionContext actionContext) throws IOException {
        Thing self = actionContext.getObject("self");

        boolean showFile = self.getBoolean("showFile");
        boolean showThingChild = self.getBoolean("showThingChild");

        TreeModel treeModel = actionContext.getObject("treeModel");
        TreeModelItem parentItem = actionContext.getObject("parentItem");

        List<TreeModelItem> items = new ArrayList<>();
        Object obj = parentItem.getSource();
        if(obj instanceof  Index){
            Index index = (Index) obj;
            if(Index.TYPE_THING.equals(index.getType())){
                if(showThingChild){
                    Thing thing = World.getInstance().getThing(index.getPath());
                    for(Thing child : thing.getChilds()){
                        items.add(toTreeModelItem(treeModel, parentItem, child));
                    }
                }
            }else {
                for (Index child : ((Index) obj).getChilds()) {
                    if (!showFile && child.getType().equals(Index.TYPE_FILE)) {
                        continue;
                    }

                    items.add(toTreeModelItem(treeModel, parentItem, child));
                }
            }
        }else if(obj instanceof Thing){
            if(showThingChild){
                for(Thing child : ((Thing) obj).getChilds()){
                    items.add(toTreeModelItem(treeModel, parentItem, child));
                }
            }
        }

        return items;
    }

    public static TreeModelItem getItemById(ActionContext actionContext) throws IOException{
        TreeModel treeModel = actionContext.getObject("treeModel");
        String id = actionContext.getObject("id");
        if(id == null || id.isEmpty()){
            return toTreeModelItem(treeModel, null, Index.getInstance());
        }

        int index = id.indexOf(":");
        String type = null;
        String path = null;
        if(index != -1){
            type = id.substring(0, index);
            path = id.substring(index + 1, id.length());
        }
        if("thingManager:".equals(type)){
            return toTreeModelItem(treeModel, null, World.getInstance().getThingManager(path));
        }else if("category".equals(type)){
            return toTreeModelItem(treeModel, null, World.getInstance().get(path));
        }else if("thing".equals(type)){
            return toTreeModelItem(treeModel, null, World.getInstance().getThing(path));
        }else if("file".equals(type)){
            return toTreeModelItem(treeModel, null, new File(path));
        }else if("object".equals(type)){
            //对象类型不支持
            return null;
        }else{
            return toTreeModelItem(treeModel, null, Index.getIndexById(id));
        }
    }

    public static TreeModelItem toTreeModelItem(TreeModel treeModel, TreeModelItem parentItem, Object obj) throws IOException {
        if(obj == null){
            return null;
        }

        TreeModelItem item = new TreeModelItem(treeModel, parentItem);
        item.setSource(obj);

        String id = treeModel.getThing().getMetadata().getPath() + "|";
        if(obj instanceof  Index) {
            Index index = (Index) obj;
            item.setText(index.getLabel());
            String indexId = Index.getIndexId(index);
            item.setId(id + indexId);
            item.setDataId(indexId);

            String type = index.getType();
            if (Index.TYPE_WORLD.equals(type)) {
                item.setIcon("/icons/xworker/project.gif");
            } else if ("thingManager".equals(type)) {
                item.setIcon("/icons/xworker/project1.gif");
            } else if ("category".equals(type)) {
                item.setIcon("/icons/xworker/package.gif");
            } else if ("thing".equals(type)) {
                if (index.getIndexObject() instanceof ThingIndex) {
                    item.setIcon("/icons/xworker/dataObject.gif");
                } else {
                    item.setIcon("xworker:core/images/swteditor/dataObjectChild.gif");
                }
            } else if (Index.TYPE_WORKING_SET.equals(type)) {
                item.setIcon("/icons/folder.gif");
            }else{
                item.setIcon("icons/page.png");
            }
        }else if(obj instanceof ThingManager){
            ThingManager thingManager = (ThingManager) obj;
            item.setTabId(thingManager.getName());
            item.setIcon("/icons/xworker/project1.gif");
            item.setId(id + "thingManager:" + thingManager.getName());
            item.setDataId("thingManager:" + thingManager.getName());
        }else if(obj instanceof Category){
            Category category = (Category) obj;
            item.setText(category.getSimpleName());
            item.setIcon("/icons/xworker/package.gif");
            item.setId(id + "category:" + category.getName());
            item.setDataId("category:" + category.getName());
        }else if(obj instanceof Thing){
            Thing thing = (Thing) obj;
            item.setText(thing.getMetadata().getLabel());
            item.setIcon(XWorkerUtils.getThingIcon(thing));
            item.setId(id + "thing:" + thing.getMetadata().getPath());
            item.setDataId("thing:" + thing.getMetadata().getPath());
        }else if(obj instanceof File){
            File file = (File) obj;
            item.setText(file.getName());
            item.setId(id + "file:" + file.getCanonicalPath());
            item.setDataId("file:" + file.getCanonicalPath());
            try {
                item.setIcon(UtilFileIcon.getFileIcon(file, false));
            }catch(Exception e){
                item.setIcon("icons/page.png");
            }
        }else{
            item.setText(String.valueOf(obj));
            item.setIcon("icons/page_delete.png");
            item.setId(id + "object:" + obj);
            item.setDataId("object:" + obj);
        }

        return item;
    }
}
