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
import xworker.util.ThingGroup;

public class ThingGroupTreeCellFactory implements Callback<TreeView<ThingGroup>, TreeCell<ThingGroup>> {
    public static ThingGroupTreeCellFactory INSTANCE = new ThingGroupTreeCellFactory();

    @Override
    public TreeCell<ThingGroup> call(TreeView<ThingGroup> param) {
        return new TextFieldTreeCell<ThingGroup>() {
            @Override
            public void updateItem(ThingGroup item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null) {
                    return;
                }

                if(item.getThing() == null){
                    this.setText(item.getGroup());

                    this.setGraphic(new ImageView(JavaFXUtils.getImage("icons/folder.png")));
                }else{
                    this.setText(item.getThing().getMetadata().getLabel());

                    String icon = item.getThing().getStringBlankAsNull("icon");
                    if (icon == null) {
                        for (Thing descriptor : item.getThing().getAllDescriptors()) {
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


            }
        };
    }

    public static ThingGroupTreeCellFactory create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ThingGroupTreeCellFactory factory = ThingGroupTreeCellFactory.INSTANCE;
        actionContext.g().put(self.getMetadata().getName(), factory);

        Object parent = actionContext.getObject("parent");
        if(parent instanceof  TreeView){
            ((TreeView) parent).setCellFactory(factory);
        }
        return factory;
    }
}
