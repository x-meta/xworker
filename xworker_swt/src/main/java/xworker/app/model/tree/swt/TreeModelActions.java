package xworker.app.model.tree.swt;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelItem;
import xworker.lang.executor.Executor;
import xworker.util.UtilData;

public class TreeModelActions {
    private static final String TAG = TreeModelActions.class.getName();

    public static void reload(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        //获取treeModel
        Object treeModel = self.doAction("getTreeModel", actionContext);

        if(treeModel instanceof TreeModel){
            TreeModel tm = (TreeModel) treeModel;
            Object bindObject = tm.getBindObjet();
            if(bindObject instanceof Tree){
                Tree tree = (Tree) bindObject;
                //移除属于TreeModel的子节点
                for(TreeItem item : tree.getItems()){
                    if(item.getData() instanceof TreeModelItem){
                        TreeModelItem treeModelItem = (TreeModelItem) item.getData();
                        if(treeModelItem.getTreeModel() == tm){
                            item.dispose();
                        }
                    }
                }

                tm.getRootItem().setItems(null);
                SwtTreeModelUtils.bindTree(tm, tree, actionContext);
            }else if(bindObject instanceof TreeItem){
                TreeItem treeItem = (TreeItem) bindObject;
                //移除属于TreeModel的子节点
                for(TreeItem item : treeItem.getItems()){
                    if(item.getData() instanceof TreeModelItem){
                        TreeModelItem treeModelItem = (TreeModelItem) item.getData();
                        if(treeModelItem.getTreeModel() == tm){
                            item.dispose();
                        }
                    }
                }

                SwtTreeModelUtils.bindTreeItem(tm, treeItem, actionContext);
            }else{
                Executor.warn(TAG, "Can not reload tree model, unknown bind object: " + bindObject + ", thing=" + self.getMetadata().getPath());
            }
        }else{
            //调用treeModel的reload方法，这是旧的TreeModel，保留其用法
            ((Thing) treeModel).doAction("reload", actionContext);
        }
    }

    public static void refresh(ActionContext actionContext) {
        Thing self = actionContext.getObject("self");

        //获取tree
        Tree tree = self.doAction("getTree", actionContext);
        TreeItem[] items = tree.getSelection();
        //获取treeModel
        Object treeModel = self.doAction("getTreeModel", actionContext);
        if(items != null && items.length > 0){
            for(TreeItem item : items){
                Object data = item.getData();
                if(data instanceof TreeModelItem){
                    TreeModelItem treeModelItem = (TreeModelItem) data;
                    treeModelItem.getTreeModel().reload(treeModelItem);
                }else{
                    Thing thingTreeModel = (Thing) treeModel;
                    Object node = item.getData();

                    //调用treeModel的refresh方法
                    //println("refresh node=" + node);
                    if (node == null) {
                        //刷新根节点还有些bug，所以暂时先使用reload
                        thingTreeModel.doAction("reload", actionContext);
                    } else {
                        thingTreeModel.doAction("refresh", actionContext,"node", node);
                    }
                }
            }
        }else{
            //应该是根节点，执行刷新
            reload(actionContext);
        }
    }
}
