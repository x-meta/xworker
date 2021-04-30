package xworker.javafx.util.selection;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import xworker.javafx.util.Selector;

import java.util.ArrayList;
import java.util.List;

public class TreeViewSelector<T> implements Selector<T> {
    TreeView<T> treeView;

    public TreeViewSelector(TreeView<T> treeView){
        this.treeView = treeView;
    }

    @Override
    public List<T> getSelectItems() {
        List<T> items = new ArrayList<>();
        for(TreeItem<T> item : treeView.getSelectionModel().getSelectedItems()){
            items.add(item.getValue());
        }
        return items;
    }

    @Override
    public T getSelectItem() {
        TreeItem<T> item = treeView.getSelectionModel().getSelectedItem();
        return item != null ? item.getValue() : null;
    }
}
