package xworker.javafx.cell;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class ThingTreeCellFactory implements Callback<TreeView<Thing>, TreeCell<Thing>> {
    public static ThingTreeCellFactory INSTANCE = new ThingTreeCellFactory();

    @Override
    public TreeCell<Thing> call(TreeView<Thing> param) {
        return new TextFieldTreeCell<Thing>() {
            @Override
            public void updateItem(Thing item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null) {
                    return;
                }

                this.setText(item.getMetadata().getLabel());
                String icon = item.getStringBlankAsNull("icon");
                if (icon == null) {
                    for (Thing descriptor : item.getAllDescriptors()) {
                        //println descriptor.getMetadata().getPath();
                        icon = descriptor.getString("icon");
                        //println icon;
                        if (icon != null && !"".equals(icon)) {
                            break;
                        }
                    }
                }
                if (icon != null) {
                    Image image = JavaFXUtils.getImage(icon);
                    if (image != null) {
                        this.setGraphic(new ImageView(image));
                    } else {
                        this.setGraphic(new ImageView(JavaFXUtils.getImage("icons/page_white.png")));
                    }
                }
            }
        };
    }

    public static ThingTreeCellFactory create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ThingTreeCellFactory factory = ThingTreeCellFactory.INSTANCE;
        actionContext.g().put(self.getMetadata().getName(), factory);

        Object parent = actionContext.getObject("parent");
        if(parent instanceof  TreeView){
            ((TreeView) parent).setCellFactory(factory);
        }
        return factory;
    }
}

