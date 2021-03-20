package xworker.javafx.thing.model;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

import java.io.File;

public class FileTreeViewModel {
    File fileRoot;
    TreeView<File> treeView;

    public FileTreeViewModel(TreeView<File> treeView, File fileRoot, boolean showRoot){
        this.treeView = treeView;
        this.fileRoot = fileRoot;

        treeView.setShowRoot(showRoot);
        treeView.setCellFactory(new Callback<TreeView<File>, TreeCell<File>>() {
            @Override
            public TreeCell<File> call(TreeView<File> param) {
                return new TextFieldTreeCell<File>(){
                    @Override
                    public void updateItem(File item, boolean empty) {
                        super.updateItem(item, empty);

                        if(item == null){
                            return;
                        }
                        this.setText(item.getName());
                        Image image = JavaFXUtils.getImage(item);
                        if(image != null){
                            this.setGraphic(new ImageView(image));
                        }
                    }
                } ;
            }
        });
        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<File>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<File>> observable, TreeItem<File> oldValue, TreeItem<File> newValue) {
                if(newValue == null){
                    return;
                }

                File file = newValue.getValue();
                if(file.isDirectory() && newValue.getChildren().size() == 0){
                    try {
                        for (File child : file.listFiles()) {
                            newValue.getChildren().add(new TreeItem<>(child));
                        }
                        newValue.setExpanded(true);
                    }catch (Exception e){

                    }
                }
            }
        });

        treeView.setRoot(new TreeItem<>(fileRoot));
    }

    public static void create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TreeView<File> parent = actionContext.getObject("parent");

        File file = self.doAction("getFile", actionContext);
        if(file != null ){
            new FileTreeViewModel(parent, file, self.getBoolean("showRoot"));
        }

    }
}
