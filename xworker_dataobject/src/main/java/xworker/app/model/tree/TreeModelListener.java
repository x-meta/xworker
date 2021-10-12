package xworker.app.model.tree;

public interface TreeModelListener {
    void itemUpdated(TreeModelItem treeModelItem);

    /**
     * 一般是子节点发生的变动。
     * @param treeModelItem 子节点发生了改变的树节点。
     */
    void itemRefreshed(TreeModelItem treeModelItem);

    void itemRemoved(TreeModelItem treeModelItem);

    void itemAdded(TreeModelItem parentTreeModelItem, TreeModelItem treeModelItem, int index);
}
