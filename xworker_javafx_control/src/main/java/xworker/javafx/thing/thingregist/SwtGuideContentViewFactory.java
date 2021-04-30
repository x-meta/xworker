package xworker.javafx.thing.thingregist;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.content.Content;
import xworker.content.ThingContent;

public class SwtGuideContentViewFactory implements ContentViewFactory{
    @Override
    public Node createContentNode(Content<?> content, final ActionContext actionContext) {
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

                Thing shell = new Thing("xworker.swt.widgets.Shell");
                shell.put("text", thingContent.getContent().getMetadata().getLabel());
                shell.addChild(new Thing("xworker.swt.layout.FillLayout"));
                Thing thing = new Thing("xworker.swt.guide.GuideComposite");
                thing.put("guide", thingContent.getContent().getMetadata().getPath());
                shell.addChild(thing);
                ActionContext ac = new ActionContext();
                ac.put("parentContext", actionContext);
                ac.put("content", thingContent.getContent());
                shell.doAction("run", ac);
            }
        });
        vBox.getChildren().add(button);
        return vBox;
    }
}
