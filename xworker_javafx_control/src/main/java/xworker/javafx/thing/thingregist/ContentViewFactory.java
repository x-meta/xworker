package xworker.javafx.thing.thingregist;

import javafx.scene.Node;
import org.xmeta.ActionContext;
import xworker.content.Content;

public interface ContentViewFactory {
    public Node createContentNode(Content<?> content, ActionContext actionContext);
}
