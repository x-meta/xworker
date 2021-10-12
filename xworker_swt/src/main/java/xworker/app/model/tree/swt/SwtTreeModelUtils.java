package xworker.app.model.tree.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelItem;
import xworker.app.model.tree.TreeModelListener;
import xworker.lang.executor.Executor;
import xworker.swt.util.SwtUtils;
import xworker.util.Callback;

import java.util.List;

public class SwtTreeModelUtils {
    private static final String TAG = SwtTreeModelUtils.class.getName();

    /**
     * 把一个TreeModel绑定到指定的对象上，对象可以是控件或其他，如果绑定成功返回true，否则返回false。
     *
     * @param treeModel 要绑定的TreeModel
     * @param object 要绑定的对象
     * @return 是否绑定成功
     */
    public static boolean bind(TreeModel treeModel, Object object, ActionContext actionContext){
        if(object instanceof Tree){
            bindTree(treeModel, (Tree) object, actionContext);
            return true;
        }else if(object instanceof  TreeItem){
            bindTreeItem(treeModel, (TreeItem) object, actionContext);
        }

        return false;
    }

    public static void bindTreeItem(final TreeModel treeModel, final TreeItem parentItem, ActionContext actionContext){
        treeModel.setBindObjet(parentItem);
        TreeModelItem rootItem = treeModel.getRoot();
        if(rootItem == null){
            Executor.warn(TAG, "Root item is null, path=" + treeModel.getThing().getMetadata().getPath());
            return;
        }

        bindTreeSelectionListener(treeModel, parentItem.getParent(), actionContext);

        if(treeModel.isRootVisible()){
            TreeItem treeItem  = new TreeItem(parentItem, SWT.NONE);
            initTreeItem(rootItem, treeItem, actionContext);

            if(!treeModel.isDelayLoad()){
                treeModel.loadChilds(rootItem, new TreeCallback(null, parentItem, actionContext));
            }
        }else{
            treeModel.loadChilds(rootItem, new TreeCallback(null, parentItem, actionContext));
        }
    }

    public static void bindTree(final TreeModel treeModel, final Tree tree, final ActionContext actionContext){
        treeModel.setBindObjet(tree);
        TreeModelItem rootItem = treeModel.getRoot();
        if(rootItem == null){
            Executor.warn(TAG, "Root item is null, path=" + treeModel.getThing().getMetadata().getPath());
            return;
        }

        bindTreeSelectionListener(treeModel, tree, actionContext);

        if(treeModel.isRootVisible()){
            TreeItem treeItem  = new TreeItem(tree, SWT.NONE);
            initTreeItem(rootItem, treeItem, actionContext);

            if(!treeModel.isDelayLoad()){
                treeModel.loadChilds(rootItem, new TreeCallback(null, treeItem, actionContext));
            }
        }else{
            treeModel.loadChilds(rootItem, new TreeCallback(null, tree, actionContext));
        }
    }

    public static void bindTreeSelectionListener(TreeModel treeModel, Tree tree, ActionContext actionContext){
        boolean have = false;
        for(Listener listener : tree.getListeners(SWT.Selection)){
            if(listener instanceof TreeSelectionListener){
                have = true;
                break;
            }
        }

        if(!have){
            Listener selectionListener = new TreeSelectionListener(treeModel, tree, tree, actionContext);

            tree.addListener(SWT.Selection, selectionListener);
            tree.addListener(SWT.DefaultSelection, selectionListener);

        }
    }

    public static void initTreeItem(TreeModelItem item, TreeItem treeItem, ActionContext actionContext){
        treeItem.setText(item.getText());
        treeItem.setData(item);

        Image icon = SwtUtils.createImage(treeItem.getParent(), item.getIcon(), actionContext);
        if(icon != null){
            treeItem.setImage(icon);
        }

        Color foreground = SwtUtils.createColor(treeItem.getParent(), item.getForeground(), actionContext);
        if(foreground != null){
            treeItem.setForeground(foreground);
        }
        Color background = SwtUtils.createColor(treeItem.getParent(), item.getBackground(), actionContext);
        if(background != null){
            treeItem.setBackground(background);
        }
        if(item.getFontSize() > 0 || item.getFontStyle() != null || item.getFontName() != null){
            String fontValue = null;
            if(item.getFontName() != null){
                fontValue = item.getFontName() + "|";
            }else{
                fontValue = "|";
            }
            if(item.getFontSize() > 0){
                fontValue = fontValue + item.getFontSize() + "|";
            }else{
                fontValue = fontValue + 10 + "|";
            }
            String fontStyle = item.getFontStyle();
            if(TreeModelItem.FONT_BOLD.equals(fontStyle)){
                fontValue = fontValue + "2";
            }else if(TreeModelItem.FONT_BOLD_ITALIC.equals(fontStyle)){
                fontValue = fontValue + "3";
            }else if(TreeModelItem.FONT_ITALIC.equals(fontStyle)){
                fontValue = fontValue + "1";
            }else{
                fontValue = fontValue + "0";
            }
            Font font = SwtUtils.createFont(treeItem.getParent(), fontValue, actionContext);
            if(font != null){
                treeItem.setFont(font);
            }
        }
    }

    static class TreeSelectionListener implements Listener, TreeModelListener, DisposeListener {
        TreeModel treeModel;
        Tree tree;
        Object rootItem;
        ActionContext actionContext;

        public TreeSelectionListener(TreeModel treeModel, Tree tree, Object rootItem, ActionContext actionContext){
            this.treeModel = treeModel;
            this.tree = tree;
            this.actionContext = actionContext;
            this.rootItem = rootItem;

            treeModel.addListener(this);
        }

        @Override
        public void handleEvent(Event event) {
            TreeItem treeItem = (TreeItem) event.item;
            Object data =  treeItem.getData();
            if(data instanceof  TreeModelItem){
                TreeModelItem treeModelItem = (TreeModelItem) data;
                if(!treeModelItem.isChildInited()){
                    treeModelItem.setExpanded(true);
                    treeModelItem.getTreeModel().loadChilds(treeModelItem, new TreeCallback(treeModelItem, treeItem, actionContext));
                }
            }
        }

        @Override
        public void itemUpdated(TreeModelItem treeModelItem) {
            if(tree.isDisposed()){
                return;
            }

            tree.getDisplay().asyncExec(() ->{
                for(TreeItem treeItem : tree.getItems()){
                    updateItem(treeItem, treeModelItem);
                }
            });

        }

        private void updateItem(TreeItem treeItem, TreeModelItem treeModelItem){
            Object data = treeItem.getData();
            if(data instanceof TreeModelItem){
                TreeModelItem item = (TreeModelItem) data;
                if(item == treeModelItem){
                    initTreeItem(treeModelItem, treeItem, actionContext);
                }
            }

            for(TreeItem childItem : treeItem.getItems()){
                updateItem(childItem, treeModelItem);
            }
        }

        @Override
        public void itemRefreshed(TreeModelItem treeModelItem) {
            if(tree.isDisposed()){
                return;
            }

            tree.getDisplay().asyncExec(() ->{
                if(!treeModel.isRootVisible() && treeModelItem == treeModel.getRootItem()){
                    //刷新整个根节点
                    if(rootItem instanceof Tree){
                        Tree tree = (Tree) rootItem;
                        for(TreeItem item : tree.getItems()){
                            Object data = item.getData();
                            if(data instanceof TreeModelItem && ((TreeModelItem) data).getParent() == treeModelItem){
                                item.dispose();
                            }
                        }

                        for(TreeModelItem item : treeModelItem.getItems()){
                            TreeItem newItem = new TreeItem(tree,  SWT.NONE);
                            initTreeItem(item, newItem, actionContext);
                        }
                    }else if(rootItem instanceof TreeItem){
                        TreeItem treeItem = (TreeItem) rootItem;
                        for(TreeItem item : treeItem.getItems()){
                            Object data = item.getData();
                            if(data instanceof TreeModelItem && ((TreeModelItem) data).getParent() == treeModelItem){
                                item.dispose();
                            }
                        }

                        for(TreeModelItem item : treeModelItem.getItems()){
                            TreeItem newItem = new TreeItem(treeItem,  SWT.NONE);
                            initTreeItem(item, newItem, actionContext);
                        }
                    }

                }else {
                    for (TreeItem treeItem : tree.getItems()) {
                        refreshItem(treeItem, treeModelItem);
                    }
                }
            });
        }

        private void refreshItem(TreeItem treeItem, TreeModelItem treeModelItem){
            Object data = treeItem.getData();
            if(data instanceof TreeModelItem){
                TreeModelItem item = (TreeModelItem) data;
                if(item == treeModelItem){
                    initTreeItem(treeModelItem, treeItem, actionContext);

                    //刷新子节点
                    for(TreeItem childItem : treeItem.getItems()){
                        childItem.dispose();
                    }

                    //加载子节点
                    TreeModel treeModel = treeModelItem.getTreeModel();
                    treeModelItem.setExpanded(true);
                    treeModel.loadChilds(treeModelItem, new TreeCallback(treeModelItem, treeItem, actionContext));

                    return;
                }
            }

            for(TreeItem childItem : treeItem.getItems()){
                refreshItem(childItem, treeModelItem);
            }
        }

        @Override
        public void itemRemoved(TreeModelItem treeModelItem) {
            if(tree.isDisposed()){
                return;
            }

            tree.getDisplay().asyncExec(() ->{
                for(TreeItem treeItem : tree.getItems()){
                    removeItem(treeItem, treeModelItem);
                }
            });
        }

        private void removeItem(TreeItem treeItem, TreeModelItem treeModelItem){
            Object data = treeItem.getData();
            if(data instanceof TreeModelItem){
                TreeModelItem item = (TreeModelItem) data;
                if(item == treeModelItem){
                    treeItem.dispose();
                    return;
                }
            }

            for (TreeItem childItem : treeItem.getItems()) {
                removeItem(childItem, treeModelItem);
            }
        }

        @Override
        public void itemAdded(TreeModelItem parentTreeModelItem, TreeModelItem treeModelItem, int index) {
            if(tree.isDisposed()){
                return;
            }

            tree.getDisplay().asyncExec(() ->{
                if(parentTreeModelItem == parentTreeModelItem.getTreeModel().getRootItem()){
                    TreeItem item = new TreeItem(tree, index, SWT.NONE);
                    initTreeItem(treeModelItem, item, actionContext);
                }else{
                    for(TreeItem treeItem : tree.getItems()){
                        addItem(treeItem, parentTreeModelItem, treeModelItem, index);
                    }
                }
            });
        }

        private void addItem(TreeItem treeItem, TreeModelItem parentItem, TreeModelItem treeModelItem, int index){
            Object data = treeItem.getData();
            if(data instanceof TreeModelItem){
                TreeModelItem item = (TreeModelItem) data;
                if(item == parentItem){
                    TreeItem aitem = new TreeItem(treeItem, index, SWT.NONE);
                    initTreeItem(treeModelItem, aitem, actionContext);
                    return;
                }
            }

            for (TreeItem childItem : treeItem.getItems()) {
                addItem(childItem, parentItem, treeModelItem, index);
            }
        }

        @Override
        public void widgetDisposed(DisposeEvent disposeEvent) {
            treeModel.removeListener(this);
        }
    }

    static class TreeCallback implements Callback<List<TreeModelItem>, Void> {
        Tree tree;
        TreeItem treeItem;
        TreeModelItem paremtItem;
        ActionContext actionContext;

        public TreeCallback(TreeModelItem paremtItem, Tree tree, ActionContext actionContext){
            this.paremtItem = paremtItem;
            this.tree = tree;
            this.actionContext = actionContext;
        }

        public TreeCallback(TreeModelItem paremtItem, TreeItem treeItem, ActionContext actionContext){
            this.paremtItem = paremtItem;
            this.treeItem = treeItem;
            this.actionContext = actionContext;
        }

        @Override
        public Void call(List<TreeModelItem> treeModelItems) {
            Display display = tree != null ? tree.getDisplay() : treeItem.getDisplay();
            display.asyncExec(new Runnable() {
                @Override
                public void run() {
                    try{
                        if((tree != null && tree.isDisposed()) || (treeItem != null && treeItem.isDisposed())){
                            return;
                        }

                        for(TreeModelItem treeModelItem : treeModelItems){
                            TreeItem item = tree != null ? new TreeItem(tree, SWT.NONE) : new TreeItem(treeItem, SWT.NONE);
                            initTreeItem(treeModelItem, item, actionContext);

                            TreeModel treeModel = treeModelItem.getTreeModel();
                            if(treeModelItem.isExpanded() || !treeModel.isDelayLoad()){
                                //加载子节点
                                treeModel.loadChilds(treeModelItem, new TreeCallback(treeModelItem, item, actionContext));
                            }
                        }

                        if(paremtItem != null && paremtItem.isExpanded()){
                            if(treeItem != null){
                                treeItem.setExpanded(true);
                            }
                        }

                        if(treeItem != null) {
                            //也重新初始化一下父节点
                            initTreeItem(paremtItem, treeItem, actionContext);
                        }
                    }catch(Exception e){
                        Executor.warn(TAG, "Init tree items error", e);
                    }
                }
            });

            return null;
        }
    }
}
