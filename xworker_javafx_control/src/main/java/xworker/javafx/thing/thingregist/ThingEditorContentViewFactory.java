package xworker.javafx.thing.thingregist;

import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.xmeta.ActionContext;
import xworker.content.Content;
import xworker.content.ThingContent;
import xworker.javafx.thing.editor.ThingEditor;

public class ThingEditorContentViewFactory implements ContentViewFactory{
    @Override
    public Node createContentNode(Content<?> content, ActionContext actionContext) {
        ThingContent thingContent = (ThingContent) content;
        ThingEditor thingEditor = new ThingEditor();
        thingEditor.setThing(thingContent.getContent());

        VBox.setVgrow(thingEditor.getThingEditorNode(), Priority.ALWAYS);
        return thingEditor.getThingEditorNode();
    }
}
