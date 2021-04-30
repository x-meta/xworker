package xworker.javafx.thing.thingregist;

import javafx.scene.Node;
import javafx.scene.web.WebView;
import org.xmeta.ActionContext;
import xworker.content.Content;
import xworker.content.ThingContent;

public class HtmlContentViewFactory implements ContentViewFactory{
    @Override
    public Node createContentNode(Content<?> content, ActionContext actionContext) {
        ThingContent thingContent = (ThingContent) content;
        WebView webView = new WebView();
        webView.getEngine().loadContent(thingContent.getContent().getString("code"));
        return webView;
    }
}
