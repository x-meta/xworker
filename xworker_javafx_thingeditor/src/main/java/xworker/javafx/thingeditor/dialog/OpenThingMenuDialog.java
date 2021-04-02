package xworker.javafx.thingeditor.dialog;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionField;
import xworker.javafx.util.JavaFXUtils;
import xworker.thingeditor.ThingMenu;
import xworker.util.XWorkerUtils;

@ActionClass
public class OpenThingMenuDialog {
    @ActionField
    public javafx.scene.control.TreeView<ThingMenu> menuTreeView;
    @ActionField
    public javafx.stage.Stage stage;
    @ActionField
    ActionContext actionContext;

    public void init(){
        menuTreeView.setShowRoot(false);
        menuTreeView.setCellFactory(new Callback<TreeView<ThingMenu>, TreeCell<ThingMenu>>() {
            @Override
            public TreeCell<ThingMenu> call(TreeView<ThingMenu> param) {
                return new TextFieldTreeCell<ThingMenu>(){
                    @Override
                    public void updateItem(ThingMenu item, boolean empty) {
                        super.updateItem(item, empty);

                        if(item == null || empty){
                            return;
                        }

                        setText(item.getLabel());
                        Image image = JavaFXUtils.getImage(item.getThing());
                        if(image != null){
                            setGraphic(new ImageView(image));
                        }
                    }
                };
            }
        });

        ThingMenu menu = actionContext.getObject("menu");
        ThingMenu root = menu.getRoot();
        TreeItem<ThingMenu> rootItem = new TreeItem<>(root);
        menuTreeView.setRoot(rootItem);

        for(ThingMenu childMenu : root.getChilds()){
            initItems(rootItem, childMenu);
        }
    }

    private void initItems(TreeItem<ThingMenu> parentItem, ThingMenu thingMenu){
        TreeItem<ThingMenu> item = new TreeItem<>((thingMenu));
        parentItem.getChildren().add(item);

        for(ThingMenu childMenu : thingMenu.getChilds()){
            initItems(item, childMenu);
        }
        parentItem.setExpanded(true);
    }

    public void open(){
        TreeItem<ThingMenu> menu = menuTreeView.getSelectionModel().getSelectedItem();
        if(menu != null){
            XWorkerUtils.ideOpenThing(menu.getValue().getThing());
        }

        stage.close();
    }

    public void close(){
        stage.close();
    }

}
