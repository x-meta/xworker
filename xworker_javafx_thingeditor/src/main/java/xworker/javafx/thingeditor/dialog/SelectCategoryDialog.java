package xworker.javafx.thingeditor.dialog;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TreeItem;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Category;
import org.xmeta.Index;
import org.xmeta.ThingManager;

public class SelectCategoryDialog {
    public static void init(ActionContext actionContext){
        javafx.scene.control.TreeView<Index> treeView = actionContext.getObject("treeView");
        final Dialog<Category> dialog = actionContext.getObject("dialog");

        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Index>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<Index>> observable, TreeItem<Index> oldValue, TreeItem<Index> newValue) {
                if(newValue == null){
                    dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
                    return;
                }

                Index index = newValue.getValue();
                String type = index.getType();
                if(Index.TYPE_THINGMANAGER.equals(type) || Index.TYPE_CATEGORY.equals(type)){
                    dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
                }else{
                    dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
                }
            }
        });

        dialog.setResultConverter(new Callback<ButtonType, Category>() {
            @Override
            public Category call(ButtonType param) {
                if(param == ButtonType.OK){
                    TreeItem<Index> item = treeView.getSelectionModel().getSelectedItem();
                    if(item != null){
                        Index index = item.getValue();
                        String type = index.getType();
                        if(Index.TYPE_CATEGORY.equals(type)){
                            return (Category) index.getIndexObject();
                        }else if(Index.TYPE_THINGMANAGER.equals(type)){
                            return ((ThingManager) index.getIndexObject()).getCategory("");
                        }
                    }
                }

                return null;
            }
        });
    }
}
