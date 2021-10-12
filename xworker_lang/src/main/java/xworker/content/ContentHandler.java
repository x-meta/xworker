package xworker.content;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public interface ContentHandler {
    Object handle(Thing quickContent, Content<?> content, ActionContext actionContext);

    void setUrl(String url);

    void setThingDesc(Thing thing);

    void setText(String text);
}
