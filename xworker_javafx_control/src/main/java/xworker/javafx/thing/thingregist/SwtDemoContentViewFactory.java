package xworker.javafx.thing.thingregist;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.content.Content;
import xworker.content.ThingContent;
import xworker.javafx.thing.editor.ThingEditor;

public class SwtDemoContentViewFactory implements ContentViewFactory{
    @Override
    public Node createContentNode(Content<?> content, ActionContext actionContext) {
        final ThingContent thingContent = (ThingContent) content;
        TabPane tabPane = new TabPane();
        Tab swtTab = new Tab("SWT");
        swtTab.setClosable(false);
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
                shell.addChild(thingContent.getContent(), false);
                ActionContext ac = new ActionContext();
                ac.put("parentContext", actionContext);
                shell.doAction("run", ac);
            }
        });
        vBox.getChildren().add(button);
        swtTab.setContent(vBox);

        Tab editorTab = new Tab(thingContent.getContent().getMetadata().getLabel());
        editorTab.setClosable(false);
        ThingEditor thingEditor = new ThingEditor();
        thingEditor.setThing(thingContent.getContent());
        editorTab.setContent(thingEditor.getThingEditorNode());

        tabPane.getTabs().addAll(swtTab, editorTab);
        return tabPane;
    }
}
