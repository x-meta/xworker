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

public class ThingTreeModel {
    Thing thing;
    TreeView<Thing> treeView;

    public ThingTreeModel(TreeView<Thing> treeView, Thing thing, boolean showRoot, boolean dynamic){
        this.thing = thing;
        this.treeView = treeView;

        treeView.setShowRoot(showRoot);
        treeView.setCellFactory(new Callback<TreeView<Thing>, TreeCell<Thing>>() {
            @Override
            public TreeCell<Thing> call(TreeView<Thing> param) {
                return new TextFieldTreeCell<Thing>(){
                    @Override
                    public void updateItem(Thing item, boolean empty) {
                        super.updateItem(item, empty);

                        if(item == null){
                            return;
                        }

                        this.setText(item.getMetadata().getLabel());
                        String icon = item.getStringBlankAsNull("icon");
                        if(icon == null){
                            for(Thing descriptor : item.getAllDescriptors()){
                                //println descriptor.getMetadata().getPath();
                                icon = descriptor.getString("icon");
                                //println icon;
                                if(icon != null && !"".equals(icon)){
                                    break;
                                }
                            }
                        }
                        if(icon != null){
                            Image image = JavaFXUtils.getImage(icon);
                            if(image != null){
                                this.setGraphic(new ImageView(image));
                            }else{
                                this.setGraphic(new ImageView(JavaFXUtils.getImage("icons/page_white.png")));
                            }
                        }
                    }
                };
            }
        });

        if(dynamic) {
            treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Thing>>() {
                @Override
                public void changed(ObservableValue<? extends TreeItem<Thing>> observable, TreeItem<Thing> oldValue, TreeItem<Thing> newValue) {
                    if (newValue == null) {
                        return;
                    }

                    Thing thing = newValue.getValue();
                    if (thing.getChilds().size() > 0 && newValue.getChildren().size() == 0) {
                        for (Thing child : thing.getChilds()) {
                            newValue.getChildren().add(new TreeItem<>(child));
                        }
                        newValue.setExpanded(true);
                    }
                }
            });
        }

        TreeItem<Thing> rootItem = new TreeItem<>(thing);
        if(!dynamic){
            for(Thing child : thing.getChilds()){
                createItem(rootItem, child);
            }
        }
        treeView.setRoot(rootItem);
    }

    public static void createItem(TreeItem<Thing> parentItem, Thing thing){
        TreeItem<Thing> item = new TreeItem<>(thing);
        parentItem.getChildren().add(item);

        for(Thing child : thing.getChilds()){
            createItem(item, child);
        }
    }
    public static void create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TreeView<Thing> parent = actionContext.getObject("parent");
        boolean showRoot = self.getBoolean("showRoot");
        Thing thing = self.doAction("getThing", actionContext);
        if(thing != null){
            new ThingTreeModel(parent, thing, showRoot, self.getBoolean("dynamic"));
        }
    }
}
