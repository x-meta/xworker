package xworker.javafx.thing.thingregist;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.content.Content;
import xworker.content.ThingContent;


public class GroovyContentViewFactory implements ContentViewFactory{
    @Override
    public Node createContentNode(Content<?> content, ActionContext actionContext) {
        final ThingContent thingContent = (ThingContent) content;

        VBox vBox = new VBox();
        TextArea textArea = new TextArea();
        String code = thingContent.getContent().getStringBlankAsNull("code");
        if(code != null){
            textArea.setText(code);
        }
        VBox.setVgrow(textArea, Priority.ALWAYS);
        vBox.getChildren().add(textArea);

        ButtonBar buttonBar = new ButtonBar();
        vBox.getChildren().add(buttonBar);

        Button button = new Button("Run");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Thing thing = new Thing("xworker.lang.actions.GroovyAction");
                    thing.set("name", thingContent.getContent().getMetadata().getName());
                    thing.getMetadata().setPath(thingContent.getContent().getMetadata().getPath());
                    thing.set("code", textArea.getText());
                    thing.getAction().run(actionContext);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        buttonBar.getButtons().add(button);

        return vBox;
    }
}
