package xworker.net.jsch;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelItem;
import xworker.app.model.tree.implnew.FileTreeModel;
import xworker.lang.executor.Executor;
import xworker.util.UtilFileIcon;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SftpTreeModel {
    private static final String TAG = SftpTreeModel.class.getName();

    public static TreeModelItem getRoot(ActionContext actionContext) throws IOException {
        Thing self = actionContext.getObject("self");
        TreeModel treeModel = actionContext.getObject("treeModel");

        String rootPath = self.doAction("getRootPath", actionContext);

        File file = new File(rootPath);
        TreeModelItem rootItem = new TreeModelItem(treeModel, null);
        FileTreeModel.init(file, rootItem, actionContext);
        rootItem.setDataId(rootPath);
        rootItem.setId(treeModel.getThing().getMetadata().getPath() + "|" + rootPath);

        return rootItem;
    }

    public static List<TreeModelItem> getChilds(ActionContext actionContext) throws SftpException, IOException {
        Thing self = actionContext.getObject("self");
        TreeModel treeModel = actionContext.getObject("treeModel");
        TreeModelItem parentItem = actionContext.getObject("parentItem");

        String path = parentItem.getDataId();
        ChannelSftp channelSftp = self.doAction("getChannelSftp", actionContext);
        if(channelSftp == null || channelSftp.isClosed()){
            Executor.warn(TAG, "ChannelSftp is null or closed, path=" + self.getMetadata().getPath());
            return Collections.emptyList();
        }

        List<TreeModelItem> items = new ArrayList<>();
        Vector<Object> files = channelSftp.ls(path);
        String id = treeModel.getThing().getMetadata().getPath() + "|";
        for(Object file : files){
            ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) file;
            String fileName = entry.getFilename();
            if(fileName.equals(".") || fileName.equals("..")){
                continue;
            }

            TreeModelItem item = new TreeModelItem(treeModel, parentItem);
            String filePath = path + "/" + fileName;
            item.setSource(entry);
            item.setId(id + filePath );
            item.setDataId(filePath);
            item.setText(fileName);
            if(entry.getAttrs().isDir()){
                item.setIcon("icons/folder.png");
            }else{
                String ext = null;
                int index = fileName.lastIndexOf(".");
                if(index != -1){
                    ext = fileName.substring(index + 1, fileName.length());
                }
                if(ext != null) {
                    String icon = UtilFileIcon.getFileIcon(ext, false);
                    item.setIcon(icon);
                }
                item.setItems(Collections.emptyList());
            }
            items.add(item);
        }

        Collections.sort(items, new Comparator<TreeModelItem>() {
            @Override
            public int compare(TreeModelItem o1, TreeModelItem o2) {
                ChannelSftp.LsEntry o1Entry = (ChannelSftp.LsEntry) o1.getSource();
                ChannelSftp.LsEntry o2Entry = (ChannelSftp.LsEntry) o2.getSource();

                if(o1Entry.getAttrs().isDir() && !o2Entry.getAttrs().isDir()){
                    return -1;
                }else if(!o1Entry.getAttrs().isDir() && o2Entry.getAttrs().isDir()){
                    return 1;
                }

                return o1.getText().compareTo(o2.getText());
            }
        });

        return items;
    }
}
