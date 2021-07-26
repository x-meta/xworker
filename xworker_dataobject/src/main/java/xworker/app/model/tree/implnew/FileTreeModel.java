package xworker.app.model.tree.implnew;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelItem;
import xworker.util.UtilFileIcon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileTreeModel {
    public static TreeModelItem getRoot(ActionContext actionContext) throws IOException {
        Thing self = actionContext.getObject("self");
        TreeModel treeModel = actionContext.getObject("treeModel");

        String filePath = self.doAction("getFilePath", actionContext);
        if(filePath == null || filePath.isEmpty()){
            filePath = ".";
        }

        File file = new File(filePath);
        TreeModelItem item = new TreeModelItem(treeModel, null);
        init(file, item, actionContext);

        return item;
    }

    public static List<TreeModelItem> getChilds(ActionContext actionContext) throws IOException{
        Thing self = actionContext.getObject("self");
        TreeModel treeModel = actionContext.getObject("treeModel");
        TreeModelItem parentItem = actionContext.getObject("parentItem");

        List<TreeModelItem> items = new ArrayList<>();
        File parentFile = (File) parentItem.getSource();
        File[] files = null;
        if(":".equals(parentFile.getName())){
            files = File.listRoots();
        }else if(parentFile.isDirectory()){
            files = parentFile.listFiles();
        }

        if(files != null){
            boolean showFile = self.getBoolean("showFile");
            for(File file : files){
                if(file.isFile()){
                    if(showFile){
                        TreeModelItem item = new TreeModelItem(treeModel, parentItem);
                        init(file, item, actionContext);
                        items.add(item);
                    }
                }else{
                    TreeModelItem item = new TreeModelItem(treeModel, parentItem);
                    init(file, item, actionContext);
                    items.add(item);
                }
            }
        }

        return items;
    }

    public static TreeModelItem getItemById(ActionContext actionContext) throws IOException {
        TreeModel treeModel = actionContext.getObject("treeModel");
        String id = actionContext.getObject("id");
        File file = new File(id);
        TreeModelItem item = new TreeModelItem(treeModel, null);
        init(file, item, actionContext);

        return item;
    }

    public static void init(File file, TreeModelItem item, ActionContext actionContext) throws IOException {
        String name = file.getName();
        if(name.equals(":")){
            item.setText(UtilString.getString("lang:d=计算机&en=Computer", actionContext));
            item.setIcon("icons/computer.png");
            item.setId(":");
        }else{
            if(name.trim().isEmpty()){
                item.setText(file.getPath());
            }else {
                item.setText(name);
            }
            if(file.isDirectory()){
                item.setIcon("icons/folder.gif");
            }else {
                item.setIcon(UtilFileIcon.getFileIcon(file, false));
            }
            item.setId(file.getCanonicalPath());
        }

        String path;
        try {
            path = file.getCanonicalPath();
        }catch(Exception e){
            path = file.getPath();
        }
        item.setDataId(path);
        item.setId(item.getTreeModel().getThing().getMetadata().getPath() + "|" + path);
        item.setSource(file);
    }
}
