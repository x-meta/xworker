package xworker.app.model.tree.implnew;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilString;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelItem;
import xworker.app.model.tree.TreeModelUtils;
import xworker.util.UtilFileIcon;
import xworker.util.XWorkerUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FileTreeModel {
    public static TreeModelItem getRoot(ActionContext actionContext) throws IOException {
        Thing self = actionContext.getObject("self");
        TreeModel treeModel = actionContext.getObject("treeModel");

        String filePath = self.doAction("getFilePath", actionContext);
        if(filePath == null || filePath.isEmpty()){
            filePath = ".";
        }

        File file = new File(filePath);
        return TreeModelUtils.toItem(treeModel, null, self, file, actionContext);
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
            List<File> fileList = Arrays.asList(files);
            fileList.sort((o1, o2) -> {
                if(o1.isDirectory() && o2.isFile()){
                    return -1;
                }else if(o1.isFile() && o2.isDirectory()){
                    return 1;
                }else{
                    return o1.getName().compareTo(o2.getName());
                }
            });
            boolean showFile = self.getBoolean("showFile");
            for(File file : fileList){
                if(file.isFile()){
                    if(showFile){
                        TreeModelItem item = TreeModelUtils.toItem(treeModel, parentItem, self, file, actionContext);
                        items.add(item);
                    }
                }else{
                    TreeModelItem item = TreeModelUtils.toItem(treeModel, parentItem, self, file, actionContext);
                    items.add(item);
                }
            }
        }

        return items;
    }

    //xworker.app.model.tree.implnew.FileTreeModel/@create/@createBySources
    public static List<TreeModelItem> createBySources(ActionContext actionContext) throws IOException {
        Thing self = actionContext.getObject("self");
        TreeModel treeModel = actionContext.getObject("treeModel");
        TreeModelItem parentItem = actionContext.getObject("parentItem");
        Object[] sources = actionContext.getObject("sources");

        List<TreeModelItem> items = new ArrayList<>();

        for(Object obj : sources){
            if(obj instanceof  File){
                File file = (File) obj;
                TreeModelItem item = TreeModelUtils.toItem(treeModel, parentItem, self, file, actionContext);
                items.add(item);
            }
        }

        return items;
    }

    public static TreeModelItem getItemById(ActionContext actionContext) throws IOException {
        Thing self = actionContext.getObject("self");
        TreeModel treeModel = actionContext.getObject("treeModel");
        String id = actionContext.getObject("id");
        File file = new File(id);
        return TreeModelUtils.toItem(treeModel, null, self, file, actionContext);
    }

}
