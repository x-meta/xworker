package xworker.javafx.thing.thingregist;

import javafx.scene.Node;
import javafx.scene.web.WebView;
import org.xmeta.ActionContext;
import xworker.content.Content;
import xworker.content.StringContent;

public class UrlContentViewFactory implements ContentViewFactory {
    @Override
    public Node createContentNode(Content<?> content, ActionContext actionContext) {
        WebView webView = new WebView();
        webView.getEngine().load(((StringContent) content).getContent());
        return webView;
    }
}
