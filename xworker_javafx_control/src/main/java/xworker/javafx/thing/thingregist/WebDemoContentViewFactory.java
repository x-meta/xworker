package xworker.javafx.thing.thingregist;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebView;
import org.xmeta.ActionContext;
import xworker.content.Content;
import xworker.content.ThingContent;
import xworker.javafx.thing.editor.ThingEditor;
import xworker.util.XWorkerUtils;

public class WebDemoContentViewFactory implements ContentViewFactory{
    @Override
    public Node createContentNode(Content<?> content, ActionContext actionContext) {
        ThingContent thingContent = (ThingContent) content;
        TabPane tabPane = new TabPane();

        Tab webTab = new Tab("Web");
        WebView webView = new WebView();
        if(XWorkerUtils.hasWebServer()){
            webView.getEngine().load(XWorkerUtils.getWebControlUrl(thingContent.getContent()));
        }else{
            webView.getEngine().loadContent("WebServer not started!");
        }
        webTab.setContent(webView);

        Tab editorTab = new Tab(thingContent.getContent().getMetadata().getLabel());
        editorTab.setClosable(false);
        ThingEditor thingEditor = new ThingEditor();
        thingEditor.setThing(thingContent.getContent());
        editorTab.setContent(thingEditor.getThingEditorNode());

        tabPane.getTabs().addAll(webTab, editorTab);

        return tabPane;
    }
}
