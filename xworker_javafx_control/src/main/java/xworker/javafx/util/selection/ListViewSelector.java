package xworker.javafx.util.selection;

import javafx.scene.control.ListView;
import xworker.javafx.util.Selector;

import java.util.List;

public class ListViewSelector<T> implements Selector<T> {
    ListView<T> listView;

    public ListViewSelector(ListView<T> listView){
        this.listView = listView;
    }

    @Override
    public List<T> getSelectItems() {
        return listView.getSelectionModel().getSelectedItems();
    }

    @Override
    public T getSelectItem() {
        return listView.getSelectionModel().getSelectedItem();
    }
}
