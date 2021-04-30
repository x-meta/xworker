package xworker.javafx.util.selection;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import xworker.javafx.util.Selector;

import java.util.ArrayList;
import java.util.List;

public class TreeTableViewSelector<T> implements Selector<T> {
    TreeTableView<T> treeTableView;

    public TreeTableViewSelector(TreeTableView<T> treeTableView){
        this.treeTableView = treeTableView;
    }

    @Override
    public List<T> getSelectItems() {
        List<T> items = new ArrayList<>();
        for(TreeItem<T> item : treeTableView.getSelectionModel().getSelectedItems()){
            items.add(item.getValue());
        }
        return items;
    }

    @Override
    public T getSelectItem() {
        TreeItem<T> item = treeTableView.getSelectionModel().getSelectedItem();
        return item != null ? item.getValue() : null;
    }
}
