package xworker.javafx.thing.thingregist;

import javafx.scene.Node;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.content.Content;
import xworker.content.ThingRegistContent;
import xworker.javafx.thing.ThingRegistView;

public class ThingRegistContentViewFactory implements ContentViewFactory{
    @Override
    public Node createContentNode(Content<?> content, ActionContext actionContext) {
        ThingRegistContent thingRegistContent = (ThingRegistContent) content;

        Thing registThing = new Thing("xworker.javafx.thing.regist.ThingRegistView");
        registThing.put("registThing", thingRegistContent.getContent());
        registThing.put("registType", thingRegistContent.getRegistType());
        registThing.put("displayMethod", thingRegistContent.getContentDisplayType());

        ThingRegistView thingRegistView = new ThingRegistView(registThing, actionContext);
        return thingRegistView.getRootNode();
    }
}
