package xworker.javafx.thing.model;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Index;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class IndexTreeViewModel {
    TreeView<Index> treeView;
    Index root;
    boolean showThings = false;

    public IndexTreeViewModel(TreeView<Index> treeView, Index root, boolean showRoot){
        this.treeView = treeView;
        this.root = root;
        this.treeView.setShowRoot(showRoot);
        if(root == Index.getInstance()){
            this.treeView.setShowRoot(false);
        }

        treeView.setCellFactory(new Callback<TreeView<Index>, TreeCell<Index>>() {
            @Override
            public TreeCell<Index> call(TreeView<Index> param) {
                return new TextFieldTreeCell<Index>(){
                    @Override
                    public void updateItem(Index item, boolean empty) {
                        super.updateItem(item, empty);

                        if(item == null){
                            return;
                        }

                        this.setText(item.getLabel());

                        Image image = IndexUtil.getImage(item);
                        if(image != null){
                            this.setGraphic(new ImageView(image));
                        }
                    }
                };
            }
        });

        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Index>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<Index>> observable, TreeItem<Index> oldValue, TreeItem<Index> newValue) {
                if(newValue == null){
                    return;
                }
                Index index = newValue.getValue();
                if(newValue.getChildren().size() == 0){
                    for(Index child : index.getChilds()){
                        if(!showThings && Index.TYPE_THING.equals(child.getType())){
                            continue;
                        }

                        TreeItem<Index> childItem = new TreeItem<>();
                        childItem.setValue(child);
                        newValue.getChildren().add(childItem);
                    }
                    newValue.setExpanded(true);
                }
            }
        });

        TreeItem<Index> rootItem = new TreeItem<Index>();
        rootItem.setValue(root);
        for(Index childIndex : root.getChilds()){
            rootItem.getChildren().add(new TreeItem<>(childIndex));
        }
        this.treeView.setRoot(rootItem);
    }

    public static void create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TreeView<Index> parent = actionContext.getObject("parent");
        IndexTreeViewModel model = new IndexTreeViewModel(parent, Index.getInstance(), false);
        if(self.getBoolean("showThings")){
            model.showThings = true;
        }
    }

}
