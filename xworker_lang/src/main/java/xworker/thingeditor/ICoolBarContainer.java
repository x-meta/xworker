package xworker.thingeditor;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public interface ICoolBarContainer {
    void setEditorCoolBar(Thing menuConfig, ActionContext actionContext);

    void removeEditorCoolItem(Thing menuConfig, ActionContext actionContext);
}
