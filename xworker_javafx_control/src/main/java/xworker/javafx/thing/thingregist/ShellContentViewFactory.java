package xworker.javafx.thing.thingregist;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import org.xmeta.ActionContext;
import org.xmeta.World;
import xworker.content.Content;
import xworker.content.ThingContent;

public class ShellContentViewFactory implements ContentViewFactory{
    @Override
    public Node createContentNode(Content<?> content, ActionContext actionContext) {
        final ThingContent thingContent = (ThingContent) content;
        VBox vBox = new VBox();
        Button button =new Button("Open");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(World.getInstance().getThing("xworker.swt.widgets.Shell") == null){
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Need xworker_swt!", ButtonType.OK);
                    alert.show();
                    return;
                }

                ActionContext ac = new ActionContext();
                ac.put("parentContext", actionContext);
                thingContent.getContent().doAction("run", ac);
            }
        });
        vBox.getChildren().add(button);
        return vBox;
    }
}
