package xworker.javafx.util.selection;

import javafx.scene.control.TableView;
import xworker.javafx.util.Selector;

import java.util.List;

public class TableViewSelector<T> implements Selector<T> {
    TableView<T> tableView;

    public TableViewSelector(TableView<T> tableView){
        this.tableView = tableView;
    }

    @Override
    public List<T> getSelectItems() {
        return tableView.getSelectionModel().getSelectedItems();
    }

    @Override
    public T getSelectItem() {
        return tableView.getSelectionModel().getSelectedItem();
    }
}
