package xworker.content;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public interface ContentHandler {
    public Object handle(Thing quickContent, Content<?> content, ActionContext actionContext);
}
