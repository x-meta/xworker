package xworker.javafx.thing.thingregist;

import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import org.xmeta.ActionContext;
import xworker.content.Content;
import xworker.content.ThingContent;
import xworker.javafx.util.JavaFXUtils;

public class ThingDescContentViewFactory implements ContentViewFactory{
    @Override
    public Node createContentNode(Content<?> content, ActionContext actionContext) {
        ThingContent thingContent = (ThingContent) content;
        WebView webView = new WebView();
        JavaFXUtils.showThingDesc(thingContent.getContent(), webView);

        VBox.setVgrow(webView, Priority.ALWAYS);
        return webView;
    }
}
