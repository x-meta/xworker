package xworker.javafx.util;

import javafx.scene.control.*;
import javafx.util.Callback;
import xworker.javafx.util.selection.*;

import java.util.HashMap;
import java.util.Map;


public class SelectorFactory {
    private final static Map<Class<?>, Callback<?, Selector<?>>> factorys= new HashMap<>();
    static{
        registSelectorFactory(TableView.class, (Callback<TableView<?>, Selector<?>>) TableViewSelector::new);
        registSelectorFactory(ComboBox.class, (Callback<ChoiceBox<?>, Selector<?>>) ChoiceBoxSelector::new);
        registSelectorFactory(ComboBox.class, (Callback<ComboBox<?>, Selector<?>>) ComboBoxSelector::new);
        registSelectorFactory(ListView.class, (Callback<ListView<?>, Selector<?>>) ListViewSelector::new);
        registSelectorFactory(TreeView.class, (Callback<TreeView<?>, Selector<?>>) TreeViewSelector::new);
        registSelectorFactory(TreeTableView.class, (Callback<TreeTableView<?>, Selector<?>>) TreeTableViewSelector::new);
    }

    public static void registSelectorFactory(Class<?> clss, Callback<?, Selector<?>> selectorCallback){
        factorys.put(clss, selectorCallback);
    }

    @SuppressWarnings("unchecked")
    public static Selector<?> getSelector(Object object){
        Callback<Object, Selector<?>> callback = (Callback<Object, Selector<?>>) factorys.get(object.getClass());
        if(callback != null){
            return callback.call(object);
        }
        return null;
    }
}
