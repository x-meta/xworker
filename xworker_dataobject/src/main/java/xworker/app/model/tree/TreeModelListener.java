package xworker.app.model.tree;

public interface TreeModelListener {
    public void itemUpdated(TreeModelItem treeModelItem);

    public void itemRefreshed(TreeModelItem treeModelItem);

    public void itemRemoved(TreeModelItem treeModelItem);

    public void itemAdded(TreeModelItem parentTreeModelItem, TreeModelItem treeModelItem, int index);
}
