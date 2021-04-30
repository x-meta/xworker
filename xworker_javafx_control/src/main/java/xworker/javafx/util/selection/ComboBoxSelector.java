package xworker.javafx.util.selection;

import javafx.scene.control.ComboBox;
import xworker.javafx.util.Selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComboBoxSelector<T> implements Selector<T> {
    ComboBox<T> comboBox;

    public ComboBoxSelector(ComboBox<T> comboBox){
        this.comboBox = comboBox;
    }

    @Override
    public List<T> getSelectItems() {
        T item =  comboBox.getSelectionModel().getSelectedItem();
        if(item != null){
            List<T> list = new ArrayList<>();
            list.add(item);
            return list;
        }else{
            return Collections.emptyList();
        }
    }

    @Override
    public T getSelectItem() {
        return comboBox.getSelectionModel().getSelectedItem();
    }
}
