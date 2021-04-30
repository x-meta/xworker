package xworker.javafx.util.selection;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import xworker.javafx.util.Selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChoiceBoxSelector <T> implements Selector<T> {
    ChoiceBox<T> choiceBox;

    public ChoiceBoxSelector(ChoiceBox<T> choiceBox){
        this.choiceBox = choiceBox;
    }

    @Override
    public List<T> getSelectItems() {
        T item =  choiceBox.getSelectionModel().getSelectedItem();
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
        return choiceBox.getSelectionModel().getSelectedItem();
    }
}
