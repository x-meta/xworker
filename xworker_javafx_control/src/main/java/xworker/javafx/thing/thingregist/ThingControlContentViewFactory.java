package xworker.javafx.thing.thingregist;

import javafx.scene.Node;
import javafx.scene.web.WebView;
import org.xmeta.ActionContext;
import xworker.content.Content;
import xworker.content.ThingContent;
import xworker.util.XWorkerUtils;

public class ThingControlContentViewFactory implements ContentViewFactory{
    @Override
    public Node createContentNode(Content<?> content, ActionContext actionContext) {
        ThingContent thingContent = (ThingContent) content;
        WebView webView = new WebView();
        if(XWorkerUtils.hasWebServer()){
            webView.getEngine().load(XWorkerUtils.getWebControlUrl(thingContent.getContent()));
        }else{
            webView.getEngine().loadContent("WebServer not started!");
        }
        return webView;
    }
}
